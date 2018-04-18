package com.ellirion.buildframework.model;

import net.minecraft.server.v1_12_R1.Position;
import org.bukkit.Location;
import org.junit.Test;

import static org.junit.Assert.*;

public class PointTest {

    @Test
    public void constructorBlank_whenInvoked_shouldUseZeroes() {
        Point p = new Point();
        assertEquals(0, p.getX(), 0);
        assertEquals(0, p.getY(), 0);
        assertEquals(0, p.getZ(), 0);
    }

    @Test
    public void constructorPosition_whenInvoked_shouldUsePositionComponents() {
        Point p = new Point(new Position(1, 2, 3));
        assertEquals(1, p.getX(), 0);
        assertEquals(2, p.getY(), 0);
        assertEquals(3, p.getZ(), 0);
    }

    @Test
    public void constructorLocation_whenInvoked_shouldUseLocationComponents() {
        Point p = new Point(new Location(null, 1, 2, 3));
        assertEquals(1, p.getX(), 0);
        assertEquals(2, p.getY(), 0);
        assertEquals(3, p.getZ(), 0);
    }

    @Test
    public void constructorInts_whenInvoked_shouldUseIntsAsComponents() {
        Point p = new Point(1, 2, 3);
        assertEquals(1, p.getX(), 0);
        assertEquals(2, p.getY(), 0);
        assertEquals(3, p.getZ(), 0);
    }

    @Test
    public void min_whenInvoked_shouldReturnNewPointWithMinimumComponents() {
        Point p1 = new Point(1,5,3);
        Point p2 = new Point(2, 0, 4);
        Point p3 = p1.min(p2);
        Point p4 = p2.min(p1);

        assertEquals(1, p3.getX(), 0);
        assertEquals(0, p3.getY(), 0);
        assertEquals(3, p3.getZ(), 0);

        assertEquals(1, p4.getX(), 0);
        assertEquals(0, p4.getY(), 0);
        assertEquals(3, p4.getZ(), 0);

        assertEquals(p3, p4);
    }

    @Test
    public void max_whenInvoked_shouldReturnNewPointWithMaximumComponents() {
        Point p1 = new Point(1,5,3);
        Point p2 = new Point(2, 0, 4);
        Point p3 = p1.max(p2);
        Point p4 = p2.max(p1);

        assertEquals(2, p3.getX(), 0);
        assertEquals(5, p3.getY(), 0);
        assertEquals(4, p3.getZ(), 0);

        assertEquals(2, p4.getX(), 0);
        assertEquals(5, p4.getY(), 0);
        assertEquals(4, p4.getZ(), 0);

        assertEquals(p3, p4);
    }

    @Test
    public void floor_whenHasDecimals_shouldReturnNewPointWithRoundedDownComponents() {
        Point p1 = new Point(1.2, -3.4, 6.7);

        Point p2 = p1.floor();

        assertEquals(1, p2.getX(), 0);
        assertEquals(-4, p2.getY(), 0);
        assertEquals(6, p2.getZ(), 0);
    }

    @Test
    public void ceil_whenHasDecimals_shouldReturnNewPointWithRoundedUpComponents() {
        Point p1 = new Point(1.2, -3.4, 6.7);

        Point p2 = p1.ceil();

        assertEquals(2, p2.getX(), 0);
        assertEquals(-3, p2.getY(), 0);
        assertEquals(7, p2.getZ(), 0);
    }

    @Test
    public void distanceEuclidian_whenInvoked_shouldReturnEuclidianDistance() {
        Point p1, p2;

        p1 = new Point(0, 0, 0);
        p2 = new Point(0, 0, 0);
        assertEquals(0, p1.distanceEuclidian(p2), 0);

        p1 = new Point(0, 0, 0);
        p2 = new Point(1, 1, 1);
        assertEquals(1.732051, p1.distanceEuclidian(p2), 0.000001);
    }

    @Test
    public void distanceManhattan_whenInvoked_shouldReturnSumOfComponentDeltas() {
        Point p1, p2;

        p1 = new Point(0, 0, 0);
        p2 = new Point(0, 0, 0);
        assertEquals(0, p1.distanceManhattan(p2), 0);

        p1 = new Point(0, 0, 0);
        p2 = new Point(1, 1, 1);
        assertEquals(3, p1.distanceManhattan(p2), 0);
    }

    @Test
    public void toPosition_whenInvoked_shouldReturnPositionWithIdenticalComponents() {
        Point point = new Point(1.2, -3.4, 6.7);

        Position pos = point.toPosition();

        assertEquals(1.2, pos.getX(), 0);
        assertEquals(-3.4, pos.getY(), 0);
        assertEquals(6.7, pos.getZ(), 0);
    }

    @Test
    public void toLocation_whenInvoked_shouldReturnPositionWithIdenticalComponents() {
        Point p = new Point(1.2, -3.4, 6.7);

        Location l = p.toLocation(null);

        assertEquals(1.2, l.getX(), 0);
        assertEquals(-3.4, l.getY(), 0);
        assertEquals(6.7, l.getZ(), 0);
    }

    @Test
    public void getBlockXYZ_whenHasDecimals_shouldRoundDown() {
        Point p = new Point(1.2, -3.4, 6.7);
        assertEquals(1, p.getBlockX());
        assertEquals(-4, p.getBlockY());
        assertEquals(6, p.getBlockZ());
    }

    @Test
    public void equals_whenNotEqual_shouldReturnFalse() {
        Point p1 = new Point(1.2, -3.4, 6.7);
        Point p2 = new Point(1.3, -3.4, 6.7);

        assertNotEquals(p1, p2);
        assertNotEquals(p2, p1);
    }

    @Test
    public void equals_whenEqual_shouldReturnTrue() {
        Point p1 = new Point(1.2, -3.4, 6.7);
        Point p2 = new Point(1.2, -3.4, 6.7);

        assertEquals(p1, p2);
        assertEquals(p2, p1);
    }

}
