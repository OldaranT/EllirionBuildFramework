package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.material.MaterialData;
import com.ellirion.buildframework.model.Point;

public class DoorWrapper {

    @Getter @Setter private MaterialData bottomMaterialData;
    @Getter @Setter private MaterialData topMaterialData;
    @Getter @Setter private Point point;

    /**
     * Constructor.
     * @param bottomMaterialData Materialdata of bottom part of the door.
     * @param topMaterialData Materialdata of top part of the door.
     * @param point point of the door.
     */
    public DoorWrapper(final MaterialData bottomMaterialData, final MaterialData topMaterialData, final Point point) {
        this.bottomMaterialData = bottomMaterialData;
        this.topMaterialData = topMaterialData;
        this.point = point;
    }
}
