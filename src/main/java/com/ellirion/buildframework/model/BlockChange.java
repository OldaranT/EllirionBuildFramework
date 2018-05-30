package com.ellirion.buildframework.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockChange {

    @Getter private Material material;
    @Getter private byte metaData;

    @Getter private Location location;

    /**
     * Create a BlockChange.
     * @param material the material that needs to be changed to
     * @param metaData the metaData that needs to be changed to
     * @param loc the location of the block
     */
    public BlockChange(final Material material, final byte metaData, final Location loc) {
        this.material = material;
        this.metaData = metaData;
        location = loc;
    }
}
