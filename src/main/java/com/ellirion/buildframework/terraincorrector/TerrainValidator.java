package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import net.minecraft.server.v1_12_R1.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.logging.Level;
import java.util.logging.Logger;


public class TerrainValidator {
    private static final Logger LOGGER = BuildFramework.getInstance().getLogger();

    /***
     * Validate if the impact on the terrain is within acceptable levels.
     * @param boundingBox this should be a BoundingBox using world coordinates where the bottom of the BoundingBox is
     * @param world the world that should
     * @return returns whether the terrain allows terrain generation
     */
    public boolean validate(final BoundingBox boundingBox, final World world) {
        final double overhangLimit = BuildFramework.getInstance().getConfig().getInt("TerrainValidation_OverheadLimit", 50);
        final double blocksLimit = BuildFramework.getInstance().getConfig().getInt("TerrainValidation_BocksLimit", 100);
        final double totalLimit = BuildFramework.getInstance().getConfig().getInt("TerrainValidation_TotalLimit", 200);
        final int offset = BuildFramework.getInstance().getConfig().getInt("Terrainvalidation_offset", 5);

        //TODO implement checking for BoundingBoxes in the world once these are saved in the database

        final double overhangScore = calculateOverhang(boundingBox, world);
        if (overhangScore > overhangLimit) {
            return false;
        }

        final double blocksScore = calculateBlocks(boundingBox, world, offset);
        if (blocksScore > blocksLimit) {
            return false;
        }


        if (blocksScore + overhangScore > totalLimit) {
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
                    final double distance = findClosestBlock(new Position(x, y, z), boundingBox, world);
                    final Double score = calculateOverhangScore(totalArea, distance);

                    total += score;
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("[TerrainValidator] Calculated score : " + score + " for x : " + x + " and y : " + y + " and z : " + z);
                    }
                }
            }
        }
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("[TerrainValidator] Total score : " + total);
        }
        return total;
    }

    private double calculateOverhangScore(final double area, final double distance) {
        return (distance / area);
    }

    /*
     * TODO: calculate using the type of block.
     * */
    private double calculateBlocks(final BoundingBox boundingBox, final World world, final int offset) {

        double blockCounter = 0;

        final Location l1 = new Location(world, boundingBox.getX1(), boundingBox.getY1(), boundingBox.getZ1());
        final Location l2 = new Location(world, boundingBox.getX2(), boundingBox.getY2(), boundingBox.getZ2());

        final int topBlockX = (l1.getBlockX() < l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());

        final int bottomBlockX = (l1.getBlockX() > l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());


        final int topBlockY = (l1.getBlockY() < l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());

        final int bottomBlockY = (l1.getBlockY() > l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());


        final int topBlockZ = (l1.getBlockZ() < l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());

        final int bottomBlockZ = (l1.getBlockZ() > l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());

        for (int x = bottomBlockX - offset; x <= topBlockX + offset; x++) {

            for (int y = bottomBlockY; y <= topBlockY + offset; y++) {

                for (int z = bottomBlockZ - offset; z <= topBlockZ + offset; z++) {

                    if (l1.getWorld().getBlockAt(x, y, z).isLiquid()) {
                        return Double.POSITIVE_INFINITY;
                    }

                    if (!l1.getWorld().getBlockAt(x, y, z).isEmpty()) {
                        blockCounter++;
                    }
                }

            }

        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("[TerrainValidator] Total block counter : " + blockCounter);
        }

        return blockCounter;
    }

    private double findClosestBlock(final Position startingPosition, final BoundingBox boundingBox, final World world) {

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

    private double loopTroughBlocks(double currentDistance, final World world, final double x, final BoundingBox boundingBox, final Position startingPosition) {

        final double z = startingPosition.getZ();
        final double y = boundingBox.getY1();

        for (double loopZ = z; loopZ <= boundingBox.getZ2(); loopZ++) {
            final Block block = world.getBlockAt((int) x, (int) y, (int) loopZ);
            final double distance = getDistance(startingPosition, new Position(x, y, loopZ));
            if (currentDistance < distance) {
                break;
            }
            if (!block.isEmpty() && !block.isLiquid()) {
                currentDistance = distance;
            }
        }

        for (double loopZ = z; loopZ >= boundingBox.getZ1(); loopZ--) {
            final Block block = world.getBlockAt((int) x, (int) y, (int) loopZ);
            final double distance = getDistance(startingPosition, new Position(x, y, loopZ));
            if (currentDistance < distance) {
                break;
            }
            if (!block.isEmpty() && !block.isLiquid()) {
                currentDistance = distance;
            }
        }

        return currentDistance;
    }


    private double getDistance(final Position p1, final Position p2) {
        final double x1 = Math.min(p1.getX(), p2.getX());
        final double y1 = Math.min(p1.getY(), p2.getY());
        final double z1 = Math.min(p1.getZ(), p2.getZ());

        final double x2 = Math.max(p1.getX(), p2.getX());
        final double y2 = Math.max(p1.getY(), p2.getY());
        final double z2 = Math.max(p1.getZ(), p2.getZ());

        return (x2 - x1) + (y2 - y1) + (z2 - z1);

    }
}
