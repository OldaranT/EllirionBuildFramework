package com.ellirion.buildframework.terraincorrector.util;

import org.bukkit.World;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HoleUtil {

    /**
     * @param world the world.
     * @param boundingBox the boundingbox
     * @param offset the offset for how far around the boundingbox you can go.
     * @param depthOffset the offset for how deep you can go.
     * @return return the found holes.
     */
    public static List<Hole> findHoles(World world, BoundingBox boundingBox, int offset, int depthOffset) {
        List<Hole> holes = new ArrayList<>();
        int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.isEmpty() || block.isLiquid() || !block.getType().isSolid()) &&
                    holes.stream().noneMatch(hole -> hole.contains(block))) {

                    // Add the hole to the list of holes once all blocks are found
                    holes.add(getHole(block, offset, boundingBox, depthOffset, world));
                }
            }
        }
        return holes;
    }

    private static Hole getHole(Block block, int offset, BoundingBox boundingBox, int depthOffset, World world) {
        Hole hole = new Hole(block);
        LinkedList<Block> todoBlocks = new LinkedList<>();

        //Add the current block to the todoBlocks of blocks that are yet to be done
        todoBlocks.add(block);

        //Execute this method for each block in the todoBlocks
        while ((block = todoBlocks.poll()) != null) {
            exploreAdjacentNonSolidBlocks(block, hole, todoBlocks, false, offset, boundingBox, depthOffset, world);
        }

        return hole;
    }

    /**
     * @param block the starting block.
     * @param hole the hole to fill.
     * @param todo the list of blocks that still need to be done.
     * @param onlyUnder boolean to determine whether to stop at the edge of the bounding box or not.
     * @param offset how far outside the boundingbox it needs to look.
     * @param boundingBox the boundingbox under which it looks.
     * @param depthOffset the maximum amount of blocks it can go down.
     * @param world the world in which to look.
     */
    public static void exploreAdjacentNonSolidBlocks(Block block, Hole hole, List<Block> todo, boolean onlyUnder,
                                                     int offset, BoundingBox boundingBox, int depthOffset,
                                                     World world) {

        //        final int offset = config.getInt(areaLimitOffsetConfigPath, 5);

        int minX = boundingBox.getX1();
        int maxX = boundingBox.getX2();
        int minZ = boundingBox.getZ1();
        int maxZ = boundingBox.getZ2();
        final int maxY = boundingBox.getY1() - 1;
        final int minY = boundingBox.getY1() - depthOffset;
        if (!onlyUnder) {
            minX -= offset;
            maxX += offset;
            minZ -= offset;
            maxZ += offset;
        }

        for (int i = 0; i < 6; i++) {
            Block b = getRelativeBlock(i, block, world);

            if (!(b.getY() <= maxY && (b.isLiquid() || b.isEmpty() || !b.getType().isSolid()) &&
                  !hole.contains(b))) {
                continue;
            }
            if (!(b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ)) {
                hole.setExceedsAreaLimit(true);
                continue;
            }
            if (b.getY() < minY) {
                hole.setExceedsMaxDepth(true);
                continue;
            }

            hole.add(b);
            todo.add(b);
        }
    }

    /**
     * @param dir in what direction it needs to look.
     * @param block the block from where it needs to look.
     * @param world the world where you are looking in.
     * @return return the found block.
     */
    public static Block getRelativeBlock(int dir, Block block, World world) {
        //        World world = block.getWorld();
        //        BuildFramework.getInstance().getLogger().info("" + block);
        switch (dir) {
            case 0:
                // NORTH
                return world.getBlockAt(block.getX(), block.getY(), block.getZ() - 1);
            case 1:
                // EAST
                return world.getBlockAt(block.getX() + 1, block.getY(), block.getZ());
            case 2:
                // SOUTH
                return world.getBlockAt(block.getX(), block.getY(), block.getZ() + 1);
            case 3:
                // WEST
                return world.getBlockAt(block.getX() - 1, block.getY(), block.getZ());
            case 4:
                // UP
                return world.getBlockAt(block.getX(), block.getY() + 1, block.getZ());
            case 5:
                // DOWN
                return world.getBlockAt(block.getX(), block.getY() - 1, block.getZ());

            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
