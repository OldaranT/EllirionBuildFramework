package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TerrainCorrector {

    private static final FileConfiguration CONFIG = BuildFramework.getInstance().getConfig();
    private static final String maxHoleDepthConfigPath = "TerrainCorrecter.MaxHoleDepth";
    private static final String areaLimitOffsetConfigPath = "TerrainCorrecter.AreaLimitOffset";

    // These are all the faces of a block
    private static final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.DOWN,
            BlockFace.UP
    };

    private BoundingBox boundingBox;
    private World world;

    /**
     * @param boundingBox the BoundingBox that will be used for terrain smoothing.
     * @param world The world in which
     * @return whether the smoothing succeeded
     */
    public String correctTerrain(BoundingBox boundingBox, World world) {
        this.boundingBox = boundingBox;
        this.world = world;
        List<Hole> holes = findHoles();

        // checken op blockers (river eg.)
        for (Hole h : holes) {
            if (h.containsLiquid() && checkForRiver(h.getBlockList())) {
                return "Could not correct the terrain because the selection is above a lake, pond or river";
            }
        }

        for (Hole h : holes) {
            correctHole(h);
        }

        List<Block> toRemove = getBlocksInBoundingBox();
        setListToAir(toRemove);
        return "Corrected Terrain";
    }

    private boolean correctHole(Hole hole) {

        if (!hole.onlyBelowBoundingBox(boundingBox) && hole.exceedsMaxDepth()) {
            buildRavineSupports(hole);
            return true;
        }
        if (hole.onlyBelowBoundingBox(boundingBox)) {
            fillBasicHole(hole);
            return true;
        }
        if (!hole.exceedsMaxDepth()) {
            fillSmallHoleAtSide(hole);
            return true;
        }
        return false;
    }

    private void fillSmallHoleAtSide(Hole hole) {
        Material mat = getFloorMaterial();

        BuildFramework.getInstance().getLogger().info(mat + "");
        for (Block b : hole.getBlockList()) {
            b.setType(mat);
        }
    }

    private void fillBasicHole(Hole hole) {
        List<Block> topBlocks = hole.getTopBlocks();

        for (Block b : topBlocks) {
            b.setType(Material.BARRIER);
        }
    }

    private List<Hole> findHoles() {
        List<Hole> holes = new ArrayList<>();
        int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.isEmpty() || block.isLiquid() ||
                     isBlockTypeNonSolid(block.getType())) && holes.stream().noneMatch(hole -> hole.contains(block))) {

                    // Add the hole to the list of holes once all blocks are found
                    holes.add(getHole(block));
                }
            }
        }
        return holes;
    }

    private boolean checkForRiver(final List<Block> blocks) {
        final int minX = boundingBox.getX1();
        final int maxX = boundingBox.getX2();
        final int minZ = boundingBox.getZ1();
        final int maxZ = boundingBox.getZ2();

        for (Block b : blocks) {
            // check whether the block is liquid, is on the edge of the boundingBox and
            // is adjacent to a liquid block outside the boundingbox.
            // if this is true then you are on a "river".
            if (b.isLiquid() && ((b.getX() == minX && b.getRelative(BlockFace.WEST).isLiquid()) ||
                                 (b.getX() == maxX && b.getRelative(BlockFace.EAST).isLiquid()) ||
                                 (b.getZ() == minZ && b.getRelative(BlockFace.NORTH).isLiquid()) ||
                                 (b.getZ() == maxZ && b.getRelative(BlockFace.SOUTH).isLiquid()))) {
                return true;
            }
        }

        return false;
    }

    private Material getFloorMaterial() {
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

    private Hole getHole(Block block) {
        Hole hole = new Hole(block);
        LinkedList<Block> todoBlocks = new LinkedList<>();

        //Add the current block to the todoBlocks of blocks that are yet to be done
        todoBlocks.add(block);

        //Execute this method for each block in the todoBlocks
        while ((block = todoBlocks.poll()) != null) {
            exploreAdjacentNonSolidBlocks(block, hole, todoBlocks);
        }

        return hole;
    }

    private void exploreAdjacentNonSolidBlocks(Block block, Hole hole, List<Block> todo) {

        final int offset = CONFIG.getInt(areaLimitOffsetConfigPath, 5);

        final int minX = boundingBox.getX1() - offset;
        final int maxX = boundingBox.getX2() + offset;
        final int minZ = boundingBox.getZ1() - offset;
        final int maxZ = boundingBox.getZ2() + offset;
        final int maxY = boundingBox.getY1() - 1;
        final int minY = boundingBox.getY1() - CONFIG.getInt(maxHoleDepthConfigPath, 5);

        for (BlockFace face : faces) {
            Block b = block.getRelative(face);

            if (!(b.getY() <= maxY && (b.isLiquid() || b.isEmpty() || isBlockTypeNonSolid(b.getType())) &&
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

    private List<Block> getBlocksInBoundingBox() {
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

    private void buildRavineSupports(Hole hole) {
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
        List<Block> toChange;

        // find the dimensions under the boundingbox
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

        // use the correct support placing locations
        if ((minHoleX <= minX && maxHoleX >= maxX) && !(minHoleZ <= minZ && maxHoleZ >= maxZ)) {
            // build bridge style supports for structure from point 1 to point 2 on the Z axis.
            toChange = getBridgeSupportOnZAxis(underBoundingBox, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
        } else if (!(minHoleX <= minX && maxHoleX <= maxX) && (minHoleZ <= minZ && maxHoleZ >= maxZ)) {
            // build bridge style supports for structure from point 1 to point 2 on the X axis.
            toChange = getBridgeSupportOnXAxis(underBoundingBox, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
        } else {
            // build building supports under the bounding box.
            toChange = createSupportsLocationMap();
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

    private List<Block> getBridgeSupportOnZAxis(List<Block> underBoundingBox,
                                                int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {

        int holeCentreZ = maxHoleZ - ((maxHoleZ - minHoleZ) / 2);

        int y = boundingBox.getY1() - 1;

        List<Block> toChange = new ArrayList<>(underBoundingBox);

        int maxDepth = maxHoleZ - holeCentreZ;

        for (int x = minHoleX; x <= maxHoleX; x++) {
            for (int i = 0; i <= maxDepth; i++) {
                toChange.addAll(blocksToReplace(x, y, holeCentreZ + i, i, underBoundingBox));
                toChange.addAll(blocksToReplace(x, y, holeCentreZ - i, i, underBoundingBox));
            }
        }
        return toChange;
    }

    private List<Block> getBridgeSupportOnXAxis(List<Block> underBoundingBox,
                                                int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {

        int holeCentreX = maxHoleX - ((maxHoleX - minHoleX) / 2);

        int y = boundingBox.getY1() - 1;

        List<Block> toChange = new ArrayList<>(underBoundingBox);

        int maxDepth = maxHoleX - holeCentreX;
        
        for (int z = minHoleZ; z <= maxHoleZ; z++) {
            for (int i = 0; i <= maxDepth; i++) {
                toChange.addAll(blocksToReplace(holeCentreX + i, y, z, i, underBoundingBox));
                toChange.addAll(blocksToReplace(holeCentreX - i, y, z, i, underBoundingBox));
            }
        }
        return toChange;
    }

    private List<Block> blocksToReplace(int x, int y, int z, int depth, List<Block> underBB) {
        List<Block> toChange = new ArrayList<>();
        Block b = world.getBlockAt(x, y, z);
        if (underBB.contains(b)) {
            toChange.addAll(getBlocksBelow(b, depth));
        }
        return toChange;
    }

    private List<Block> createSupportsLocationMap() {
        List<Block> toChange = new ArrayList<>();
        int x1 = boundingBox.getX1();
        int x2 = boundingBox.getX2();

        int z1 = boundingBox.getZ1();
        int z2 = boundingBox.getZ2();

        int width = x2 - x1 + 1;
        int depth = z2 - z1 + 1;

        int minX = 0;
        int maxX = width - 1;
        int minZ = 0;
        int maxZ = depth - 1;

        int baseY = boundingBox.getY1();
        int offsetY = 1;

        // Keep track of what is done and what isn't
        boolean[][] done = new boolean[width][];
        for (int i = 0; i < width; i++) {
            done[i] = new boolean[depth];
        }

        // Keep going until we've shrunk to zero (or lower) width
        while (minX <= maxX && minZ <= maxZ) {

            // Trace down if it is not done
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    // If we already encountered a solid, ignore
                    if (done[x][z]) {
                        continue;
                    }

                    // If it's a solid, this column is done
                    Location loc = new Point(x1 + x, baseY - offsetY, z1 + z).toLocation(world);
                    Block block = world.getBlockAt(loc);
                    if (block.getType().isSolid()) {
                        //                        done[x][z] = true;
                        continue;
                    }
                    toChange.add(block);
                }
            }

            // Shrink the area by one on all sides
            minX++;
            minZ++;
            maxX--;
            maxZ--;

            // Go down one layer
            offsetY++;
        }

        return toChange;
    }
}

