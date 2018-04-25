package com.ellirion.buildframework.terraincorrector.model;

import lombok.Getter;
import org.bukkit.block.Block;

import java.util.HashSet;

public class Hole {

    @Getter private HashSet<Block> blockList;
    @Getter private boolean isLiquid;

    /**
     * Creates a new Hole that contains the Block {@code b}.
     * @param b the block that will be added to the list
     */
    public Hole(final Block b) {
        blockList = new HashSet<>();
        blockList.add(b);
    }

    /**
     * Returns whether the Block {@code b} is present in this hole.
     * @param b the block that will be checked
     * @return whether the block is in the list
     */
    public boolean contains(Block b) {
        return blockList.contains(b);
    }

    /**
     * @return a whether the hole has liquid
     */
    public boolean containsLiquid() {
        if (isLiquid) {
            return true;
        }
        for (Block b : blockList) {
            if (b.isLiquid()) {
                isLiquid = true;
                return true;
            }
        }
        return false;
    }
}
