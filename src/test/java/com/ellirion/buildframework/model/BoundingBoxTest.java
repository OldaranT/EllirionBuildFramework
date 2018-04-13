package com.ellirion.buildframework.model;


import net.minecraft.server.v1_12_R1.Position;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoundingBoxTest {

    @Test
    public void intersectsBoundingBox_whenAdjacent_shouldReturnFalse() {
        BoundingBox bba = new BoundingBox(0, 0, 0);
        BoundingBox bbb;

        for (int i = 0; i < 6; i++) {
            int j = i / 2; // index in component array (what axis?)
            int k = (i % 2) * 2 - 1; // direction of the axis (-1 or 1)

            int[] components = new int[] { 0, 0, 0 };
            components[j] = k * 2 - 1; // (0, 1) to (-1, 1)

            bbb = new BoundingBox(components[0], components[1], components[2]);

            assertFalse(bba.intersects(bbb));
            assertFalse(bbb.intersects(bba));
        }
    }

    @Test
    public void intersectsBoundingBox_whenCornerIntersects_shouldReturnTrue() {
        BoundingBox bba = new BoundingBox(-1, -1, -1, 1, 1, 1);
        BoundingBox bbb;

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    // range(0, 1) * 2 - 1 = range(-1, 1)
                    bbb = new BoundingBox(x * 2 - 1, y * 2 - 1, z * 2 - 1);
                    assertTrue(bbb.intersects(bba));
                    assertTrue(bba.intersects(bbb));
                }
            }
        }
    }

    @Test
    public void intersectsBoundingBox_whenIntersectsFace_shouldReturnTrue() {
        BoundingBox bba = new BoundingBox(-1, -1, -1, 1, 1, 1);
        BoundingBox bbb;

        for (int i = 0; i < 6; i++) {
            int j = i / 2; // index of the axis are we going to intersect with
            int k = (i % 2) * 2 - 1; // direction of the axis (-1 or 1)

            int[] pos1 = new int[] { 0, 0, 0 }; // just outside bba
            int[] pos2 = new int[] { 0, 0, 0 }; // same

            pos2[j] = k; // actually intersect on the face

            bbb = new BoundingBox(pos1[0], pos1[1], pos1[2], pos2[0], pos2[1], pos2[2]);

            assertTrue(bba.intersects(bbb));
            assertTrue(bbb.intersects(bba));
        }
    }

    @Test
    public void intersectsBoundingBox_whenContained_shouldReturnTrue() {
        BoundingBox bba = new BoundingBox(-1, -1, -1, 1, 1, 1);
        BoundingBox bbb = new BoundingBox(0,0,0);
        assertTrue(bba.intersects(bbb));
        assertTrue(bbb.intersects(bba));
    }

    @Test
    public void intersectsPosition_whenIntersecting_shouldReturnTrue() {
        BoundingBox bb = new BoundingBox(-1, -1, -1, 1, 1, 1);
        Position p;

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    p = new Position(x, y, z);
                    assertTrue(bb.intersects(p));
                }
            }
        }
    }

    @Test
    public void intersectsPosition_whenNotIntersecting_shouldReturnFalse() {
        BoundingBox bb = new BoundingBox(-1, -1, -1, 1, 1, 1);
        Position p;

        for (int x = -2; x <= 2; x++) {
            if (x >= -1 || x <= 1) continue;
            for (int y = -2; y <= 2; y++) {
                if (y >= -1 || y <= 1) continue;
                for (int z = -2; z <= 2; z++) {
                    if (z >= -1 || z <= 1) continue;
                    p = new Position(x, y, z);
                    assertFalse(bb.intersects(p));
                }
            }
        }
    }

    @Test
    public void toLocal_whenNotLocal_shouldBecomeLocal() {
        BoundingBox bba = new BoundingBox(2,2,2,3,3,3);
        BoundingBox bbb = bba.toLocal();

        assertEquals(0, bbb.getX1());
        assertEquals(0, bbb.getY1());
        assertEquals(0, bbb.getZ1());

        assertEquals(1, bbb.getX2());
        assertEquals(1, bbb.getY2());
        assertEquals(1, bbb.getZ2());

        bba = new BoundingBox(-2,-2,-2,-1,-1,-1);
        bbb = bba.toLocal();

        assertEquals(0, bbb.getX1());
        assertEquals(0, bbb.getY1());
        assertEquals(0, bbb.getZ1());

        assertEquals(1, bbb.getX2());
        assertEquals(1, bbb.getY2());
        assertEquals(1, bbb.getZ2());
    }

    @Test
    public void toLocal_whenLocal_shouldStayLocal() {
        BoundingBox bba = new BoundingBox(0,0,0,1,1,1);
        BoundingBox bbb = bba.toLocal();

        assertEquals(0, bbb.getX1());
        assertEquals(0, bbb.getY1());
        assertEquals(0, bbb.getZ1());

        assertEquals(1, bbb.getX2());
        assertEquals(1, bbb.getY2());
        assertEquals(1, bbb.getZ2());
    }

    @Test
    public void toWorld_whenLocal_shouldBecomeWorld() {
        BoundingBox bba = new BoundingBox(0, 0, 0, 1, 1, 1);
        BoundingBox bbb = bba.toWorld(new Position(5, 5, 5));

        assertEquals(5, bbb.getX1());
        assertEquals(5, bbb.getY1());
        assertEquals(5, bbb.getZ1());

        assertEquals(6, bbb.getX2());
        assertEquals(6, bbb.getY2());
        assertEquals(6, bbb.getZ2());
    }

}
