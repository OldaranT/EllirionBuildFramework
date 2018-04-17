package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;

public class TerrainValidator {

    private double overhangLimit = 50;
    private double blocksLimit = 300;
    private double totalLimit = 400;

    /***
     *
     * @param boundingBox bla
     * @param world the world that should
     * @return returns whether the terrain allows terrain generation
     */
    public double validate(final BoundingBox boundingBox, final World world) {

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

        return 1;
    }

    private double calculateBlocks(final BoundingBox boundingBox, final World world) {

        double blockCounter = 0;

        Location l1 = new Location(world, boundingBox.getX1(), boundingBox.getY1(), boundingBox.getZ1());
        Location l2 = new Location(world, boundingBox.getX2(), boundingBox.getY2(), boundingBox.getZ2());

        int topBlockX = (l1.getBlockX() < l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());

        int bottomBlockX = (l1.getBlockX() > l2.getBlockX() ? l2.getBlockX() : l1.getBlockX());


        int topBlockY = (l1.getBlockY() < l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());

        int bottomBlockY = (l1.getBlockY() > l2.getBlockY() ? l2.getBlockY() : l1.getBlockY());


        int topBlockZ = (l1.getBlockZ() < l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());

        int bottomBlockZ = (l1.getBlockZ() > l2.getBlockZ() ? l2.getBlockZ() : l1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {

            for (int y = bottomBlockY; y <= topBlockY; y++) {

                for (int z = bottomBlockZ; z <= topBlockZ; z++) {

                    if (l1.getWorld().getBlockAt(x, y, z).isLiquid()) {
                        return Double.POSITIVE_INFINITY;
                    }

                    if (!l1.getWorld().getBlockAt(x, y, z).isEmpty()) {
                        blockCounter++;
                    }
                }

            }

        }

        return blockCounter;
    }

    private boolean checkForBoundingBoxes() {
        return false;
    }


}
