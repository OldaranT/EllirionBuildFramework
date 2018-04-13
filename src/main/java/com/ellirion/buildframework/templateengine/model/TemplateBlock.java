package com.ellirion.buildframework.templateengine.model;

import lombok.experimental.Accessors;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.List;

public class TemplateBlock {
    /**
     * The actual block
     */
    public Block block;

    /**
     * ID
     */
    @Accessors
    private int templateBlockID;

    /**
     * All markers assigned to this block
     */
    @Accessors
    private List<String> markers;

    /**
     *
     * @param block
     */
    protected TemplateBlock(final Block block) {
        this.block = block;
        markers = new LinkedList<String>();
    }

    /**
     *
     * @param marker
     * @return
     */
    protected boolean addMarker(final String marker) {
        return markers.add(marker);
    }
}
