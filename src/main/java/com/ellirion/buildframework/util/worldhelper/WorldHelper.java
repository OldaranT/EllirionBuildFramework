package com.ellirion.buildframework.util.worldhelper;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.util.async.Promise;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldHelper {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
    private static boolean STARTED = false;
    private static final Object LOCK = new Object();
    private static final int THROTTLE = BuildFramework.getInstance().getConfig().getInt(
            "WorldHelper.MaximumAmountOfBlocks", 1000);
    private static final BlockingQueue<PendingBlockChange> QUEUE = new LinkedBlockingQueue<>();

    /**
     * Get the block at the given coordinates and load the chunk using a synchronous promise if necessary.
     * @param world the world
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block at the specified coordinates
     */
    public static Block getBlockAt(final World world, final int x, final int y, final int z) {
        loadChunk(world, x, z);
        return world.getBlockAt(x, y, z);
    }

    /**
     * Get the block at the given location and load the chunk using a synchronous promise if necessary.
     * @param location the location of the block
     * @return the block at the given location
     */
    public static Block getBlockAt(Location location) {
        return getBlockAt(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private static void loadChunk(World world, int x, int z) {
        int chunkX = Math.floorDiv(x, 16);
        int chunkZ = Math.floorDiv(z, 16);

        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            Promise p = new Promise<>(finisher -> {
                world.loadChunk(chunkX, chunkZ);
                finisher.resolve(null);
            }, false);

            p.await();
        }
    }

    /**
     * Add blocks to the queue that is used to change the world.
     * @param change the block change that needs to be executed
     * @return the promise that the block will be changed
     */

    public static Promise<Boolean> queueBlockChange(BlockChange change) {
        PendingBlockChange pending = new PendingBlockChange(change);

        return new Promise<>(finisher -> {
            // Pass the finisher along so the Promise can be resolved once the change has been executed
            pending.setFinisher(finisher);
            QUEUE.add(pending);
            startExecutingIfNotStarted();
        });
    }


    private static void startExecutingIfNotStarted() {
        synchronized (LOCK) {
            if (!STARTED) {
                executeChanges();
                STARTED = true;
            }
        }
    }

    private static void executeChanges() {
        new Promise<>(finisher -> {
            long startTime = System.currentTimeMillis();
            long wantVisited;
            long haveVisited = 0;

            while (!QUEUE.isEmpty()) {
                wantVisited = THROTTLE * (System.currentTimeMillis() - startTime) / 1000;
                sendUpdates(QUEUE.poll());
                haveVisited++;

                long deltaVisited = haveVisited - wantVisited;

                // Keep our pace correct (throttle)
                try {
                    Thread.sleep((deltaVisited / THROTTLE) * 1000);
                } catch (InterruptedException ignore) {
                }
            }
            finisher.resolve(null);
        }, true).then(next -> {
            synchronized (LOCK) {
                STARTED = false;
            }
        }, true);
    }

    private static void sendUpdates(PendingBlockChange pendingChange) {
        new Promise<>(finisher -> {

            // Execute the change
            BlockChange change = pendingChange.getChange();
            Block block = change.getLocation().getWorld().getBlockAt(change.getLocation());
            block.setType(change.getMaterial());
            block.setData(change.getMetaData());

            // Notify the promise that the change has been executed
            pendingChange.getFinisher().resolve(true);

            finisher.resolve(null);
        }, false);
    }
}
