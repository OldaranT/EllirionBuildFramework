package com.ellirion.buildframework.model;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.Position;
import org.bukkit.Location;
import org.bukkit.World;

public class Point {

    @Getter private double x;
    @Getter private double y;
    @Getter private double z;

    /**
     * Constructs a Point at (0,0,0).
     */
    public Point() {
        this(0d, 0d, 0d);
    }

    /**
     * Constructs a Point form Position {@code p}.
     * @param p The Position to convert
     */
    public Point(final Position p) {
        this(p.getX(), p.getY(), p.getZ());
    }

    /**
     * Constructs a Point from Location {@code l}.
     * @param l The Location to convert
     */
    public Point(final Location l) {
        this(l.getX(), l.getY(), l.getZ());
    }

    /**
     * Constructs a Point at (x,y,z).
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public Point(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a Point at (x,y,z).
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public Point(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns the minimum of the two Points as a new Point.
     * @param p The other Point
     * @return The minimum Point
     */
    public Point min(Point p) {
        return new Point(Math.min(x, p.x), Math.min(y, p.y), Math.min(z, p.z));
    }

    /**
     * Returns the maximum o the two Points as a new Point.
     * @param p The other Point
     * @return The maximum Point
     */
    public Point max(Point p) {
        return new Point(Math.max(x, p.x), Math.max(y, p.y), Math.max(z, p.z));
    }

    /**
     * Rounds the components of this Point down and returns the new Point.
     * @return The rounded-down Point
     */
    public Point floor() {
        return new Point(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    /**
     * Rounds the components of this Point up and returns the new Point.
     * @return The rounded-up Point
     */
    public Point ceil() {
        return new Point(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    /**
     * Calculates the Euclidian distance between this Point and Point {@code p}.
     * @param p The other Point
     * @return The Euclidian distance
     */
    public double distanceEuclidian(Point p) {
        return Math.sqrt(Math.pow(p.x - x, 2)
                         + Math.pow(p.y - y, 2)
                         + Math.pow(p.z - z, 2));
    }

    /**
     * Calculates the Manhattan distance between this Point and Point {@code p}.
     * @param p The other Point
     * @return The Manhattan distance
     */
    public double distanceManhattan(Point p) {
        return Math.abs(p.x - x)
               + Math.abs(p.y - y)
               + Math.abs(p.z - z);
    }

    /**
     * Converts this Point to a Position.
     * @return The resulting position
     */
    public Position toPosition() {
        return new Position(x, y, z);
    }

    /**
     * Converts this Point to a Location using World {@code w}.
     * @param w The world this Location belongs to
     * @return The resulting Location
     */
    public Location toLocation(World w) {
        return new Location(w, x, y, z);
    }

    public int getBlockX() {
        return (int) Math.floor(x);
    }

    public int getBlockY() {
        return (int) Math.floor(y);
    }

    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point p = (Point) o;
            return x == p.x && y == p.y && z == p.z;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return hash(Double.hashCode(x))
               ^ hash(Double.hashCode(y))
               ^ hash(Double.hashCode(z));
    }

    // Simpele integer hash functie: https://stackoverflow.com/a/12996028
    private int hash(int x) {
        final int half = 32 / 2;
        final int mult = 0x45d9f3b;
        x = ((x >>> half) ^ x) * mult;
        x = ((x >>> half) ^ x) * mult;
        x = (x >>> half) ^ x;
        return x;
    }

    /**
     * Set a point to a local template point.
     * @param worldPoint point in the world.
     * @return A point localized to a template.
     */
    public Point toLocalTemplate(Point worldPoint) {
        double newX = 0;
        double newY = 0;
        double newZ = 0;

        newX = x - worldPoint.getX();
        newY = y - worldPoint.getY();
        newZ = z - worldPoint.getZ();

        return new Point(newX, newY, newZ);
    }
}
