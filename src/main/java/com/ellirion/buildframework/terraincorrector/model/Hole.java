package com.ellirion.buildframework.terraincorrector.model;

import lombok.Getter;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hole {

    @Getter private List<Block> blockList;

    /**
     * Creates an empty hole.
     */
    public Hole() {
        blockList = new ArrayList<>();
    }

    /**
     * Creates an new Hole that contains the Block {@code b}.
     * @param b the block that will be added to the list
     */
    public Hole(final Block b) {
        blockList = new ArrayList<>();
        blockList.add(b);
    }

    /**
     * @param s The set that should be used to fill the list.
     */
    public Hole(final Set<Block> s) {
        blockList = new ArrayList<>();
        blockList.addAll(s);
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
     * @param b the block that should
     * @return the block was already present in the hole
     */
    public boolean add(Block b) {
        if (contains(b)) {
            return false;
        }

        blockList.add(b);
        return true;
    }

    /**
     * @return a whether the hole has liquid
     */
    public boolean containsLiquid() {
        for (Block b : blockList) {
            if (b.isLiquid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a List of the highest blocks in the Hole.
     * @return the highest blocks
     */
    public List<Block> getTopBlocks() {
        int highestY = blockList.stream().max(Comparator.comparing(Block::getY)).get().getY();
        return blockList.stream().filter(block -> block.getY() == highestY).collect(Collectors.toList());
    }
}
