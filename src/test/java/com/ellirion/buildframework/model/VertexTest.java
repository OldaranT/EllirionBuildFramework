package com.ellirion.buildframework.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class VertexTest {

    @Test
    public void findEdgeByVertex_whenNotExists_shouldReturnNull() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertNull(v1.findEdge(v2));
        assertNull(v2.findEdge(v1));
    }

    @Test
    public void findEdgeByVertex_whenExists_shouldReturnEdge() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);
        v1.connect(v2, 3);

        Edge e1 = v1.findEdge(v2);
        Edge e2 = v2.findEdge(v1);

        assertNotNull(e1);
        assertNotNull(e2);
        assertSame(e1, e2);
        assertEquals(3, e1.getWeight(), 0);
    }

    @Test
    public void findEdgeByData_whenNotExists_shouldReturnNull() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertNull(v1.findEdge(2));
        assertNull(v2.findEdge(1));
    }

    @Test
    public void findEdgeByData_whenExists_shouldReturnEdge() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);
        v1.connect(v2, 3);

        Edge e1 = v1.findEdge(2);
        Edge e2 = v2.findEdge(1);

        assertNotNull(e1);
        assertNotNull(e2);
        assertSame(e1, e2);
        assertEquals(3, e1.getWeight(), 0);
    }

    @Test
    public void connect_whenNotConnected_shouldConnect() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertEquals(0, v1.size());
        assertEquals(0, v2.size());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));

        v1.connect(v2, 3);

        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);
    }

    @Test
    public void connect_whenConnected_shouldUpdateWeightToMinimum() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.connect(v2, 3);

        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);

        v1.connect(v2, 2);

        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(2, v1.findEdge(v2).getWeight(), 0);
    }

    @Test
    public void disconnect_whenNotConnected_shouldDoNothing() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.disconnect(v2);

        assertEquals(0, v1.size());
        assertEquals(0, v2.size());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
    }

    @Test
    public void disconnect_whenConnected_shouldDisconnect() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.connect(v2, 3);

        assertEquals(1, v1.size());
        assertEquals(1, v2.size());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);
        assertNotNull(v1.findEdge(v2));
        assertNotNull(v2.findEdge(v1));

        v1.disconnect(v2);

        assertEquals(0, v1.size());
        assertEquals(0, v2.size());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
        assertNull(v1.findEdge(v2));
        assertNull(v2.findEdge(v1));
    }

    @Test
    public void isConnectedTo_whenNotConnected_shouldReturnFalse() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v1.isConnectedTo(2));
        assertFalse(v2.isConnectedTo(v1));
        assertFalse(v2.isConnectedTo(1));
    }

    @Test
    public void isConnectedTo_whenConnected_shouldReturnTrue() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.connect(v2);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v1.isConnectedTo(2));
        assertTrue(v2.isConnectedTo(v1));
        assertTrue(v2.isConnectedTo(1));
    }

    @Test
    public void getWeightTo_whenNotConnected_shouldReturnNegativeOne() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        assertEquals(-1, v1.getWeightTo(v2), 0);
        assertEquals(-1, v1.getWeightTo(2), 0);
        assertEquals(-1, v2.getWeightTo(v1), 0);
        assertEquals(-1, v2.getWeightTo(1), 0);
    }

    @Test
    public void getWeightTo_whenConnected_shouldReturnWeight() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.connect(v2, 5);

        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v1.getWeightTo(2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(5, v2.getWeightTo(1), 0);
    }

    @Test
    public void destroy_whenHasNoEdges_shouldRemoveSelf() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        g.add(2);

        v1.destroy();
        assertEquals(1, g.size());
        assertNull(g.find(1));
        assertNotNull(g.find(2));
    }

    @Test
    public void destroy_whenHasEdges_shouldRemoveEdgesAndSelf() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2 = g.add(2);

        v1.connect(v2, 5);
        v1.destroy();

        assertEquals(1, g.size());
        assertEquals(0, v1.size());
        assertEquals(0, v2.size());
        assertNull(g.find(1));
        assertNotNull(g.find(2));
    }

    @Test
    public void size_whenHasNoEdges_shouldReturnZero() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);

        assertEquals(0, v1.size());
    }

    @Test
    public void size_whenHasEdges_shouldReturnEdgeCount() {
        Graph<Integer> g = new Graph<>();
        Vertex v1 = g.add(1);
        Vertex v2;

        for (int i = 0; i < 10; i++) {
            v2 = g.add(i + 2);

            assertEquals(i, v1.size());
            assertEquals(0, v2.size());

            v1.connect(v2);

            assertEquals(i + 1, v1.size());
            assertEquals(1, v2.size());
        }
    }

}
