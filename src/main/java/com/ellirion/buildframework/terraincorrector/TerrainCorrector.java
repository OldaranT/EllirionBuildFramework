package com.ellirion.buildframework.terraincorrector;

import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.List;

public class TerrainCorrector {

    /**
     * @param boundingBox the BoundingBox that will be used for terrain smoothing
     * @param world The world in which
     * @return whether the smoothing succeeded
     */
    public boolean correctTerrain(BoundingBox boundingBox, World world) {
        List<Hole> holes = findHoles(boundingBox, world);

        // checken op blockers (river eg.)

        // juiste manier van vullen aanroepen
        return true;
    }

    private List<Hole> findHoles(BoundingBox boundingBox, World world) {
        List<Hole> holes = new ArrayList<>();
        int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.isEmpty() || block.isLiquid()) && holes.stream().noneMatch(hole -> hole.contains(block))) {
                    // create new hole
                    Hole hole = new Hole(block);

                    // find ajecent blocks (different function)
                }
            }
        }
        return holes;
    }

    private boolean checkForRiver(BoundingBox boundingBox, World world) {
        int y = boundingBox.getY1() - 1;
        int x1 = boundingBox.getX1();
        int x2 = boundingBox.getX2();
        int z1 = boundingBox.getZ1();
        int z2 = boundingBox.getZ2();

        // check from corner x1 z1 to corner x1 z2.
        for (int z = z1; z <= z2; z++) {
            Block block = world.getBlockAt(x1, y, z);
            if (block.isLiquid()) {
                Block outsideBlock = world.getBlockAt(x1 - 1, y, z);
                if (outsideBlock.isLiquid()) {
                    return true;
                }
            }
        }
        // check from corner x1 z2 to corner x2 z2.
        for (int x = x1; x <= x2; x++) {
            Block block = world.getBlockAt(x, y, z2);
            if (block.isLiquid()) {
                Block outsideBlock = world.getBlockAt(x, y, z2 + 1);
                if (outsideBlock.isLiquid()) {
                    return true;
                }
            }
        }

        // check from corner x2 z2 to corner x2 z1.
        for (int z = z2; z >= z1; z--) {
            Block block = world.getBlockAt(x2, y, z);
            if (block.isLiquid()) {
                Block outsideBlock = world.getBlockAt(x2 + 1, y, z);
                if (outsideBlock.isLiquid()) {
                    return true;
                }
            }
        }

        // check from corner x2 z1 to corner x1 z1.
        for (int x = x2; x >= x1; x--) {
            Block block = world.getBlockAt(x, y, z1);
            if (block.isLiquid()) {
                Block outsideBlock = world.getBlockAt(x, y, z1 - 1);
                if (outsideBlock.isLiquid()) {
                    return true;
                }
            }
        }

        return false;
    }

    private int calculateDepth(final Block block) {
        final World world = block.getWorld();
        final int blockX = block.getX();
        final int blockZ = block.getZ();
        int depth = 0;
        for (int y = block.getY(); y >= 0; y--) {
            Block nextBlock = world.getBlockAt(blockX, y, blockZ);
            if (!nextBlock.isEmpty() && !nextBlock.isLiquid()) {
                break;
            }
            depth++;
        }
        return depth;
    }
}
