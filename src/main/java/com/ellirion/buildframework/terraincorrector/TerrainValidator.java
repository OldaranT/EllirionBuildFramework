package com.ellirion.buildframework.terraincorrector;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.List;

import static com.ellirion.buildframework.terraincorrector.util.HoleUtil.*;

public class TerrainValidator {

    private static final BuildFramework BUILD_FRAMEWORK = BuildFramework.getInstance();
    private static final FileConfiguration CONFIG = BUILD_FRAMEWORK.getConfig();
    private static final FileConfiguration BLOCK_VALUE_CONFIG = BUILD_FRAMEWORK.getBlockValueConfig();
    private static final String maxHoleDepthConfigPath = "TerrainCorrecter.MaxHoleDepth";
    private static final int depthOffset = CONFIG.getInt(maxHoleDepthConfigPath, 5);
    private World world;
    private BoundingBox boundingBox;

    /**
     * Validate whether the impact on the terrain is within acceptable levels.
     * @param boundingBox this should be a BoundingBox using world coordinates.
     *         The bottom of the of the BoundingBox should be flush with the ground
     * @param world the world that should
     * @return returns whether the terrain chances to the terrain will be within acceptable levels
     */
    public boolean validate(final BoundingBox boundingBox, final World world) {
        final double overhangLimit = CONFIG.getInt("TerrainCorrector.OverheadLimit", 50);
        final double blocksLimit = CONFIG.getInt("TerrainCorrector.BlocksLimit", 100);
        final double totalLimit = CONFIG.getInt("TerrainCorrector.TotalLimit", 200);
        final int offset = CONFIG.getInt("TerrainCorrector.Offset", 5);
        this.world = world;
        this.boundingBox = boundingBox;

        if (checkForBoundingBoxes()) {
            return false;
        }

        final double overhangScore = calculateOverhang();

        if (overhangScore >= overhangLimit) {
            return false;
        }

        final double blocksScore = calculateBlocks(offset);
        if (blocksScore >= blocksLimit) {
            return false;
        }

        List<Hole> holes = findHoles(world, boundingBox, 0, depthOffset);
        for (Hole h : holes) {
            List<Block> blocks = h.getBlockList();
            if (checkForRiver(blocks)) {
                return false;
            }
        }

        return !(blocksScore + overhangScore >= totalLimit);
    }

    private boolean checkForBoundingBoxes() {
        final int checkRadius = CONFIG.getInt("TerrainCorrector.BoundingBoxMinDist", 5);

        Point point1 = new Point(boundingBox.getX1() - checkRadius, boundingBox.getY1() - checkRadius,
                                 boundingBox.getZ1() - checkRadius);
        Point point2 = new Point(boundingBox.getX2() + checkRadius, boundingBox.getY2() + checkRadius,
                                 boundingBox.getZ2() + checkRadius);

        BoundingBox boundingBoxWithOffset = new BoundingBox(point1, point2);

        for (BoundingBox listItem : TerrainManager.getBoundingBoxes()) {
            if (listItem.intersects(boundingBoxWithOffset)) {
                return true;
            }
        }
        return false;
    }

    private double calculateOverhang() {
        double total = 0D;

        final double totalArea = boundingBox.getWidth() * boundingBox.getDepth();
        final int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z < boundingBox.getZ2(); z++) {

                final Block block = world.getBlockAt(x, y, z);

                if (block.isLiquid() || block.isEmpty()) {

                    final double distance = findClosestBlock(new Point(x, y, z));
                    final Double score = calculateOverhangScore(totalArea, distance);
                    total += score;
                }
            }
        }
        return total;
    }

    private double calculateOverhangScore(final double area, final double distance) {
        return (distance / area) * 75;
    }

    private double calculateBlocks(final int offset) {

        double blockCounter = 0;

        final int bottomBlockX = boundingBox.getX1() - offset;
        final int topBlockX = boundingBox.getX2() + offset;

        final int bottomBlockY = boundingBox.getY1();
        final int topBlockY = boundingBox.getY2() + offset;

        final int bottomBlockZ = boundingBox.getZ1() - offset;
        final int topBlockZ = boundingBox.getZ2() + offset;

        for (int x = bottomBlockX; x <= topBlockX; x++) {

            for (int y = bottomBlockY; y <= topBlockY; y++) {

                for (int z = bottomBlockZ; z <= topBlockZ; z++) {

                    final Block b = world.getBlockAt(x, y, z);

                    if (b.isLiquid()) {
                        return Double.POSITIVE_INFINITY;
                    }
                    if (!b.isEmpty()) {
                        blockCounter += BLOCK_VALUE_CONFIG.getInt(b.getType().toString(), 1);
                    }
                }
            }
        }

        return blockCounter;
    }

    private double findClosestBlock(final Point startingPosition) {

        final double x = startingPosition.getX();

        double finalDistance = Double.POSITIVE_INFINITY;

        for (double loopX = x; loopX <= boundingBox.getX2(); loopX++) {
            finalDistance = loopTroughBlocks(finalDistance, loopX, startingPosition);
        }

        for (double loopX = x; loopX >= boundingBox.getX1(); loopX--) {

            finalDistance = loopTroughBlocks(finalDistance, loopX, startingPosition);
        }

        return finalDistance;
    }

    private double loopTroughBlocks(double currentDistance, final double x, final Point startingPosition) {

        final double z = startingPosition.getZ();
        final double y = boundingBox.getY1() - 1;

        for (double loopZ = z; loopZ <= boundingBox.getZ2(); loopZ++) {

            final double distance = startingPosition.distanceManhattan(new Point(x, y, loopZ));

            if (currentDistance < distance) {
                break;
            }

            final Block block = world.getBlockAt((int) x, (int) y, (int) loopZ);
            if (!block.isEmpty() && !block.isLiquid()) {
                currentDistance = distance;
            }
        }

        for (double loopZ = z; loopZ >= boundingBox.getZ1(); loopZ--) {

            final double distance = startingPosition.distanceManhattan(new Point(x, y, loopZ));
            if (currentDistance < distance) {
                break;
            }

            final Block block = world.getBlockAt((int) x, (int) y, (int) loopZ);
            if (!block.isEmpty() && !block.isLiquid()) {
                currentDistance = distance;
            }
        }

        return currentDistance;
    }

    private boolean checkForRiver(final List<Block> blocks) {
        final int minX = boundingBox.getX1();
        final int maxX = boundingBox.getX2();
        final int minZ = boundingBox.getZ1();
        final int maxZ = boundingBox.getZ2();

        for (Block b : blocks) {
            if (b.isLiquid() &&
                ((b.getX() == minX && getRelativeBlock(3, b, world).isLiquid()) ||
                 (b.getX() == maxX && getRelativeBlock(1, b, world).isLiquid()) ||
                 (b.getZ() == minZ && getRelativeBlock(0, b, world).isLiquid()) ||
                 (b.getZ() == maxZ && getRelativeBlock(2, b, world).isLiquid()))) {
                return true;
            }
        }

        return false;
    }
}
