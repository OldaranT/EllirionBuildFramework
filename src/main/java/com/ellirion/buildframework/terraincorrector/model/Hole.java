package com.ellirion.buildframework.terraincorrector.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BoundingBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Hole {

    @Getter private List<Block> blockList;

    @Setter private boolean exceedsMaxDepth = false;
    @Setter private boolean exceedsAreaLimit = false;

    @Setter @Getter private int minX;
    @Setter @Getter private int maxX;
    @Setter @Getter private int minZ;
    @Setter @Getter private int maxZ;

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
        minX = b.getX();
        maxX = minX;
        minZ = b.getZ();
        maxZ = minZ;
    }

    /**
     * @param s The set that should be used to fill the list.
     */
    public Hole(final List<Block> s) {
        blockList = s;
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
        if (minX > b.getX()) {
            minX = b.getX();
        }
        if (maxX < b.getX()) {
            maxX = b.getX();
        }
        if (minZ > b.getZ()) {
            minZ = b.getZ();
        }
        if (maxZ < b.getZ()) {
            maxZ = b.getZ();
        }
        return true;
    }

    /**
     * @return whether the hole exceeds the maximum depth
     */
    public boolean exceedsMaxDepth() {
        return exceedsMaxDepth;
    }

    /**
     * @return whether the hole exceeds the maximum depth
     */
    public boolean exceedsAreaLimit() {
        return exceedsAreaLimit;
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
        if (!blockList.isEmpty()) {
            int highestY = blockList.stream().max(Comparator.comparing(Block::getY)).get().getY();
            return blockList.stream().filter(block -> block.getY() == highestY).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * @param boundingBox the BoundingBox that will be used for checking
     * @return Whether the hole is fully below the BoundingBox
     */
    public boolean onlyBelowBoundingBox(BoundingBox boundingBox) {
        return blockList.stream()
                .noneMatch(block -> block.getX() < boundingBox.getX1() ||
                                    block.getX() > boundingBox.getX2() ||
                                    block.getZ() < boundingBox.getZ1() ||
                                    block.getZ() > boundingBox.getZ2());
    }

    /**
     * @return the depth of the hole
     */
    public int getDepth() {
        int highestY = blockList.stream().max(Comparator.comparing(Block::getY)).get().getY();
        int lowestY = blockList.stream().min(Comparator.comparing(Block::getY)).get().getY();
        return highestY - lowestY;
    }
}
