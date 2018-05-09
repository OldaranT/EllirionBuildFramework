package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TerrainCorrector {

    private static final FileConfiguration CONFIG = BuildFramework.getInstance().getConfig();

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
    public String correctTerrain(BoundingBox boundingBox, World world) {
        List<Hole> holes = findHoles(boundingBox, world);

        // checken op blockers (river eg.)
        for (Hole h : holes) {
            if (h.containsLiquid() && checkForRiver(h.getBlockList(), boundingBox)) {
                return "Could not correct the terrain because the selection is above a lake, pond or river";
            }
        }

        for (Hole h : holes) {
            correctHole(h, world, boundingBox);
        }

        List<Block> toRemove = getBlocksInBoundingBox(boundingBox, world);
        setListToAir(toRemove);
        return "Corrected Terrain";
    }

    private boolean correctHole(Hole hole, World world, BoundingBox boundingbox) {
        if (!hole.onlyBelowBoundingBox(boundingbox)) {
            fillBasicHole(hole, world);
            return true;
        }
        if (!hole.exceedsMaxDepth()) {
            fillSmallHoleAtSide(hole, world, boundingbox);
            return true;
        }

        return false;
    }

    private void fillSmallHoleAtSide(Hole hole, World world, BoundingBox boundingBox) {
        Material mat = getFloorMaterial(boundingBox, world);

        BuildFramework.getInstance().getLogger().info(mat + "");
        for (Block b : hole.getBlockList()) {
            b.setType(mat);
        }
    }

    private void fillBasicHole(Hole hole, World world) {
        List<Block> topBlocks = hole.getTopBlocks();

        for (Block b : topBlocks) {
            b.setType(Material.BARRIER);
        }
    }

    private List<Hole> findHoles(BoundingBox boundingBox, World world) {
        List<Hole> holes = new ArrayList<>();
        int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.isEmpty() || block.isLiquid()) ||
                    isBlockTypeNonSolid(block.getType()) && holes.stream().noneMatch(hole -> hole.contains(block))) {

                    // Add the hole to the list of holes once all blocks are found
                    holes.add(getHole(block, boundingBox));
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

    private Material getFloorMaterial(BoundingBox boundingBox, World world) {
        List<Material> materials = new ArrayList<>();

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                materials.add(world.getBlockAt(x, boundingBox.getY1() - 1, z).getType());
            }
        }

        int max = 0;
        int curr;
        Material currKey = null;
        Set<Material> unique = new HashSet<>(materials);

        for (Material key : unique) {
            curr = Collections.frequency(materials, key);

            if (max < curr) {
                max = curr;
                currKey = key;
            }
        }
        return currKey;
    }

    private Hole getHole(Block block, BoundingBox boundingBox) {
        Hole hole = new Hole();
        LinkedList<Block> todoBlocks = new LinkedList<>();

        //Add the current block to the todoBlocks of blocks that are yet to be done
        todoBlocks.add(block);

        //Execute this method for each block in the todoBlocks
        while ((block = todoBlocks.poll()) != null) {
            exploreAdjacentNonSolidBlocks(block, hole, todoBlocks, boundingBox);
        }

        return hole;
    }

    private void exploreAdjacentNonSolidBlocks(Block block, Hole hole, List<Block> todo, BoundingBox boundingBox) {

        final int minX = boundingBox.getX1() - 5;
        final int maxX = boundingBox.getX2() + 5;
        final int minZ = boundingBox.getZ1() - 5;
        final int maxZ = boundingBox.getZ2() + 5;
        final int maxY = boundingBox.getY1() - 1;
        final int minY = maxY - CONFIG.getInt("TerrainCorrecter.MaxHoleDepth", 5);

        for (BlockFace face : faces) {
            Block b = block.getRelative(face);

            if (!(b.getY() <= maxY && (b.isLiquid() || b.isEmpty() || isBlockTypeNonSolid(b.getType())) &&
                  !hole.contains(b))) {
                continue;
            }
            if (!(b.getY() >= minY)) {
                hole.setExceedsMaxDepth(true);
                continue;
            }
            if (!(b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ)) {
                hole.setExceedsAreaLimit(true);
                continue;
            }

            hole.add(b);
            todo.add(b);
        }
    }

    private List<Block> getBlocksInBoundingBox(BoundingBox boundingBox, World world) {
        List<Block> blocks = new ArrayList<>();

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

    private void setListToAir(List<Block> blocks) {
        for (Block b : blocks) {
            b.setType(Material.AIR, true);
        }
    }

    private boolean isBlockTypeNonSolid(Material type) {
        return type == Material.LONG_GRASS || type == Material.BROWN_MUSHROOM ||
               type == Material.RED_MUSHROOM || type == Material.SAPLING ||
               type == Material.CROPS || type == Material.DEAD_BUSH ||
               type == Material.DOUBLE_PLANT;
    }
}

