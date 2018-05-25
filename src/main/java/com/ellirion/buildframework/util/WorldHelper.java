package com.ellirion.buildframework.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BlockChange;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldHelper {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
    private static boolean STARTED = false;
    private static final Object LOCK = new Object();
    private static final int THROTTLE = BuildFramework.getInstance().getConfig().getInt(
            "WorldHelper.MaximumAmountOfBlocks", 100);
    private static final BlockingQueue<BlockChange> QUEUE = new LinkedBlockingQueue<>();

    /**
     * get the block at the given coordinates and load the chunk using a synchronous promise if necessary.
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
     * get the block at the given location and load the chunk using a synchronous promise if necessary.
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
     */

    public static void queueBlockChange(BlockChange change) {
        QUEUE.add(change);
        startExecutingIfNotStarted();
    }

    /**
     * Add a list of Blocks to the queue that is used to changed.
     * @param changes the list of block changes that need to be executed
     */
    public static void queueBlockChange(ArrayList<BlockChange> changes) {
        QUEUE.addAll(changes);
        startExecutingIfNotStarted();
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
                } catch (Exception ex) {
                }
            }
            finisher.resolve(null);
        }, true).consumeSync(next -> {
            synchronized (LOCK) {
                STARTED = false;
            }
        });
    }

    private static void sendUpdates(BlockChange change) {
        new Promise<>(finisher -> {
            Block block = change.getLocation().getWorld().getBlockAt(change.getLocation());
            block.setType(change.getMatAfter());
            block.setData(change.getMetadataAfter());
            finisher.resolve(null);
        }, false);
    }
}
