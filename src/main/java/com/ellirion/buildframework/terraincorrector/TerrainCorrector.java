package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TerrainCorrector {

    // These are all the faces of a block
    private static final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.DOWN,
            BlockFace.UP
    };

    /**
     * @param boundingBox the BoundingBox that will be used for terrain smoothing.
     * @param world The world in which
     * @return whether the smoothing succeeded
     */
    public boolean correctTerrain(BoundingBox boundingBox, World world) {
        List<Hole> holes = findHoles(boundingBox, world);

        // checken op blockers (river eg.)
        for (Hole h : holes) {
            if (h.containsLiquid() && checkForRiver(h.getBlockList(), boundingBox)) {
                return false;
            }
        }

        // juiste manier van vullen aanroepen

        for (Hole h : holes) {
            fillBasicHole(h, world);
        }

        Set<Block> toRemove = getBlocksInBoundingBox(boundingBox, world);
        setListToAir(toRemove);
        return true;
    }

    private boolean fillBasicHole(Hole hole, World world) {
        List<Block> blocks = hole.getTopBlocks();

        for (Block b : blocks) {
            b.setType(Material.BARRIER);
        }

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
                    hole.getBlockList().addAll(getConnectedBlocks(block, boundingBox));

                    // Add the hole to the list of holes
                    holes.add(hole);
                }
            }
        }
        return holes;
    }

    private boolean checkForRiver(final List<Block> blocks, BoundingBox boundingBox) {
        final int minX = boundingBox.getX1();
        final int maxX = boundingBox.getX2();
        final int minZ = boundingBox.getZ1();
        final int maxZ = boundingBox.getZ2();

        for (Block b : blocks) {
            //
            if (b.isLiquid() && ((b.getX() == minX && b.getRelative(BlockFace.WEST).isLiquid()) ||
                                 (b.getX() == maxX && b.getRelative(BlockFace.EAST).isLiquid()) ||
                                 (b.getZ() == minZ && b.getRelative(BlockFace.NORTH).isLiquid()) ||
                                 (b.getZ() == maxZ && b.getRelative(BlockFace.SOUTH).isLiquid()))) {
                return true;
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

    private void exploreNonSolidBlocks(Block block, Set<Block> results, List<Block> todo, BoundingBox boundingBox) {
        //Here I collect all blocks that are directly connected to variable 'block'.
        //(Shouldn't be more than 6, because a block has 6 sides)
        Set<Block> result = results;
        final int minX = boundingBox.getX1();
        final int maxX = boundingBox.getX2();
        final int minZ = boundingBox.getZ1();
        final int maxZ = boundingBox.getZ2();
        final int maxY = boundingBox.getY1() - 1;

        //Loop through all the relevant block faces
        for (BlockFace face : faces) {
            Block b = block.getRelative(face);

            //Check if the relative block is inside the to check area and
            //Check if the block is air or liquid and add the block if it wasn't added already
            if (b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ && b.getY() <= maxY &&
                (b.isLiquid() || b.isEmpty()) && result.add(b)) {
                //Add this block to the list of blocks that are yet to be done.
                todo.add(b);
            }
        }
    }

    private Set<Block> getConnectedBlocks(Block block, BoundingBox boundingBox) {
        Set<Block> set = new HashSet<>();
        LinkedList<Block> todoBlocks = new LinkedList<>();

        //Add the current block to the todoBlocks of blocks that are yet to be done
        todoBlocks.add(block);

        //Execute this method for each block in the todoBlocks
        while ((block = todoBlocks.poll()) != null) {
            exploreNonSolidBlocks(block, set, todoBlocks, boundingBox);
        }
        return set;
    }

    private Set<Block> getBlocksInBoundingBox(BoundingBox boundingBox, World world) {
        Set<Block> blocks = new HashSet<>();

        final int bottomBlockX = boundingBox.getX1();
        final int topBlockX = boundingBox.getX2();

        final int bottomBlockY = boundingBox.getY1();
        final int topBlockY = boundingBox.getY2();

        final int bottomBlockZ = boundingBox.getZ1();
        final int topBlockZ = boundingBox.getZ2();

        for (int x = bottomBlockX; x <= topBlockX; x++) {

            for (int y = bottomBlockY; y <= topBlockY; y++) {

                for (int z = bottomBlockZ; z <= topBlockZ; z++) {

                    final Block b = world.getBlockAt(x, y, z);

                    if (!b.isLiquid() && !b.isEmpty()) {
                        blocks.add(b);
                    }
                }
            }
        }

        return blocks;
    }

    private void setListToAir(Set<Block> blocks) {
        for (Block b : blocks) {
            b.setType(Material.AIR);
        }
    }
}
