package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.material.MaterialData;
import com.ellirion.buildframework.model.Point;

public class DoorWrapper {

    @Getter @Setter private MaterialData materialData;
    @Getter @Setter private Byte top;
    @Getter @Setter private Byte bottom;
    @Getter @Setter private Point point;

    /**
     * Constructer.
     * @param materialData materialdata of a door.
     * @param top top part of a door.
     * @param bottom bottom part of a door.
     * @param point point of the door.
     */
    public DoorWrapper(final MaterialData materialData, final Byte top, final Byte bottom, final Point point) {
        this.materialData = materialData;
        this.top = top;
        this.bottom = bottom;
        this.point = point;
    }
}
