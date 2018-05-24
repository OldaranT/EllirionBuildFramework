package com.ellirion.buildframework.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockChange {

    @Getter private Material matBefore;
    @Getter private byte metadataBefore;

    @Getter private Material matAfter;
    @Getter private byte metadataAfter;

    @Getter private Location location;

    /**
     * Create a BlockChange.
     * @param before the material of the before state
     * @param bbefore the metadata of the before state
     * @param after the material of the after state
     * @param bafter the metadata of the after state
     * @param loc the location of the block
     */
    public BlockChange(final Material before, final byte bbefore, final Material after, final byte bafter,
                       final Location loc) {
        matBefore = before;
        metadataBefore = bbefore;
        matAfter = after;
        metadataAfter = bafter;
        location = loc;
    }
}
