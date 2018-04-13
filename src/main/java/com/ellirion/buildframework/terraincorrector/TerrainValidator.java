package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;
import org.bukkit.World;

public class TerrainValidator {
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

        if
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
