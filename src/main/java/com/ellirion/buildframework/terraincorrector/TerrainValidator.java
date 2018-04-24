package com.ellirion.buildframework.terraincorrector;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;

public class TerrainValidator {

    private static final BuildFramework BUILD_FRAMEWORK = BuildFramework.getInstance();
    private static final FileConfiguration CONFIG = BUILD_FRAMEWORK.getConfig();
    private static final FileConfiguration BLOCK_VALUE_CONFIG = BUILD_FRAMEWORK.getBlockValueConfig();

    /**
     * Validate whether the impact on the terrain is within acceptable levels.
     * @param boundingBox this should be a BoundingBox using world coordinates.
     *                    The bottom of the of the BoundingBox should be flush with the ground
     * @param world the world that should
     * @return returns whether the terrain chances to the terrain will be within acceptable levels
     */
    public boolean validate(final BoundingBox boundingBox, final World world) {
        final double overhangLimit = CONFIG.getInt("TerrainValidation_OverheadLimit", 50);
        final double blocksLimit = CONFIG.getInt("TerrainValidation_BlocksLimit", 100);
        final double totalLimit = CONFIG.getInt("TerrainValidation_TotalLimit", 200);
        final int offset = CONFIG.getInt("TerrainValidation_Offset", 5);

        //TODO implement checking for BoundingBoxes in the world once these are saved in the database

        final double overhangScore = calculateOverhang(boundingBox, world);
        if (overhangScore >= overhangLimit) {
            return false;
        }

        final double blocksScore = calculateBlocks(boundingBox, world, offset);
        if (blocksScore >= blocksLimit) {
            return false;
        }

        if (blocksScore + overhangScore >= totalLimit) {
            return false;
        }

        return true;
    }

    private double calculateOverhang(final BoundingBox boundingBox, final World world) {
        double total = 0D;

        final double totalArea = boundingBox.getWidth() * boundingBox.getDepth();
        final int y = boundingBox.getY1() - 1;

        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int z = boundingBox.getZ1(); z < boundingBox.getZ2(); z++) {

                final Block block = world.getBlockAt(x, y, z);

                if (block.isLiquid() || block.isEmpty()) {

                    final double distance = findClosestBlock(new Point(x, y, z), boundingBox, world);
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

    private double calculateBlocks(final BoundingBox boundingBox, final World world, final int offset) {

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

    private double findClosestBlock(final Point startingPosition, final BoundingBox boundingBox, final World world) {

        final double x = startingPosition.getX();

        double finalDistance = Double.POSITIVE_INFINITY;

        for (double loopX = x; loopX <= boundingBox.getX2(); loopX++) {
            finalDistance = loopTroughBlocks(finalDistance, world, loopX, boundingBox, startingPosition);
        }

        for (double loopX = x; loopX >= boundingBox.getX1(); loopX--) {

            finalDistance = loopTroughBlocks(finalDistance, world, loopX, boundingBox, startingPosition);
        }

        return finalDistance;
    }

    private double loopTroughBlocks(double currentDistance, final World world, final double x,
                                    final BoundingBox boundingBox, final Point startingPosition) {

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
}
