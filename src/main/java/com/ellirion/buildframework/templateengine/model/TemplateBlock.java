package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class TemplateBlock {
    /**
     * The actual block.
     */
    @Getter
    @Setter
    private Material material;

    /**
     * ID.
     */
    @Accessors
    private int templateBlockID;

    /**
     * All markers assigned to this block.
     */
    @Accessors
    private List<String> markers;

    /**
     * If the block has metadata, we can store it here.
     */
    @Nullable
    @Getter
    @Setter
    private MaterialData metadata;

    @Getter
    @Setter
    private NBTTagCompound data;

    /**
     *
     * @param material create a templateblock with the given block
     */
    public TemplateBlock(final Material material) {
        this.material = material;

        markers = new LinkedList<String>();
    }

    /**
     *
     * @param marker The marker to add to this block
     * @return whether the marker was added
     */
    protected boolean addMarker(final String marker) {
        return markers.add(marker);
    }
}
