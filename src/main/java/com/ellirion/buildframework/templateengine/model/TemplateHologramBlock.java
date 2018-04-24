package com.ellirion.buildframework.templateengine.model;

import lombok.Setter;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

public class TemplateHologramBlock {

    @Getter @Setter private Location loc;
    @Getter @Setter private Material mat;

    /**
     * Creates a Template hologram block.
     * @param location the location of the hologram block
     * @param material the material of the hologram block
     */
    public TemplateHologramBlock(final Location location, final Material material) {
        loc = location.clone();
        mat = material;
    }
}
