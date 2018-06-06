package com.ellirion.buildframework.terraincorrector;

import com.deliveredtechnologies.rulebook.FactMap;
import com.deliveredtechnologies.rulebook.Result;
import com.deliveredtechnologies.rulebook.lang.RuleBookBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.model.Hole;
import com.ellirion.buildframework.terraincorrector.rulebook.RavineSupportsRuleBook;
import com.ellirion.buildframework.util.MinecraftHelper;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.transact.SequenceTransaction;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.ellirion.buildframework.terraincorrector.util.HoleUtil.*;
import static com.ellirion.buildframework.util.WorldHelper.*;

public class TerrainCorrector {

    private static final FileConfiguration CONFIG = BuildFramework.getInstance().getConfig();
    private static final String maxHoleDepthConfigPath = "TerrainCorrector.MaxHoleDepth";
    private static final String areaLimitOffsetConfigPath = "TerrainCorrector.AreaLimitOffset";
    private static final int offset = CONFIG.getInt(areaLimitOffsetConfigPath, 5);
    private static final int depthOffset = CONFIG.getInt(maxHoleDepthConfigPath, 5);
    private static final int bridgeSupportClearancePercentage = CONFIG.getInt(
            "TerrainCorrector.BridgeCenterSupportClearancePercentage", 15);
    private final List<Transaction> transactions = new ArrayList<>();

    private BoundingBox boundingBox;
    private World world;

    /**
     * Makes room for the object in the {@code boundingBox} by clearing out the terrain
     * and makes the floor solid or builds supports.
     * @param boundingBox the BoundingBox that will be used for terrain smoothing.
     * @param world The world in which the changes need to happen
     * @param player The player that wants to change the world
     * @return the {@link Promise} in which the correction will be executed
     */

    public Promise correctTerrain(BoundingBox boundingBox, World world, Player player) {
        return new Promise<>(finisher -> {
            this.boundingBox = boundingBox;
            this.world = world;
            List<Hole> holes = findHoles(world, boundingBox, offset, depthOffset);

            for (Hole h : holes) {
                correctHole(h);
            }

            List<Block> toRemove = getBlocksInBoundingBox();
            setListToAir(toRemove);

            TransactionManager.addDoneTransaction(player,
                                                  new SequenceTransaction(true,
                                                                          transactions.toArray(new Transaction[0])));
            finisher.resolve(null);
        }, true);
    }

    private BlockData getFloorMaterial() {
        Map<BlockData, Integer> materials = new HashMap<>();

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {

                Block b = getBlock(world, x, boundingBox.getY1() - 1, z);
                BlockData data = new BlockData(b.getType(), b.getData());

                if (materials.containsKey(data)) {
                    materials.replace(data, materials.get(data) + 1);
                } else {
                    materials.put(data, 1);
                }
            }
        }

        int max = 0;
        BlockData data = null;
        for (Map.Entry<BlockData, Integer> entry : materials.entrySet()) {
            if (entry.getValue() > max && entry.getKey().material.isSolid()) {
                max = entry.getValue();
                data = entry.getKey();
            }
        }

        if (data == null) {
            return new BlockData(Material.DIRT, (byte) 0);
        }
        return data;
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
        BlockData data = getFloorMaterial();
        List<Block> blocks = hole.getTopBlocks();
        Map<Block, Integer> startingDepthMap = calculateStartingDepthMap(blocks);

        placeBlocksAccordingToDepthMap(startingDepthMap, data);
    }

    private Map<Block, Integer> calculateStartingDepthMap(List<Block> blocks) {
        int minimalFillingWidth = 2;
        int maxDepth = CONFIG.getInt("TerrainCorrector.HoleFillerMaxDepth", 5);
        int chancePercentage = CONFIG.getInt("TerrainCorrector.HoleFillerChanceToChangeDepth", 10);

        Map<Block, Integer> result = new HashMap<>();
        LinkedList<ToDoEntry> todo = new LinkedList<>();
        Block block = blocks.stream().filter(x -> blocksBelowBoundingBoxOrWithinOffset(x, 0)).findAny().get();
        ToDoEntry entry = new ToDoEntry(block, 0, chancePercentage, 0);
        todo.add(entry);

        while ((entry = todo.poll()) != null) {
            exploreDepthOfAdjacentBlocks(entry, result, todo, minimalFillingWidth, maxDepth, chancePercentage);
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

        // Loop through the adjacent blocks
        for (int dir = 0; dir < 4; dir++) {
            Block nextBlock = getRelativeBlock(dir, currentEntry.getBlock(), world);

            int percentage = currentEntry.getPercentage();
            int maxOffset = currentEntry.getMaxDepthOffset();
            int depth = currentEntry.getDepth() + 1;

            if (!blocksBelowBoundingBoxOrWithinOffset(nextBlock, minWidth)) {

                // Introduce randomization for blocks not below the bounding box
                Double rand = ThreadLocalRandom.current().nextDouble(0, 100);

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

    private void placeBlocksAccordingToDepthMap(Map<Block, Integer> map, BlockData data) {
        for (Map.Entry entry : map.entrySet()) {
            fillDownwards((Block) entry.getKey(), data, (int) entry.getValue());
        }
    }

    private void fillDownwards(Block block, BlockData data, int startDepth) {
        Block currentBlock = getBlock(world, block.getX(), block.getY() - startDepth, block.getZ());

        while (currentBlock.isEmpty() || !MinecraftHelper.isPathSolid(currentBlock.getType())) {
            sendSyncBlockChanges(currentBlock, data);
            currentBlock = getRelativeBlock(5, currentBlock, world);
        }
    }

    private boolean blocksBelowBoundingBoxOrWithinOffset(Block block, int offset) {
        return (block.getX() >= boundingBox.getX1() - offset &&
                block.getX() <= boundingBox.getX2() + offset &&
                block.getZ() >= boundingBox.getZ1() - offset &&
                block.getZ() <= boundingBox.getZ2() + offset);
    }

    private void fillHoleAtSide(Hole hole) {
        BlockData data = getFloorMaterial();

        for (Block b : hole.getBlockList()) {
            sendSyncBlockChanges(b, data);
        }
    }

    private void fillBasicHole(Hole hole) {
        List<Block> topBlocks = hole.getTopBlocks();

        for (Block b : topBlocks) {
            sendSyncBlockChanges(b, new BlockData(Material.BARRIER, (byte) 0));
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
                    final Block b = getBlock(world, x, y, z);
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
            sendSyncBlockChanges(b, new BlockData(Material.AIR, (byte) 0));
        }
    }

    private void buildRavineSupports(Hole hole) {

        RavineSupportsRuleBook ravineSupportsRuleBook = (RavineSupportsRuleBook) RuleBookBuilder
                .create(RavineSupportsRuleBook.class)
                .withResultType(Integer.class)
                .withDefaultResult(Integer.MAX_VALUE)
                .build();

        FactMap ravineSupportsFacts = new FactMap();

        int minX = boundingBox.getX1();
        int maxX = boundingBox.getX2();
        int minZ = boundingBox.getZ1();
        int maxZ = boundingBox.getZ2();

        ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMinX(), minX);
        ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMaxX(), maxX);
        ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMinZ(), minZ);
        ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMaxZ(), maxZ);

        int minHoleX;
        int maxHoleX;
        int minHoleZ;
        int maxHoleZ;

        List<Block> topBlocks = hole.getTopBlocks();
        List<Hole> subHoles = new ArrayList<>();

        for (Block b : topBlocks) {
            int blockX = b.getX();
            int blockZ = b.getZ();
            if (!(blockX < minX || blockX > maxX || blockZ < minZ || blockZ > maxZ) &&
                subHoles.stream().noneMatch(subHole -> subHole.contains(b))) {
                Hole h = new Hole(b);
                LinkedList<Block> todo = new LinkedList<>();
                todo.add(b);
                Block current;
                while ((current = todo.poll()) != null) {
                    exploreAdjacentNonSolidBlocks(current, h, todo, true, offset, boundingBox, depthOffset, world);
                }
                subHoles.add(h);
            }
        }

        for (Hole h : subHoles) {
            minHoleX = h.getMinX();
            maxHoleX = h.getMaxX();
            minHoleZ = h.getMinZ();
            maxHoleZ = h.getMaxZ();

            ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMinHoleX(), h.getMinX());
            ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMaxHoleX(), h.getMaxX());
            ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMinHoleZ(), h.getMinZ());
            ravineSupportsFacts.setValue(RavineSupportsRuleBook.getMaxHoleZ(), h.getMaxZ());

            // Run the values through the rules in the rulebook.
            ravineSupportsRuleBook.run(ravineSupportsFacts);
            // If the rulebook returns a result get the to change blocks
            if (ravineSupportsRuleBook.getResult().isPresent()) {
                Result result = ravineSupportsRuleBook.getResult().get();
                List<Block> toChange = supportSelector((int) result.getValue(), minHoleX, maxHoleX, minHoleZ, maxHoleZ);
                for (Block b : toChange) {
                    sendSyncBlockChanges(b, new BlockData(Material.FENCE, (byte) 0));
                }
            }
        }
    }

    private List<Block> getBlocksBelow(Block b, int depth) {
        List<Block> result = new ArrayList<>();
        Block current = getBlock(world, b.getX(), b.getY() - 1, b.getZ());

        for (int i = 0; i < depth; i++) {
            if (!current.isEmpty() && !current.isLiquid()) {
                break;
            }
            result.add(current);
            current = getBlock(world, current.getX(), current.getY() - 1, current.getZ());
        }

        return result;
    }

    private List<Block> getBridgeSupport(int dir, int minHoleX, int maxHoleX,
                                         int minHoleZ, int maxHoleZ) {
        int holeCentre;
        int y = boundingBox.getY1() - 1;
        List<Block> toChange = new ArrayList<>();
        int maxDepth;
        double centerClearance;
        switch (dir) {
            case 0:
                // Z-axis
                holeCentre = maxHoleZ - ((maxHoleZ - minHoleZ) / 2);
                maxDepth = (maxHoleZ - minHoleZ) / 2;
                centerClearance = ((double) maxDepth / 100D) * bridgeSupportClearancePercentage;
                for (int x = minHoleX; x <= maxHoleX; x++) {
                    if (Math.abs(x) % 2 == 0) {
                        for (int i = 0; i <= maxDepth; i++) {
                            if (holeCentre + centerClearance + i > maxHoleZ ||
                                holeCentre - centerClearance - i < minHoleZ) {
                                break;
                            }
                            toChange.addAll(
                                    blocksToReplace(x, y, holeCentre + (int) centerClearance + i, i));
                            toChange.addAll(
                                    blocksToReplace(x, y, holeCentre - (int) centerClearance - i, i));
                        }
                    }
                }
                return toChange;
            case 1:
                // X-axis
                holeCentre = maxHoleX - ((maxHoleX - minHoleX) / 2);
                maxDepth = (maxHoleX - minHoleX) / 2;
                centerClearance = ((double) maxDepth / 100D) * bridgeSupportClearancePercentage;
                for (int z = minHoleZ; z <= maxHoleZ; z++) {
                    if (Math.abs(z) % 2 == 0) {
                        for (int i = 0; i <= maxDepth; i++) {
                            if (holeCentre + centerClearance + i > maxHoleX ||
                                holeCentre - centerClearance - i < minHoleX) {
                                break;
                            }
                            toChange.addAll(
                                    blocksToReplace(holeCentre + (int) centerClearance + i, y, z, i));
                            toChange.addAll(
                                    blocksToReplace(holeCentre - (int) centerClearance - i, y, z, i));
                        }
                    }
                }
                return toChange;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    private List<Block> blocksToReplace(int x, int y, int z, int depth) {
        List<Block> toChange = new ArrayList<>();
        Block b = getBlock(world, x, y, z);
        if (blocksBelowBoundingBoxOrWithinOffset(b, 0) && (b.isLiquid() || b.isEmpty())) {
            if (!toChange.contains(b)) {
                toChange.add(b);
            }
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

        // Keep going until we've shrunk to zero (or lower) width
        while (minX <= maxX && minZ <= maxZ) {

            // Trace down if it is not done
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    // If it's a solid, this column is done
                    Location loc = new Point(x1 + x, baseY - offsetY,
                                             z1 + z).toLocation(world);
                    Block block = getBlock(loc);
                    if (block.getType().isSolid()) {
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

    private List<Block> oneSidedSupportMap(int dir, int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {
        List<Block> toChange = new ArrayList<>();
        int y = boundingBox.getY1() - 1;
        int maxDepth;
        int centre;
        int distance;

        switch (dir) {
            case 0:
                // North to south
                maxDepth = maxHoleZ - minHoleZ;
                centre = maxHoleX - ((maxHoleX - minHoleX) / 2);
                distance = (maxHoleX - minHoleX) / 2;
                if (Math.abs(distance) % 2 == 1) {
                    distance++;
                }
                for (int dis = 0; dis <= distance; dis += 2) {
                    for (int i = 0; i <= maxDepth; i++) {
                        toChange.addAll(blocksToReplace(centre + dis, y, minHoleZ + i, i));
                        toChange.addAll(blocksToReplace(centre - dis, y, minHoleZ + i, i));
                    }
                }
                return toChange;
            case 1:
                // East to west
                maxDepth = maxHoleX - minHoleX;
                centre = maxHoleZ - ((maxHoleZ - minHoleZ) / 2);
                distance = (maxHoleZ - minHoleZ) / 2;
                if (Math.abs(distance) % 2 == 1) {
                    distance++;
                }
                for (int dis = 0; dis <= distance; dis += 2) {
                    for (int i = 0; i <= maxDepth; i++) {
                        toChange.addAll(blocksToReplace(maxHoleX - i, y, centre + dis, i));
                        toChange.addAll(blocksToReplace(maxHoleX - i, y, centre - dis, i));
                    }
                }
                return toChange;
            case 2:
                // South to north
                maxDepth = maxHoleZ - minHoleZ;
                centre = maxHoleX - ((maxHoleX - minHoleX) / 2);
                distance = (maxHoleX - minHoleX) / 2;
                if (Math.abs(distance) % 2 == 1) {
                    distance++;
                }
                for (int dis = 0; dis <= distance; dis += 2) {
                    for (int i = 0; i <= maxDepth; i++) {
                        toChange.addAll(blocksToReplace(centre + dis, y, maxHoleZ - i, i));
                        toChange.addAll(blocksToReplace(centre - dis, y, maxHoleZ - i, i));
                    }
                }
                return toChange;
            case 3:
                // West to east
                maxDepth = maxHoleX - minHoleX;
                centre = maxHoleZ - ((maxHoleZ - minHoleZ) / 2);
                distance = (maxHoleZ - minHoleZ) / 2;
                if (Math.abs(distance) % 2 == 1) {
                    distance++;
                }
                for (int dis = 0; dis <= distance; dis += 2) {
                    for (int i = 0; i <= maxDepth; i++) {
                        toChange.addAll(blocksToReplace(minHoleX + i, y, centre + dis, i));
                        toChange.addAll(blocksToReplace(minHoleX + i, y, centre - dis, i));
                    }
                }
                return toChange;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    private List<Block> cornerSupportMap(int dir, int minHoleX, int maxHoleX, int minHoleZ, int maxHoleZ) {
        List<Block> toChange = new ArrayList<>();
        int y = boundingBox.getY1() - 1;
        int widthX = maxHoleX - minHoleX;
        int widthZ = maxHoleZ - minHoleZ;
        int maxDepth = widthX > widthZ ? widthX : widthZ;

        switch (dir) {
            case 0:
                // North east to south west
                for (int i = 0; i <= maxDepth; i++) {
                    for (int x = maxHoleX - i; x >= minHoleX; x--) {
                        for (int z = minHoleZ + i; z <= maxHoleZ; z++) {
                            Block b = getBlock(world, x, y - i, z);
                            if ((!b.isEmpty() && !b.isLiquid())) {
                                continue;
                            }
                            toChange.add(b);
                        }
                    }
                }
                return toChange;
            case 1:
                // South east to north west
                for (int i = 0; i <= maxDepth; i++) {
                    for (int x = maxHoleX - i; x >= minHoleX; x--) {
                        for (int z = maxHoleZ - i; z >= minHoleZ; z--) {
                            Block b = getBlock(world, x, y - i, z);
                            if ((!b.isEmpty() && !b.isLiquid())) {
                                continue;
                            }
                            toChange.add(b);
                        }
                    }
                }
                return toChange;
            case 2:
                // South west to north east
                for (int i = 0; i <= maxDepth; i++) {
                    for (int x = minHoleX + i; x <= maxHoleX; x++) {
                        for (int z = maxHoleZ - i; z >= minHoleZ; z--) {
                            Block b = getBlock(world, x, y - i, z);
                            if ((!b.isEmpty() && !b.isLiquid())) {
                                continue;
                            }
                            toChange.add(b);
                        }
                    }
                }
                return toChange;
            case 3:
                // North west to south east
                for (int i = 0; i <= maxDepth; i++) {
                    for (int x = minHoleX + i; x <= maxHoleX; x++) {
                        for (int z = minHoleZ + i; z <= maxHoleZ; z++) {
                            Block b = getBlock(world, x, y - i, z);
                            if ((!b.isEmpty() && !b.isLiquid())) {
                                continue;
                            }
                            toChange.add(b);
                        }
                    }
                }
                return toChange;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    private void sendSyncBlockChanges(Block block, BlockData data) {
        transactions.add(setBlock(block.getLocation(), data.getMaterial(), data.getData()));
    }

    private List<Block> supportSelector(int method, int minHoleX, int maxHoleX,
                                        int minHoleZ, int maxHoleZ) {
        switch (method) {
            // Bridge style supports
            case 0:
            case 1:
                return getBridgeSupport(method, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
            // One sided supports
            case 2:
            case 3:
            case 4:
            case 5:
                return oneSidedSupportMap(method - 2, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
            // Corner supports
            case 6:
            case 7:
            case 8:
            case 9:
                return cornerSupportMap(method - 6, minHoleX, maxHoleX, minHoleZ, maxHoleZ);
            default:
                // For unexpected scenario's just fill from outside in on all sides.
                // Build building supports under the bounding box from all sides inwards.
                return createSupportsLocationMap();
        }
    }

    private static class ToDoEntry {

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

    private static class BlockData {

        @Getter private Material material;
        @Getter private byte data;

        BlockData(final Material material, final byte data) {
            this.material = material;
            this.data = data;
        }

        @Override
        public int hashCode() {
            return (material.getId() * 100) + data;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }

            if (!(obj instanceof BlockData)) {
                return false;
            }

            BlockData other = (BlockData) obj;

            if (!material.equals(other.getMaterial())) {
                return false;
            }

            if (data != other.getData()) {
                return false;
            }

            return true;
        }
    }
}



