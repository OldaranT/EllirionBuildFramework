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

        //        if (!hole.onlyBelowBoundingBox(boundingbox)) {
        //            fillBasicHole(hole, world);
        //            return true;
        //        }
        //        if (!hole.exceedsMaxDepth()) {
        //            fillSmallHoleAtSide(hole, world, boundingbox);
        //            return true;
        //        }
        //        if (hole.exceedsMaxDepth()) {
        //        buildSupports(hole, boundingbox);
        //        }
        buildRavineSupports(hole, boundingbox);

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
                if ((block.isEmpty() || block.isLiquid() ||
                     isBlockTypeNonSolid(block.getType())) && holes.stream().noneMatch(hole -> hole.contains(block))) {

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
        Hole hole = new Hole(block);
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
            if (!(b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ)) {
                hole.setExceedsAreaLimit(true);
                if (b.getY() < minY) {
                    hole.setExceedsMaxDepth(true);
                }
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

    private void buildSupports(Hole hole, BoundingBox boundingBox) {
        List<Block> topBlocks = hole.getTopBlocks();
        List<Block> toChange = new ArrayList<>();

        int minX = boundingBox.getX1();
        int maxX = boundingBox.getX2();
        int minZ = boundingBox.getZ1();
        int maxZ = boundingBox.getZ2();

        for (Block b : topBlocks) {
            if (b.getX() < minX || b.getX() > maxX || b.getZ() < minZ || b.getZ() > maxZ || surroundingIsNotSolid(b)) {
                continue;
            }
            toChange.addAll(getLowerBlocks(b, boundingBox));
        }

        for (Block b : toChange) {
            b.setType(Material.FENCE);
        }

        List<Block> toBarrier = new ArrayList(topBlocks);
        toBarrier.removeAll(toChange);

        for (Block b : toBarrier) {
            if (b.getX() >= minX && b.getX() <= maxX && b.getZ() >= minZ && b.getZ() <= maxZ) {
                b.setType(Material.BARRIER);
            }
        }
    }

    private boolean surroundingIsNotSolid(Block b) {
        BlockFace[] currentYPlaneFaces = {
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST
        };
        for (BlockFace f : currentYPlaneFaces) {
            Block relative = b.getRelative(f);
            if (!(relative.isEmpty() || relative.isLiquid())) {
                return false;
            }
        }
        return true;
    }

    private List<Block> getLowerBlocks(Block b, BoundingBox boundingBox) {
        int maxDepth = CONFIG.getInt("TerrainCorrecter.MaxHoleDepth", 5);
        List<Block> temp = new ArrayList<>();
        Block current = b;
        for (int i = 0; i < maxDepth; i++) {
            temp.add(current);
            Block nextBlock = current.getRelative(BlockFace.DOWN);
            if (i == maxDepth - 1 && (nextBlock.isEmpty() || nextBlock.isLiquid())) {
                temp.clear();
                break;
            }
            if (!nextBlock.isEmpty() && !nextBlock.isLiquid()) {
                break;
            }
            current = nextBlock;
        }
        return getConeShape(temp, boundingBox);
    }

    private List<Block> getConeShape(List<Block> blocks, BoundingBox bb) {
        if (blocks.isEmpty() || blocks.size() == 1) {
            return blocks;
        }
        Block topBlock = blocks.get(0);
        Block bottomBlock = blocks.get(blocks.size() - 1);

        int height = topBlock.getY() - bottomBlock.getY();
        int bottomY = bottomBlock.getY();
        int midX = topBlock.getX();
        int midZ = topBlock.getZ();

        int minX = bb.getX1();
        int maxX = bb.getX2();
        int minZ = bb.getZ1();
        int maxZ = bb.getZ2();

        World world = topBlock.getWorld();

        for (int i = height; i >= 0; i--) {
            for (int x = midX - i; x <= midX + i; x++) {
                for (int z = midZ - i; z <= midZ + i; z++) {
                    Block current = world.getBlockAt(x, bottomY + i, z);
                    if (blocks.contains(current)) {
                        continue;
                    }
                    if (!current.isLiquid() && !current.isEmpty()) {
                        continue;
                    }
                    if (z < minZ || z > maxZ || x < minX || x > maxX) {
                        continue;
                    }
                    blocks.add(current);
                }
            }
        }

        return blocks;
    }

    private void buildRavineSupports(Hole hole, BoundingBox boundingBox) {
        int minX = boundingBox.getX1();
        int maxX = boundingBox.getX2();

        int minZ = boundingBox.getZ1();
        int maxZ = boundingBox.getZ2();

        int minHoleX = maxX - ((maxX - minX) / 2);
        int maxHoleX = maxX - ((maxX - minX) / 2);

        int minHoleZ = maxZ - ((maxZ - minZ) / 2);
        int maxHoleZ = maxZ - ((maxZ - minZ) / 2);

        List<Block> topBlocks = hole.getTopBlocks();
        List<Block> underBoundingBox = new ArrayList<>();
        List<Block> toChange = new ArrayList<>();

        for (Block b : topBlocks) {
            int blockX = b.getX();
            int blockZ = b.getZ();
            if (!(blockX < minX || blockX > maxX || blockZ < minZ || blockZ > maxZ)) {
                underBoundingBox.add(b);
                if (blockX < minHoleX) {
                    minHoleX = blockX;
                }
                if (blockX > maxHoleX) {
                    maxHoleX = blockX;
                }
                if (blockZ < minHoleZ) {
                    minHoleZ = blockZ;
                }
                if (blockZ > maxHoleZ) {
                    maxHoleZ = blockZ;
                }
            }
        }

        int spaceX = maxHoleX - minHoleX;
        int spaceZ = maxHoleZ - minHoleZ;

        //todo change >= <= wrong way around
        if ((minHoleX >= minX && maxHoleX <= maxX) && !(minHoleZ <= minZ || maxHoleZ >= maxZ)) {
            toChange = getBridgeSupportOnZLine(boundingBox, underBoundingBox, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
        } else if (!(minHoleX <= minX || maxHoleX <= maxX) && (minHoleZ >= minZ && maxHoleZ <= maxZ)) {
            toChange = getBridgeSupportOnXLine(boundingBox, underBoundingBox, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
        } else {
            toChange = getToChangeBlocks(spaceX, spaceZ, boundingBox, underBoundingBox, minHoleX, maxHoleX,
                                         minHoleZ, maxHoleZ);
        }

        for (Block b : toChange) {
            b.setType(Material.FENCE);
        }
    }

    private List<Block> getBlocksBelow(Block b, int depth) {
        List<Block> result = new ArrayList<>();
        BlockFace down = BlockFace.DOWN;
        Block current = b.getRelative(down);

        for (int i = 0; i < depth; i++) {
            if (!current.isEmpty() && !current.isLiquid()) {
                break;
            }
            result.add(current);
            current = current.getRelative(down);
        }

        return result;
    }

    @SuppressWarnings("Duplicates")
    private List<Block> getToChangeBlocks(int spaceX, int spaceZ, BoundingBox boundingBox, List<Block> underBoundingBox,
                                          int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {
        int minX = boundingBox.getX1();
        int maxX = boundingBox.getX2();
        int centreX = maxX - ((maxX - minX) / 2);

        int minZ = boundingBox.getZ1();
        int maxZ = boundingBox.getZ2();
        int centreZ = maxZ - ((maxZ - minZ) / 2);

        int y = boundingBox.getY1() - 1;

        World world = underBoundingBox.get(0).getWorld();
        List<Block> toChange = new ArrayList<>(underBoundingBox);
        int maxDepth;

        if (minHoleX == minX) {
            maxDepth = (spaceX > spaceZ) ? spaceX : spaceZ;
            if (minHoleZ == minZ) {
                for (int i = 0; i < maxDepth; i++) {
                    for (int x = minX + i; x <= centreX; x++) {
                        for (int z = minZ + i; z <= centreZ; z++) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
            if (maxHoleZ == maxZ) {
                for (int i = 0; i < maxDepth; i++) {
                    for (int x = minX + i; x <= centreX; x++) {
                        for (int z = maxZ - i; z >= centreZ; z--) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
            if (!(minHoleZ == minZ) && !(maxHoleZ == maxZ)) {
                maxDepth = spaceX;

                for (int i = 0; i < maxDepth; i++) {
                    for (int x = minX + i; x <= centreX; x++) {
                        for (int z = minZ; z <= centreZ; z++) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                        for (int z = maxZ; z >= centreZ; z--) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
        }

        if (maxHoleX == maxX) {
            maxDepth = (spaceX > spaceZ) ? spaceX : spaceZ;
            if (minHoleZ == minZ) {
                for (int i = 0; i < maxDepth; i++) {
                    for (int x = maxX - i; x >= centreX; x--) {
                        for (int z = minZ + i; z <= centreZ; z++) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
            if (maxHoleZ == maxZ) {
                for (int i = 0; i < maxDepth; i++) {
                    for (int x = maxX - i; x >= centreX; x--) {
                        for (int z = maxZ - i; z >= centreZ; z--) {
                            Block b = world.getBlockAt(x, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
            if (!(minHoleZ == minZ) && !(maxHoleZ == maxZ)) {
                maxDepth = spaceX;

                for (int i = 0; i < maxDepth; i++) {
                    for (int xLoc = maxX - i; xLoc >= centreX; xLoc--) {
                        for (int z = minZ; z <= centreZ; z++) {
                            Block b = world.getBlockAt(xLoc, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                        for (int z = maxZ; z >= centreZ; z--) {
                            Block b = world.getBlockAt(xLoc, y, z);
                            if (!underBoundingBox.contains(b)) {
                                continue;
                            }
                            toChange.addAll(getBlocksBelow(b, i));
                        }
                    }
                }
            }
        }
        if (minHoleZ == minZ && !(minHoleX == minX || maxHoleX == maxX)) {
            maxDepth = spaceZ;
            for (int i = 0; i < maxDepth; i++) {
                for (int x = minX; x <= centreX; x++) {
                    for (int z = minZ + i; z <= centreZ; z++) {
                        Block b = world.getBlockAt(x, y, z);
                        if (!underBoundingBox.contains(b)) {
                            continue;
                        }
                        toChange.addAll(getBlocksBelow(b, i));
                    }
                }
            }
        }
        if (maxHoleZ == maxZ && !(minHoleX == minX || maxHoleX == maxX)) {
            maxDepth = spaceZ;
            for (int i = 0; i < maxDepth; i++) {
                for (int x = minX; x <= centreX; x++) {
                    for (int z = maxZ - i; z >= centreZ; z--) {
                        Block b = world.getBlockAt(x, y, z);
                        if (!underBoundingBox.contains(b)) {
                            continue;
                        }
                        toChange.addAll(getBlocksBelow(b, i));
                    }
                }
            }
        }

        return toChange;
    }

    private List<Block> getBridgeSupportOnZLine(BoundingBox boundingBox, List<Block> underBoundingBox,
                                                int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {

        int holeCentreZ = maxHoleZ - ((maxHoleZ - minHoleZ) / 2);

        int y = boundingBox.getY1() - 1;

        World world = underBoundingBox.get(0).getWorld();
        List<Block> toChange = new ArrayList<>(underBoundingBox);

        int maxDepth = maxHoleZ - holeCentreZ;

        for (int x = minHoleX; x <= maxHoleX; x++) {
            //            for (int z = holeCentreZ + i; z <= maxHoleZ; z++) {
            //                Block b = world.getBlockAt(x, y, z);
            //                if (!underBoundingBox.contains(b)) {
            //                    continue;
            //                }
            //                toChange.addAll(getBlocksBelow(b, i));
            //            }
            //            for (int z = holeCentreZ - i; z >= minHoleZ; z--) {
            //                Block b = world.getBlockAt(x, y, z);
            //                if (!underBoundingBox.contains(b)) {
            //                    continue;
            //                }
            //                toChange.addAll(getBlocksBelow(b, i));
            //            }
            for (int i = 0; i < maxDepth; i++) {
                Block b = world.getBlockAt(x, holeCentreZ + i, y);
                Block a = world.getBlockAt(x, holeCentreZ - i, y);
                if (underBoundingBox.contains(b)) {
                    toChange.addAll(getBlocksBelow(b, i));
                }
                if (underBoundingBox.contains(a)) {
                    toChange.addAll(getBlocksBelow(a, i));
                }
            }
        }
        return toChange;
    }

    @SuppressWarnings("Duplicates")
    private List<Block> getBridgeSupportOnXLine(BoundingBox boundingBox, List<Block> underBoundingBox,
                                                int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {

        int holeCentreX = maxHoleX - ((maxHoleX - minHoleX) / 2);

        int y = boundingBox.getY1() - 1;

        World world = underBoundingBox.get(0).getWorld();
        List<Block> toChange = new ArrayList<>(underBoundingBox);

        int maxDepth = maxHoleX - holeCentreX;

        for (int i = 0; i < maxDepth; i++) {
            for (int x = holeCentreX + i; x <= maxHoleX; x++) {
                for (int z = minHoleZ; z <= maxHoleZ; z++) {
                    Block b = world.getBlockAt(x, y, z);
                    if (!underBoundingBox.contains(b)) {
                        continue;
                    }
                    toChange.addAll(getBlocksBelow(b, i));
                }
            }
            for (int x = holeCentreX - i; x >= minHoleX; x--) {
                for (int z = minHoleZ; z <= maxHoleZ; z++) {
                    Block b = world.getBlockAt(x, y, z);
                    if (!underBoundingBox.contains(b)) {
                        continue;
                    }
                    toChange.addAll(getBlocksBelow(b, i));
                }
            }
        }
        return toChange;
    }
}

