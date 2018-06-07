package com.ellirion.buildframework.terraincorrector;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.model.Hole;
import com.ellirion.buildframework.terraincorrector.model.TerrainValidatorModel;

import java.util.List;

import static com.ellirion.buildframework.terraincorrector.util.HoleUtil.*;
import static com.ellirion.buildframework.util.WorldHelper.*;
import static org.bukkit.block.BlockFace.*;

public class TerrainValidator {

    private static final BuildFramework BUILD_FRAMEWORK = BuildFramework.getInstance();
    private static final FileConfiguration CONFIG = BUILD_FRAMEWORK.getConfig();
    private static final FileConfiguration BLOCK_VALUE_CONFIG = BUILD_FRAMEWORK.getBlockValueConfig();
    private static final int DEPTH_OFFSET = CONFIG.getInt("TerrainCorrecter.MaxHoleDepth", 5);
    private static final int BOUNDING_BOX_CHECK_RADIUS = CONFIG.getInt("TerrainCorrector.BoundingBoxMinDist", 5);
    private World world;
    private BoundingBox boundingBox;

    /**
     * Validate whether the impact on the terrain is within acceptable levels.
     * @param boundingBox this should be a BoundingBox using world coordinates.
     *         The bottom of the of the BoundingBox should be flush with the ground
     * @param world the world that should
     * @return returns whether the terrain chances to the terrain will be within acceptable levels
     */
    public TerrainValidatorModel validate(final BoundingBox boundingBox, final World world) {
        TerrainValidatorModel model = new TerrainValidatorModel(true);
        final double overhangLimit = CONFIG.getInt("TerrainCorrector.OverheadLimit", 50);
        final double blocksLimit = CONFIG.getInt("TerrainCorrector.BlocksLimit", 100);
        final double totalLimit = CONFIG.getInt("TerrainCorrector.TotalLimit", 200);
        final int offset = CONFIG.getInt("TerrainCorrector.Offset", 5);
        this.world = world;
        this.boundingBox = boundingBox;

        if (checkForBoundingBoxes()) {
            model.getErrors().add(String.format("Another object that has been placed is within %d blocks",
                                                BOUNDING_BOX_CHECK_RADIUS));
            model.setSucceeded(false);
        }

        List<Hole> holes = findHoles(world, boundingBox, 0, DEPTH_OFFSET);
        for (Hole h : holes) {
            List<Block> blocks = h.getBlockList();
            if (checkForRiver(blocks)) {
                model.getErrors().add("The selected area was above a river or lake");
                model.setSucceeded(false);
            }
        }

        final double overhangScore = calculateOverhang();

        if (overhangScore >= overhangLimit) {
            model.getErrors().add("The amount of blocks that needs to be filled is too high");
            model.setSucceeded(false);
        }

        final double blocksScore = calculateBlocks(offset);
        if (blocksScore >= blocksLimit) {
            model.getErrors().add("The amount of blocks that needs to be cleared is too much");
            model.setSucceeded(false);
        }

        // This step also needs to check if the model has succeeded thus far
        // Because we don't want to add unnecessary information.
        if (blocksScore + overhangScore >= totalLimit && model.isSucceeded()) {
            model.getErrors().add("the amount of blocks that need to be changed is too high");
            model.setSucceeded(false);
        }

        return model;
    }

    private boolean checkForBoundingBoxes() {
        Point point1 = new Point(boundingBox.getX1() - BOUNDING_BOX_CHECK_RADIUS,
                                 boundingBox.getY1() - BOUNDING_BOX_CHECK_RADIUS,
                                 boundingBox.getZ1() - BOUNDING_BOX_CHECK_RADIUS);
        Point point2 = new Point(boundingBox.getX2() + BOUNDING_BOX_CHECK_RADIUS,
                                 boundingBox.getY2() + BOUNDING_BOX_CHECK_RADIUS,
                                 boundingBox.getZ2() + BOUNDING_BOX_CHECK_RADIUS);

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
                final Block block = getBlock(world, x, y, z);

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
        final double volume = boundingBox.getDepth() * boundingBox.getWidth() * boundingBox.getHeight();

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int y = bottomBlockY; y <= topBlockY; y++) {
                for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                    final Block b = getBlock(world, x, y, z);

                    if (b.isLiquid()) {
                        return Double.POSITIVE_INFINITY;
                    }
                    if (!b.isEmpty()) {
                        blockCounter += BLOCK_VALUE_CONFIG.getInt(b.getType().toString(), 1);
                    }
                }
            }
        }

        return calculateBlockScore(volume, blockCounter);
    }

    private double calculateBlockScore(final double volume, final double blockCounter) {
        return (blockCounter / volume) * 75;
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

            final Block block = getBlock(world, (int) x, (int) y, (int) loopZ);
            if (!block.isEmpty() && !block.isLiquid()) {
                currentDistance = distance;
            }
        }

        for (double loopZ = z; loopZ >= boundingBox.getZ1(); loopZ--) {

            final double distance = startingPosition.distanceManhattan(new Point(x, y, loopZ));
            if (currentDistance < distance) {
                break;
            }

            final Block block = getBlock(world, (int) x, (int) y, (int) loopZ);
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
                ((b.getX() == minX && getRelativeBlock(WEST, b, world).isLiquid()) ||
                 (b.getX() == maxX && getRelativeBlock(EAST, b, world).isLiquid()) ||
                 (b.getZ() == minZ && getRelativeBlock(NORTH, b, world).isLiquid()) ||
                 (b.getZ() == maxZ && getRelativeBlock(SOUTH, b, world).isLiquid()))) {
                return true;
            }
        }

        return false;
    }
}
