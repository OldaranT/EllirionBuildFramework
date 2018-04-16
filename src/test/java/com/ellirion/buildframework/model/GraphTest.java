package com.ellirion.buildframework.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class GraphTest {

    @Test
    public void find_whenExists_shouldReturnVertex() {
        Graph<String> g = new Graph<>();
        g.add("test");

        Vertex<String> v = g.find("test");
        assertNotNull(v);
        assertEquals("test", v.getData());
    }

    @Test
    public void find_whenDataNotSameButEquals_shouldReturnVertex() {
        Graph<Integer> g = new Graph<>();
        g.add(new Integer(2));

        Vertex<Integer> v = g.find(new Integer(2));
        assertNotNull(v);
        assertEquals(new Integer(2), v.getData());
    }

    @Test
    public void find_whenNotExists_shouldReturnNull() {
        Graph<String> g = new Graph<>();
        g.add("test");

        assertNull(g.find("not present"));
    }

    @Test
    public void findOrCreate_whenExists_shouldReturnOriginal() {
        Graph<Integer> g = new Graph<>();

        Vertex<Integer> v1 = g.add(new Integer(2));
        Vertex<Integer> v2 = g.findOrCreate(new Integer(2));

        assertNotNull(v1);
        assertNotNull(v2);
        assertSame(v1, v2);
    }

    @Test
    public void findOrCreate_whenNotExists_shouldReturnNew() {
        Graph<Integer> g = new Graph<>();

        Vertex<Integer> v = g.findOrCreate(2);

        assertNotNull(v);
        assertEquals(new Integer(2), v.getData());
    }

    @Test
    public void remove_whenExists_shouldRemove() {
        Graph<Integer> g = new Graph<>();
        g.add(2);

        g.remove(2);
        assertNull(g.find(2));
        assertEquals(0, g.size());
    }

    @Test
    public void remove_whenNotExists_shouldNotRemove() {
        Graph<Integer> g = new Graph<>();
        g.add(1);
        g.add(3);

        assertEquals(2, g.size());
        g.remove(2);
        assertEquals(2, g.size());
    }

    @Test
    public void connect_whenNotConnected_shouldConnect() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
        assertEquals(-1, v1.getWeightTo(v2), 0);
        assertEquals(-1, v2.getWeightTo(v1), 0);
        assertEquals(0, v1.size());
        assertEquals(0, v2.size());

        g.connect(1, 2, 1);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(1, v1.getWeightTo(v2), 0);
        assertEquals(1, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
    }

    @Test
    public void connect_whenConnected_shouldUpdateWeightToMinimum() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        g.connect(1, 2, 5);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.size());
        assertEquals(1, v2.size());

        g.connect(1, 2, 7);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.size());
        assertEquals(1, v2.size());

        g.connect(1,2, 3);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.getWeightTo(v2), 0);
        assertEquals(3, v2.getWeightTo(v1), 0);
        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
    }

    @Test
    public void connect_whenNoVerticesExist_shouldCreateVertices() {
        Graph<Integer> g = new Graph<>();

        assertNull(g.find(1));
        assertNull(g.find(2));
        assertEquals(0, g.size());
        assertFalse(g.areConnected(1, 2));

        g.connect(1, 2, 5);

        Vertex v1 = g.find(1);
        Vertex v2 = g.find(2);

        assertNotNull(v1);
        assertNotNull(v2);
        assertEquals(2, g.size());
        assertTrue(g.areConnected(1, 2));
        assertEquals(v1, g.find(1));
        assertEquals(v2, g.find(2));
    }

    @Test
    public void disconnect_whenNotConnected_shouldDoNothing() {
        Graph<Integer> g = new Graph<>();
        assertEquals(0, g.size());
        g.disconnect(1, 2);
        assertEquals(0, g.size());
    }

    @Test
    public void disconnect_whenConnected_shouldDisconnect() {
        Graph<Integer> g = new Graph<>();
        g.add(1);
        g.add(2);
        g.connect(1,2, 1);

        assertTrue(g.areConnected(1, 2));

        g.disconnect(1,2);

        assertFalse(g.areConnected(1,2));
    }

    @Test
    public void areConnected_whenNotConnected_shouldReturnFalse() {
        Graph<Integer> g = new Graph<>();
        assertFalse(g.areConnected(1,2));
    }

    @Test
    public void areConnected_whenConnected_shouldReturnTrue() {
        Graph<Integer> g = new Graph<>();
        g.connect(1, 2, 1);
        assertTrue(g.areConnected(1,2));
    }

    @Test
    public void size_whenEmpty_shouldReturnZero() {
        Graph<Integer> g = new Graph<>();
        assertEquals(0, g.size());
    }

    @Test
    public void size_whenNotEmpty_shouldReturnVertexCount() {
        Graph<Integer> g = new Graph<>();

        for (int i = 0; i < 10; i++) {
            assertEquals(i, g.size());
            g.add(i);
            assertEquals(i + 1, g.size());
        }
    }

}
