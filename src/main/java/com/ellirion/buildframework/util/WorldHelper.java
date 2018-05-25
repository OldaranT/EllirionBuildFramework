package com.ellirion.buildframework.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BlockChange;

import java.util.ArrayList;
import java.util.LinkedList;

public class WorldHelper {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
    private static boolean STARTED = false;
    private static final int MAX_SIZE = 1000;
    private static final LinkedList<ArrayList<BlockChange>> QUEUE = new LinkedList<>();

    /**
     * get the blocks in the world using coordinates in using the current thread and if that cant be done us a promise.
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
     * get the blocks in the world using a location in using the current thread and if that cant be done us a promise.
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
        ArrayList<BlockChange> list = findFirstNotFullArray();
        list.add(change);
        startExecutingIfNotStarted();
    }

    /**
     * Add a list of Blocks to the queue that is used to changed.
     * @param changes the list of block changes that need to be executed
     */
    public static void queueBlockChange(ArrayList<BlockChange> changes) {
        ArrayList<BlockChange> list = findFirstNotFullArray();

        if (list.size() + changes.size() <= MAX_SIZE) {
            list.addAll(changes);
            startExecutingIfNotStarted();
            return;
        }

        QUEUE.addLast((ArrayList<BlockChange>) changes.subList(0, MAX_SIZE - 1));
        ArrayList<BlockChange> iteratingList = (ArrayList<BlockChange>) changes.subList(MAX_SIZE - 1,
                                                                                        changes.size() - 1);

        while (iteratingList.size() > MAX_SIZE) {
            QUEUE.add((ArrayList<BlockChange>) iteratingList.subList(0, MAX_SIZE - 1));
            iteratingList = (ArrayList<BlockChange>) iteratingList.subList(MAX_SIZE - 1, iteratingList.size() - 1);
        }

        startExecutingIfNotStarted();
        QUEUE.add((ArrayList<BlockChange>) iteratingList.subList(0, MAX_SIZE - 1));
    }

    private static ArrayList<BlockChange> findFirstNotFullArray() {
        for (ArrayList<BlockChange> blockChanges : QUEUE) {
            if (blockChanges.size() < MAX_SIZE) {
                return blockChanges;
            }
        }
        QUEUE.addLast(new ArrayList<>());
        return QUEUE.getLast();
    }

    private static void startExecutingIfNotStarted() {
        if (!STARTED) {
            executeChanges();
            STARTED = true;
        }
    }

    private static void executeChanges() {
        new Promise<>(finisher -> {
            ArrayList<BlockChange> changes = QUEUE.pollFirst();
            if (changes == null) {
                finisher.resolve(null);
                return;
            }

            for (BlockChange blockChange : changes) {
                Block toChange = blockChange.getLocation().getWorld().getBlockAt(blockChange.getLocation());
                toChange.setType(blockChange.getMatAfter());
                toChange.setData(blockChange.getMetadataAfter());
            }
            finisher.resolve(null);
        }, false).consumeAsync(next -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                //
            }
            executeChanges();
        });
    }
}
