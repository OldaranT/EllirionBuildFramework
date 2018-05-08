package com.ellirion.buildframework.model;

public enum Direction {
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(1, 0),
    WEST(-1, 0),
    NONE(0, 0);

    private int dx, dz;

    /**
     * Constructs a Direction with the given deltas.
     * @param dx The delta X
     * @param dz The delta Z
     */
    Direction(final int dx, final int dz) {
        this.dx = dx;
        this.dz = dz;
    }


    /**
     * Applies the Direction to the given point.
     * @param p The point to apply this Direction to
     * @return The Point in this Direction relative to point {@code p}
     */
    public Point apply(final Point p) {
        return new Point(p.getBlockX() + dx, p.getBlockY(), p.getBlockZ() + dz);
    }

    /**
     * Checks if this Direction is perpendicular to Direction {@code d}.
     * @param d The other Direction that this Direction may be perpendicular to
     * @return Whether this Direction is perpendicular to Direction {@code d}
     */
    public boolean isPerpendicularTo(final Direction d) {
        int x = dx + d.dx;
        int z = dz + d.dz;
        // If they are the same direction, X or Y is 2.
        // If they are opposite directions, X and Y are 0.
        // If they are perpendicular, they are both 1 or -1.
        return !(x == 2 || z == 2 || (x == 0 && z == 0));
    }

    /**
     * Gets the Direction that, when applied to Point {@code a}, will yield Point {@code b}.
     * @param a The first Point
     * @param b The second Point
     * @return The determined Direction
     */
    public static Direction getDirectionTo(final Point a, final Point b) {
        for (Direction d : Direction.values()) {
            Point cur = d.apply(a);
            if (cur.getBlockX() == b.getBlockX() && cur.getBlockZ() == b.getBlockZ()) {
                return d;
            }
        }
        throw new IllegalArgumentException("Point B is not adjacent to Point A");
    }

}
