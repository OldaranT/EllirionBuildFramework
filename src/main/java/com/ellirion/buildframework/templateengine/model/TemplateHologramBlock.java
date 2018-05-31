package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateHologramBlock)) {
            return false;
        }

        TemplateHologramBlock other = (TemplateHologramBlock) obj;

        if (!loc.equals(other.loc)) {
            return false;
        }

        if (!mat.equals(other.mat)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
