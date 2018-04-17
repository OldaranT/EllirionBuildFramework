package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;
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

    private int calculateBlocks(final BoundingBox boundingBox, final World world) {
        return 1;
    }

    private boolean checkForBoundingBoxes() {
        return false;
    }

}
