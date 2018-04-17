package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class TemplateBlock {
    /**
     * The actual block.
     */
    @Getter
    @Setter
    private Block block;

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
    private BlockState metadata;

    /**
     *
     * @param block create a templateblock with the given block
     */
    protected TemplateBlock(final Block block) {
        this.block = block;
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
