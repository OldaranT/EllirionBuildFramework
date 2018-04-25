package com.ellirion.buildframework.terraincorrector;

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

    // all the faces facing towards the X and Z axis
    private static final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
    };

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

        a:
        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            b:
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

    private boolean checkForRiver(final List<Block> holes, BoundingBox boundingBox) {
        final int minX = boundingBox.getX1();
        final int maxX = boundingBox.getX2();
        final int minZ = boundingBox.getZ1();
        final int maxZ = boundingBox.getZ2();
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

    private void getConnectedblocks(Block block, Set<Block> results, List<Block> todo, BoundingBox boundingBox) {
        //Here I collect all blocks that are directly connected to variable 'block'.
        //(Shouldn't be more than 6, because a block has 6 sides)
        Set<Block> result = results;
        final int minX = boundingBox.getX1() - 1;
        final int maxX = boundingBox.getX2() + 1;
        final int minZ = boundingBox.getZ1() - 1;
        final int maxZ = boundingBox.getZ2() + 1;

        //Loop through all the relevant block faces
        for (BlockFace face : faces) {
            Block b = block.getRelative(face);
            //Check if the relative block is inside the to check area.
            if (b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ) {
                //Check if they're both of the same type

                if (b.getType() == block.getType()) {
                    //Add the block if it wasn't added already
                    if (result.add(b)) {

                        //Add this block to the list of blocks that are yet to be done.
                        todo.add(b);
                    }
                }
            }
        }
    }

    private Set<Block> getConnectedblocks(Block block, BoundingBox boundingBox) {
        Set<Block> set = new HashSet<>();
        LinkedList<Block> todoBlocks = new LinkedList<>();

        //Add the current block to the todoBlocks of blocks that are yet to be done
        todoBlocks.add(block);

        //Execute this method for each block in the todoBlocks
        while ((block = todoBlocks.poll()) != null) {
            getConnectedblocks(block, set, todoBlocks, boundingBox);
        }
        return set;
    }
}
