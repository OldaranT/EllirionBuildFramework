package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class TerrainManager {

    private static List<BoundingBox> BOUNDING_BOXES = new ArrayList<>();

    public static List<BoundingBox> getBoundingBoxes() {
        return BOUNDING_BOXES;
    }
}
