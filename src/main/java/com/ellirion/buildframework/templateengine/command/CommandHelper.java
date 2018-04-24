package com.ellirion.buildframework.templateengine.command;

import org.bukkit.Location;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;

public class CommandHelper {

    /**
     * Gets world coordinates of a boundingbox.
     * @param box the box
     * @param location the location
     * @return an int array
     */
    public static int[] getCoordinates(BoundingBox box, Location location) {
        BoundingBox bbox = box.toWorld(new Point(location.getBlockX(), location.getBlockY(), location.getBlockZ()));

        return new int[] {
                bbox.getX1(),
                bbox.getX2(),
                bbox.getY1(),
                bbox.getY2(),
                bbox.getZ1(),
                bbox.getZ2()
        };
    }
}
