package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;
import net.minecraft.server.v1_12_R1.Position;
import org.bukkit.World;
import org.bukkit.block.Block;

public class TerrainValidator {

    /***
     * Validate if the impact on the terrain is within acceptable levels.
     * @param boundingBox this should be a BoundingBox using world coordinates where the bottom of the BoundingBox is
     * @param world the world that should
     * @return returns whether the terrain allows terrain generation
     */
    public double validate(final BoundingBox boundingBox, final World world) {
        final double overhangLimit = 10;
        final double blocksLimit = 300;
        final double totalLimit = 400;

        if (checkForBoundingBoxes()) {
            return Double.POSITIVE_INFINITY;
        }

        if (calculateOverhang(boundingBox, world) > overhangLimit) {
            return Double.POSITIVE_INFINITY;
        }

        if (calculateBlocks(boundingBox, world) > blocksLimit) {
            return Double.POSITIVE_INFINITY;
        }

        final Double total = calculateBlocks(boundingBox, world) + calculateOverhang(boundingBox, world);

        if (total > totalLimit) {
            return Double.POSITIVE_INFINITY;
        }

        return total;
    }

    private double calculateOverhang(final BoundingBox boundingBox, final World world) {
        double total = 0D;
        double totalarea = 
        final int z = boundingBox.getZ1();
        for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
            for (int y = boundingBox.getY1(); x < boundingBox.getY2(); y++) {
                final Block block = world.getBlockAt(x, y, z);
                if (block.isLiquid() || block.isEmpty()) {
                    total += findClosestBlock(new Position(x, y, z), boundingBox, world);
                }
            }
        }
        return total;
    }

    private int calculateBlocks(final BoundingBox boundingBox, final World world) {
        return 1;
    }

    private boolean checkForBoundingBoxes() {
        return false;
    }

    /***
     *
     * @param startingPosition bla
     * @param boundingBox bla
     * @param world bla
     * @return bla
     */
    public double findClosestBlock(final Position startingPosition, final BoundingBox boundingBox, final World world) {

        final double x = startingPosition.getX();

        double finalDistance = Double.POSITIVE_INFINITY;

        for (double loopX = x;loopX <= boundingBox.getX2();loopX++)
        {
            finalDistance = loopTroughBlocks(finalDistance, world, loopX, boundingBox, startingPosition);
        }

        for (double loopX = x;loopX >= boundingBox.getX1();loopX--)
        {
            finalDistance = loopTroughBlocks(finalDistance, world, loopX, boundingBox, startingPosition);
        }

        return finalDistance;
    }

    private double loopTroughBlocks(double currentDistance, final World world, final double x, final BoundingBox boundingBox, final Position startingPosition) {

        final double z = startingPosition.getZ();
        final double y = boundingBox.getY1();

        for (double loopZ = z; loopZ <= boundingBox.getZ2(); loopZ++)
            {
            final Block block = world.getBlockAt((int) x, (int) y, (int) loopZ);
            final double distance = getDistance(startingPosition, new Position(x, y, loopZ));
            if (currentDistance < distance)
            {
                break;
            }
            if (!block.isEmpty() && !block.isLiquid())
            {
                currentDistance =  distance;
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


    private double getDistance(final Position p1, final Position p2)
    {
        final double x1 = Math.min(p1.getX(), p2.getX());
        final double y1 = Math.min(p1.getY(), p2.getY());
        final double z1 = Math.min(p1.getZ(), p2.getZ());

        final double x2 = Math.max(p1.getX(), p2.getX());
        final double y2 = Math.max(p1.getY(), p2.getY());
        final double z2 = Math.max(p1.getZ(), p2.getZ());

        return (x2 - x1) + (y2 - y1) + (z2 - z1);

    }
}
