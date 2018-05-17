package com.ellirion.buildframework.terraincorrector;

import lombok.Getter;
import com.ellirion.buildframework.model.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class TerrainManager {

    @Getter private static List<BoundingBox> BOUNDING_BOXES = new ArrayList<>();
}
