package com.ellirion.buildframework.util;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldHelper {

    private static final BlockingQueue<PendingBlockChange> PENDING = new LinkedBlockingQueue<>();

    /**
     * Safely set a block in the world at the given coordinates to the given material and metadata.
     * @param world The world to set the block in
     * @param x The X coordinate of the block
     * @param y The Y coordinate of the block
     * @param z The Z coordinate of the block
     * @param mat The Material of the block
     * @param meta The metadata of the block
     * @return A {@link BlockChangeTransaction} that has been applied
     */
    public static Transaction setBlock(World world, int x, int y, int z, Material mat, byte meta) {
        return setBlock(new Location(world, x, y, z), mat, meta);
    }

    /**
     * Safely set a block in the world at the given location to the given material and metadata.
     * @param loc The Location of the block
     * @param mat The Material of the block
     * @param meta The metadata of the block
     * @return A {@link BlockChangeTransaction} that has been applied
     */
    public static Transaction setBlock(Location loc, Material mat, byte meta) {
        Transaction t = new BlockChangeTransaction(new BlockChange(loc, mat, meta));
        t.apply();
        return t;
    }

    /**
     * Safely set a block in the world at the given location to the given material, metadata and nbtdata.
     * @param loc The Location of the block
     * @param mat The Material of the block
     * @param data The metadata of the block
     * @param nbt The nbtdata of the block
     * @return A {@link BlockChangeTransaction} that has been applied
     */
    public static Transaction setBlock(Location loc, Material mat, byte data, NBTTagCompound nbt) {
        Transaction t = new BlockChangeTransaction(new BlockChange(loc, mat, data, nbt));
        t.apply();
        return t;
    }

    /**
     * Safely set a block in the world at the given coordinates to the given material, metadata and nbtdata.
     * @param world The world to set the block in
     * @param x The X coordinate of the block
     * @param y The Y coordinate of the block
     * @param z The Z coordinate of the block
     * @param mat The Material of the block
     * @param meta The metadata of the block
     * @param nbt The nbtdata of the Block
     * @return A {@link BlockChangeTransaction} that has been applied
     */
    public static Transaction setBlock(World world, int x, int y, int z, Material mat, byte meta, NBTTagCompound nbt) {
        return setBlock(new Location(world, x, y, z), mat, meta, nbt);
    }

    /**
     * Safely get a block from the world at the given coordinates.
     * @param world The World to get the block from
     * @param x The X coordinate of the block
     * @param y The Y coordinate of the block
     * @param z The Z coordinate of the block
     * @return The Block
     */
    public static Block getBlock(World world, int x, int y, int z) {
        // Load chunk if necessary
        int chunkX = Math.floorDiv(x, 16);
        int chunkZ = Math.floorDiv(z, 16);
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            Promise p = new Promise<>(finisher -> {
                world.loadChunk(chunkX, chunkZ);
                finisher.resolve(null);
            }, false);

            p.await();
        }

        // Get the block
        return world.getBlockAt(x, y, z);
    }

    /**
     * Safely get a block from the world at the given location.
     * @param loc The Location of the block
     * @return The Block
     */
    public static Block getBlock(Location loc) {
        return getBlock(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private static Promise<BlockChange> scheduleSetBlock(BlockChange change) {
        PendingBlockChange pending = new PendingBlockChange(change);
        PENDING.add(pending);
        return pending.promise;
    }

    /**
     * Run scheduled block changes.
     */
    public static void run() {
        // 125 is the "magic value" of block updates that can be applied
        // per tick without having a noticeable performance impact.
        for (int i = 0; i < 125; i++) {
            PendingBlockChange pending = PENDING.poll();
            if (pending == null) {
                return;
            }

            pending.apply();
        }
    }

    private static class BlockChange {

        private Location location;
        private Material material;
        private byte data;
        private NBTTagCompound nbt;

        BlockChange(final Location loc, final Material mat, final byte data) {
            this(loc, mat, data, null);
        }

        BlockChange(final Location loc, final Material mat, final byte data, final NBTTagCompound nbt) {
            this.location = loc;
            this.material = mat;
            this.data = data;
            this.nbt = nbt;
        }

        BlockChange apply() {
            // Note what the current block state was so we can revert back to it.
            Block block = getBlock(location);
            BlockChange change;

            TileEntity te = ((CraftWorld) location.getWorld()).getTileEntityAt(location.getBlockX(),
                                                                               location.getBlockY(),
                                                                               location.getBlockZ());
            if (te != null) {
                change = new BlockChange(location, block.getType(), block.getData(), te.save(new NBTTagCompound()));
                te.load(nbt);
            } else {
                change = new BlockChange(location, block.getType(), block.getData());
            }

            // Apply the changes we were supposed to make.
            block.setType(material);
            block.setData(data);

            // Return the BlockChange to be used for reverting.
            return change;
        }
    }

    private static class PendingBlockChange {

        private BlockChange change;
        private Promise<BlockChange> promise;

        PendingBlockChange(final BlockChange change) {
            this.change = change;
            this.promise = new Promise<>();
        }

        BlockChange apply() {
            BlockChange previous = change.apply();
            promise.getFinisher().resolve(previous);
            return previous;
        }
    }

    private static class BlockChangeTransaction extends Transaction {

        private BlockChange before;
        private BlockChange after;

        BlockChangeTransaction(final BlockChange change) {
            this.before = null;
            this.after = change;
        }

        @Override
        protected Promise<Boolean> applier() {
            return scheduleSetBlock(after).then(change -> {
                before = change;
                return true;
            });
        }

        @Override
        protected Promise<Boolean> reverter() {
            return scheduleSetBlock(before).then(change -> true);
        }
    }
}
