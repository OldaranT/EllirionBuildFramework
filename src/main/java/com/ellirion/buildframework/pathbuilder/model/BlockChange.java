package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockChange {

    @Getter private Material matAfter;
    @Getter private byte metadataAfter;

    @Getter private Location location;

    /**
     * Create a BlockChange.
     * @param mat material
     * @param meta metadata
     * @param loc location
     */
    public BlockChange(final Material mat, final byte meta, final Location loc) {
        matAfter = mat;
        metadataAfter = meta;
        location = loc;
    }
}
