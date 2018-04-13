package com.ellirion.buildframework.util;

import lombok.Data;
import net.minecraft.server.v1_12_R1.Position;

@Data public class BoundingBox {

    private int x;
    private int y;
    private int z;

    private int width;
    private int height;
    private int depth;

    /**
     * Create a BoundingBox at position (0,0,0) with dimensions (0,0,0).
     */
    public BoundingBox() {
        this(0, 0, 0, 0, 0, 0);
    }

    /**
     * Create a BoundingBox at position (0,0,0) with dimensions (width,height,depth).
     *
     * @param width The size along the X-axis
     * @param height The size along the Y-axis
     * @param depth The size along the Z-axis
     */
    public BoundingBox(final int width, final int height, final int depth) {
        this(0, 0, 0, width, height, depth);
    }

    /**
     * Create a BoundingBox at position (x,y,z) with dimensions (width,height,depth).
     *
     * @param x The x component of the position
     * @param y The y component of the position
     * @param z The z component of the position
     * @param width The size along the X-axis
     * @param height The size along the Y-axis
     * @param depth The size along the Z-axis
     */
    public BoundingBox(final int x, final int y, final int z, final int width, final int height, final int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    /**
     * Checks if the position {@code p} lies within the blocks contained in this BoundingBox.
     *
     * @param p The position to check
     * @return Whether the position lies within the bounds of this BoundingBox
     */
    public boolean intersects(Position p) {
        int px = (int) p.getX();
        int py = (int) p.getY();
        int pz = (int) p.getZ();
        return px >= x && px <= this.getX2()
                && py >= y && py <= this.getY2()
                && pz >= z && pz <= this.getZ2();
    }

    /**
     * Checks if the BoundingBox {@code bb} intersects with the current BoundingBox.
     *
     * @param bb The BoundingBox to check for intersection with
     * @return Whether the two BoundingBoxes intersect
     */
    public boolean intersects(final BoundingBox bb) {
        return this.getX() < bb.getX2()
                && this.getX2() > bb.getX()
                && this.getY() > bb.getY2()
                && this.getY2() < bb.getY()
                && this.getZ() > bb.getZ()
                && this.getZ2() < bb.getZ();
    }

    public Position getPosition() {
        return new Position(x, y, z);
    }

    /**
     * Sets the (x,y,z) components of this BoundingBox to those of the Position {@code pos}.
     * The doubles are rounded and cast to integers before assignment.
     *
     * @param pos The position to change to.
     */
    public void setPosition(Position pos) {
        x = (int) Math.round(pos.getX());
        y = (int) Math.round(pos.getY());
        z = (int) Math.round(pos.getY());
    }

    public Position getOtherPosition() {
        return new Position(x + width, y + height, z + depth);
    }

    public int getX2() {
        return x + width - 1;
    }

    public int getY2() {
        return y + height - 1;
    }

    public int getZ2() {
        return z + depth - 1;
    }

}
