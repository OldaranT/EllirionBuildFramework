package com.ellirion.buildframework.model.graph;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class BasicGraphTest {

    @Test
    public void find_whenExists_shouldReturnVertex() {
        IGraph<String> g = new BasicGraph<>();
        g.add("test");

        IVertex<String> v = g.find("test");

        assertNotNull(v);
        assertEquals("test", v.getData());
    }

    @Test
    public void find_whenDataNotSameButEquals_shouldReturnVertex() {
        IGraph<Integer> g = new BasicGraph<>();
        g.add(2);

        IVertex<Integer> v = g.find(2);

        assertNotNull(v);
        assertEquals(2, v.getData().intValue());
    }

    @Test
    public void find_whenNotExists_shouldReturnNull() {
        IGraph<String> g = new BasicGraph<>();
        g.add("test");

        assertNull(g.find("not present"));
    }

    @Test
    public void findOrCreate_whenExists_shouldReturnOriginal() {
        IGraph<Integer> g = new BasicGraph<>();

        IVertex<Integer> v1 = g.add(2);
        IVertex<Integer> v2 = g.findOrCreate(2);

        assertNotNull(v1);
        assertNotNull(v2);
        assertSame(v1, v2);
    }

    @Test
    public void findOrCreate_whenNotExists_shouldReturnNew() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v = g.findOrCreate(2);

        assertNotNull(v);
        assertEquals(new Integer(2), v.getData());
    }

    @Test
    public void containsVertex_whenNotContained_shouldReturnFalse() {
        IGraph<Integer> g1 = new BasicGraph<>();
        IGraph<Integer> g2 = new BasicGraph<>();

        IVertex<Integer> v2 = g2.add(2);

        assertFalse(g1.contains(v2));
    }

    @Test
    public void containsVertex_whenContained_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v = g.add(2);

        assertTrue(g.contains(v));
    }

    @Test
    public void containsVertex_whenNotContainedButEqual_shouldReturnFalse() {
        IGraph<Integer> g1 = new BasicGraph<>();
        IGraph<Integer> g2 = new BasicGraph<>();

        g1.add(2);
        IVertex<Integer> v2 = g2.add(2);

        assertFalse(g1.contains(v2));
    }

    @Test
    public void containsData_whenNotContained_shouldReturnFalse() {
        IGraph<Integer> g = new BasicGraph<>();
        assertFalse(g.contains(1));
    }

    @Test
    public void containsData_whenContained_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        g.add(1);

        assertTrue(g.contains(1));
    }

    @Test
    public void remove_whenExists_shouldRemove() {
        IGraph<Integer> g = new BasicGraph<>();
        g.add(2);

        g.remove(2);
        assertNull(g.find(2));
        assertEquals(0, g.getVertexCount());
    }

    @Test
    public void remove_whenNotExists_shouldNotRemove() {
        IGraph<Integer> g = new BasicGraph<>();
        g.add(1);
        g.add(3);

        assertEquals(2, g.getVertexCount());
        g.remove(2);
        assertEquals(2, g.getVertexCount());
    }

    @Test
    public void connect_whenNotConnected_shouldConnect() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);

        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
        assertEquals(-1, v1.getWeightTo(v2), 0);
        assertEquals(-1, v2.getWeightTo(v1), 0);
        assertEquals(0, v1.getEdgeCount());
        assertEquals(0, v2.getEdgeCount());

        g.connect(1, 2, 1);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(1, v1.getWeightTo(v2), 0);
        assertEquals(1, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
    }

    @Test
    public void connect_whenConnected_shouldUpdateWeightToMinimum() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);

        g.connect(1, 2, 5);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());

        g.connect(1, 2, 7);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());

        g.connect(1, 2, 3);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.getWeightTo(v2), 0);
        assertEquals(3, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
    }

    @Test
    public void connect_whenNoVerticesExist_shouldCreateVertices() {
        IGraph<Integer> g = new BasicGraph<>();

        assertNull(g.find(1));
        assertNull(g.find(2));
        assertEquals(0, g.getVertexCount());
        assertFalse(g.areConnected(1, 2));

        g.connect(1, 2, 5);

        IVertex<Integer> v1 = g.find(1);
        IVertex<Integer> v2 = g.find(2);

        assertNotNull(v1);
        assertNotNull(v2);
        assertEquals(2, g.getVertexCount());
        assertTrue(g.areConnected(1, 2));
        assertEquals(v1, g.find(1));
        assertEquals(v2, g.find(2));
    }

    @Test
    public void disconnect_whenNotConnected_shouldDoNothing() {
        IGraph<Integer> g = new BasicGraph<>();

        assertEquals(0, g.getVertexCount());

        g.disconnect(1, 2);

        assertEquals(0, g.getVertexCount());
    }

    @Test
    public void disconnect_whenConnected_shouldDisconnect() {
        IGraph<Integer> g = new BasicGraph<>();
        g.add(1);
        g.add(2);
        g.connect(1, 2, 1);

        assertTrue(g.areConnected(1, 2));

        g.disconnect(1, 2);

        assertFalse(g.areConnected(1, 2));
    }

    @Test
    public void areConnected_whenNotConnected_shouldReturnFalse() {
        IGraph<Integer> g = new BasicGraph<>();

        assertFalse(g.areConnected(1, 2));
    }

    @Test
    public void areConnected_whenConnected_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        g.connect(1, 2, 1);

        assertTrue(g.areConnected(1, 2));
    }

    @Test
    public void getVertices_whenEmpty_shouldReturnEmptyIterator() {
        IGraph<Integer> g = new BasicGraph<>();
        Iterator<? extends IVertex<Integer>> iter = g.getVertices().iterator();

        assertFalse(iter.hasNext());
    }

    @Test
    public void getVertices_whenNotEmpty_shouldReturnFilledIterator() {
        IGraph<Integer> g = new BasicGraph<>();
        for (int i = 0; i < 10; i++) {
            g.add(i);
        }

        Iterator<? extends IVertex<Integer>> iter = g.getVertices().iterator();

        for (int i = 0; i < 10; i++) {
            assertTrue(iter.hasNext());

            IVertex<Integer> vert = iter.next();

            assertNotNull(vert);
            assertEquals(i, vert.getData().intValue());
        }

    }

    @Test
    public void getVertexCount_whenEmpty_shouldReturnZero() {
        IGraph<Integer> g = new BasicGraph<>();

        assertEquals(0, g.getVertexCount());
    }

    @Test
    public void getVertexCount_whenNotEmpty_shouldReturnVertexCount() {
        IGraph<Integer> g = new BasicGraph<>();

        for (int i = 0; i < 10; i++) {
            assertEquals(i, g.getVertexCount());

            g.add(i);

            assertEquals(i + 1, g.getVertexCount());
        }
    }

}
