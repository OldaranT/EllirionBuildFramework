package com.ellirion.buildframework.terraincorrector;

import lombok.Getter;
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
import com.ellirion.buildframework.util.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TerrainCorrector {

    private static final FileConfiguration CONFIG = BuildFramework.getInstance().getConfig();
    private static final String maxHoleDepthConfigPath = "TerrainCorrecter.MaxHoleDepth";
    private static final String areaLimitOffsetConfigPath = "TerrainCorrecter.AreaLimitOffset";

    private BoundingBox boundingBox;
    private World world;

    /**
     * @param boundingBox the BoundingBox that will be used for terrain smoothing.
     * @param world The world in which
     */

    public void correctTerrain(BoundingBox boundingBox, World world) {
        new Promise<>(finisher -> {
            this.boundingBox = boundingBox;
            this.world = world;
            List<Hole> holes = findHoles();

            for (Hole h : holes) {
                correctHole(h);
            }

            List<Block> toRemove = getBlocksInBoundingBox();
            setListToAir(toRemove);
        }, true);
    }

    private void correctHole(Hole hole) {

        if (!hole.onlyBelowBoundingBox(boundingBox) && hole.exceedsMaxDepth()) {
            buildRavineSupports(hole);
            return;
        }
        if (hole.onlyBelowBoundingBox(boundingBox)) {
            fillBasicHole(hole);
            return;
        }
        if (!hole.exceedsMaxDepth() && !hole.exceedsAreaLimit()) {
            fillHoleAtSide(hole);
            return;
        }

        fillHoleAtSidePartially(hole);
    }

    private void fillHoleAtSidePartially(Hole hole) {
        Material mat = getFloorMaterial();

        List<Block> blocks = hole.getTopBlocks();

        Map<Block, Integer> startingDepthMap = calculateStartingDepthMap(blocks);

        placeBlocksAccordingToDepthMap(startingDepthMap, world, mat);
    }

    private Map<Block, Integer> calculateStartingDepthMap(List<Block> blocks) {
        int minimalFillingWidth = 2;
        int maxDepth = 5;
        int percentage = 10;

        Map<Block, Integer> result = new HashMap<>();
        LinkedList<ToDoEntry> todo = new LinkedList<>();

        Block block = blocks.stream().filter(x -> blocksBelowBoundingBoxOrWithinOffset(x, 0)).findAny().get();

        ToDoEntry entry = new ToDoEntry(block, 0, percentage, 0);

        todo.add(entry);

        while ((entry = todo.poll()) != null) {
            exploreDepthOfAdjacentBlocks(entry, result, todo, minimalFillingWidth, maxDepth, percentage);
        }

        return result;
    }

    private void exploreDepthOfAdjacentBlocks(ToDoEntry currentEntry, Map<Block, Integer> result,
                                              LinkedList<ToDoEntry> todo,
                                              int minWidth, int maxDepth, int initialPercentage) {

        if ((result.containsKey(currentEntry.getBlock()) &&
             result.get(currentEntry.getBlock()) <= currentEntry.getDepth()) ||
            currentEntry.getDepth() >= maxDepth) {
            return;
        }

        result.put(currentEntry.getBlock(), currentEntry.getDepth());

        //Loop through the adjacent blocks
        for (int dir = 0; dir < 4; dir++) {
            Block nextBlock = getRelativeBlock(dir, currentEntry.getBlock());

            int percentage = currentEntry.getPercentage();
            int maxOffset = currentEntry.getMaxDepthOffset();
            int depth = currentEntry.getDepth() + 1;

            if (!blocksBelowBoundingBoxOrWithinOffset(nextBlock, minWidth)) {

                //introduce randomization for blocks not below the bounding box
                Double rand = ThreadLocalRandom.current().nextDouble(0, 100);

                BuildFramework.getInstance().getLogger().info(
                        String.format("rand: %f, percentage: %d", rand, currentEntry.getPercentage()));

                if (rand < currentEntry.getPercentage()) {
                    percentage -= 5;
                    maxOffset += 1;
                    depth -= 1;
                }

                todo.addLast(new ToDoEntry(nextBlock, depth, percentage, maxOffset));
                continue;
            }
            todo.addLast(new ToDoEntry(nextBlock, 0, initialPercentage, maxOffset));
        }
    }

    private void placeBlocksAccordingToDepthMap(Map<Block, Integer> map, World world,
                                                Material mat) {
        for (Map.Entry entry : map.entrySet()) {

            fillDownwards((Block) entry.getKey(), world, mat, (int) entry.getValue());
        }
    }

    private void fillDownwards(Block block, World world, Material mat, int startDepth) {
        Block currentBlock = world.getBlockAt(block.getX(), block.getY() - startDepth, block.getZ());

        while (currentBlock.isEmpty() || !currentBlock.getType().isSolid()) {
            sendSyncBlockChanges(currentBlock, mat);
            currentBlock = getRelativeBlock(5, currentBlock);
        }
    }

    private boolean blocksBelowBoundingBoxOrWithinOffset(Block block, int offset) {
        return (block.getX() >= boundingBox.getX1() - offset &&
                block.getX() <= boundingBox.getX2() + offset &&
                block.getZ() >= boundingBox.getZ1() - offset &&
                block.getZ() <= boundingBox.getZ2() + offset);
    }

    private void fillHoleAtSide(Hole hole) {
        Material mat = getFloorMaterial();

        for (Block b : hole.getBlockList()) {
            sendSyncBlockChanges(b, mat);
        }
    }

    private void fillBasicHole(Hole hole) {
        List<Block> topBlocks = hole.getTopBlocks();

        for (Block b : topBlocks) {
            sendSyncBlockChanges(b, Material.BARRIER);
        }
    }

    private List<Hole> findHoles() {
        List<Hole> holes = new ArrayList<>();
        int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Block block = world.getBlockAt(x, y, z);
                if ((block.isEmpty() || block.isLiquid() || !block.getType().isSolid()) &&
                    holes.stream().noneMatch(hole -> hole.contains(block))) {

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
            if (b.isLiquid() && blocksBelowBoundingBoxOrWithinOffset(b, 0) &&
                ((b.getX() == minX && getRelativeBlock(1, b).isLiquid()) ||
                 (b.getX() == maxX && getRelativeBlock(3, b).isLiquid()) ||
                 (b.getZ() == minZ && getRelativeBlock(0, b).isLiquid()) ||
                 (b.getZ() == maxZ && getRelativeBlock(2, b).isLiquid()))) {
                return true;
            }
        }

        return false;
    }

    private Material getFloorMaterial() {
        Map<Material, Integer> materials = new HashMap<>();

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                Material mat = world.getBlockAt(x, boundingBox.getY1() - 1, z).getType();
                if (materials.containsKey(mat)) {
                    materials.replace(mat, materials.get(mat) + 1);
                } else {
                    materials.put(mat, 1);
                }
            }
        }

        int max = 0;
        Material material = null;
        for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
            if (entry.getValue() > max && entry.getKey().isSolid()) {
                max = entry.getValue();
                material = entry.getKey();
            }
        }

        return material;
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

        for (int i = 0; i < 6; i++) {
            Block b = getRelativeBlock(i, block);

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
            sendSyncBlockChanges(b, Material.AIR);
        }
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
            toChange = getBridgeSupportOnZAxis(underBoundingBox, minHoleX, maxHoleX, minHoleZ,
                                               maxHoleZ);
        } else if (!(minHoleX <= minX && maxHoleX <= maxX) &&
                   (minHoleZ <= minZ && maxHoleZ >= maxZ)) {
            // build bridge style supports for structure from point 1 to point 2 on the X axis.
            toChange = getBridgeSupportOnXAxis(underBoundingBox, minHoleX, maxHoleX, minHoleZ,
                                               maxHoleZ);
        } else {
            // build building supports under the bounding box.
            toChange = createSupportsLocationMap();
        }

        for (Block b : toChange) {
            sendSyncBlockChanges(b, Material.FENCE);
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
                toChange.addAll(
                        blocksToReplace(holeCentreX + i, y, z, i, underBoundingBox));
                toChange.addAll(
                        blocksToReplace(holeCentreX - i, y, z, i, underBoundingBox));
            }
        }
        return toChange;
    }

    private List<Block> blocksToReplace(int x, int y, int z,
                                        int depth, List<Block> underBB) {
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
                    Location loc = new Point(x1 + x, baseY - offsetY,
                                             z1 + z).toLocation(world);
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

    private Block getRelativeBlock(int dir, Block block) {
        World world = block.getWorld();
        BuildFramework.getInstance().getLogger().info("" + block);
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

    //    private Block syncGetBlockAt(World world, Location location) {
    //        return syncGetBlockAt(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    //    }
    //
    //    private Block syncGetBlockAt(World world, int x, int y, int z) {
    //        List<Block> result = new ArrayList<>();
    //
    //        Promise<Block> p = new Promise<>((finisher) -> {
    //            BuildFramework.getInstance().getLogger().info("bla");
    //            finisher.resolve(world.getBlockAt(x, y, z));
    //        }, false);
    //
    //        p.consumeSync(result::add);
    //
    //        return result.get(0);
    //    }

    private void sendSyncBlockChanges(Block block, Material mat) {
        new Promise<>(subFinisher -> block.setType(mat), false);
    }
}

class ToDoEntry {

    @Getter private Block block;
    @Getter private int depth;
    @Getter private int percentage;
    @Getter private int maxDepthOffset;

    ToDoEntry(final Block block, final int depth, final int percentage, final int maxDepthOffset) {
        this.block = block;
        this.depth = depth;
        this.percentage = percentage;
        this.maxDepthOffset = maxDepthOffset;
    }
}

