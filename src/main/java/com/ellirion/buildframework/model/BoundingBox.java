package com.ellirion.buildframework.model;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.Position;

public class BoundingBox {

    @Getter private int x1, x2;
    @Getter private int y1, y2;
    @Getter private int z1, z2;

    /**
     * Create a BoundingBox between (inclusive) position (0,0,0) and position (0,0,0).
     */
    public BoundingBox() {
        this(0, 0, 0, 0, 0, 0);
    }

    /**
     * Creates a BoundingBox at exactly the given position {@code p}.
     * @param p The position the BoundingBox should be created at
     */
    public BoundingBox(final Position p) {
        this(p, p);
    }

    /**
     * Creates a BoundingBox at exactly the given position (x1,y1,z1).
     * @param x1 The x-component
     * @param y1 The y-component
     * @param z1 The z-component
     */
    public BoundingBox(final int x1, final int y1, final int z1) {
        this(x1, y1, z1, x1, y1, z1);
    }

    /**
     * Creates a BoundingBox between (inclusive) position {@code p1} and {@code p2}.
     * @param p1 The first position
     * @param p2 The second position
     */
    public BoundingBox(final Position p1, final Position p2) {
        this((int) Math.round(p1.getX()), (int) Math.round(p1.getY()), (int) Math.round(p1.getZ()),
                (int) Math.round(p2.getX()), (int) Math.round(p2.getY()), (int) Math.round(p2.getZ()));
    }

    /**
     * Create a BoundingBox between (inclusive) position (x1,y1,z1) and position (x2,y2,z2).
     * @param x1 The first x-component
     * @param y1 The first y-component
     * @param z1 The first z-component
     * @param x2 The second x-component
     * @param y2 The second y-component
     * @param z2 The second z-component
     */
    public BoundingBox(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);

        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    /**
     * Checks if the position {@code p} lies within the blocks contained in this BoundingBox.
     * @param p The position to check
     * @return Whether the position lies within the bounds of this BoundingBox
     */

    public boolean intersects(final Position p) {
        final int px = (int) p.getX();
        final int py = (int) p.getY();
        final int pz = (int) p.getZ();
        return x1 <= px && px <= x2 && y1 <= py && py <= y2 && z1 <= pz && pz <= z2;
    }

    /**
     * Checks if the BoundingBox {@code bb} intersects with the current BoundingBox.
     * @param bb The BoundingBox to check for intersection with
     * @return Whether the two BoundingBoxes intersect
     */
    public boolean intersects(final BoundingBox bb) {
        return x1 <= bb.x2 && bb.x1 <= x2 && y1 <= bb.y2 && bb.y1 <= y2 && z1 <= bb.z2 && bb.z1 <= z2;
    }

    /**
     * Translates the BoundingBox to local coordinates.
     * @return A new BoundingBox with local coordinates
     */
    public BoundingBox toLocal() {
        return new BoundingBox(0, 0, 0, x2 - x1, y2 - y1, z2 - z1);
    }

    /**
     * Translates the BoundingBox to world coordinates, with {@code pos} as the origin.
     * @param pos The new origin
     * @return The BoundingBox at the world coordinates
     */

    public BoundingBox toWorld(final Position pos) {
        final BoundingBox local = toLocal();
        final int px = (int) Math.round(pos.getX());
        final int py = (int) Math.round(pos.getY());
        final int pz = (int) Math.round(pos.getZ());
        return new BoundingBox(px, py, pz,
                px + (local.x2 - local.x1),
                py + (local.y2 - local.y1),
                pz + (local.z2 - local.z1));
    }

    /**
     * Gets the smallest-component position of this BoundingBox.
     * @return The smallest-component position of this BoundingBox
     */
    public Position getPosition1() {
        return new Position(x1, y1, z1);
    }

    /**
     * Gets the largest-component position of this BoundingBox.
     * @return The largest-component position of this BoundingBox
     */
    public Position getPosition2() {
        return new Position(x2, y2, z2);
    }

    /**
     * Get the width (x-axis) of this BoundingBox.
     * @return the width
     */
    public int getWidth() {
        return x2 - x1 + 1;
    }

    /**
     * Gets the height (y-axis) of this BoundingBox.
     * @return the height
     */
    public int getHeight() {
        return y2 - y1 + 1;
    }

    /**
     * Gets the depth (z-axis) of this BoundingBox.
     * @return the depth
     */
    public int getDepth() {
        return z2 - z1 + 1;
    }

}
