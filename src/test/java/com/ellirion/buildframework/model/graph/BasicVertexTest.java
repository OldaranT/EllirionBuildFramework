package com.ellirion.buildframework.model.graph;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class BasicVertexTest {

    @Test
    public void findEdgeByVertex_whenNotExists_shouldReturnNull() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        assertNull(v1.findEdge(v2));
        assertNull(v2.findEdge(v1));
    }

    @Test
    public void findEdgeByVertex_whenExists_shouldReturnEdge() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);
        v1.connect(v2, 3);

        IEdge e1 = v1.findEdge(v2);
        IEdge e2 = v2.findEdge(v1);

        assertNotNull(e1);
        assertNotNull(e2);
        assertSame(e1, e2);
        assertEquals(3, e1.getWeight(), 0);
    }

    @Test
    public void findEdgeByData_whenNotExists_shouldReturnNull() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        assertNull(v1.findEdge(2));
        assertNull(v2.findEdge(1));
    }

    @Test
    public void findEdgeByData_whenExists_shouldReturnEdge() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);
        v1.connect(v2, 3);

        IEdge e1 = v1.findEdge(2);
        IEdge e2 = v2.findEdge(1);

        assertNotNull(e1);
        assertNotNull(e2);
        assertSame(e1, e2);
        assertEquals(3, e1.getWeight(), 0);
    }

    @Test
    public void connectByVertex_whenNotConnected_shouldConnect() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        assertEquals(0, v1.getEdgeCount());
        assertEquals(0, v2.getEdgeCount());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));

        v1.connect(v2, 3);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);
    }

    @Test
    public void connectByVertex_whenConnected_shouldUpdateWeightToMinimum() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        v1.connect(v2, 3);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);

        v1.connect(v2, 2);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(2, v1.findEdge(v2).getWeight(), 0);
    }

    @Test
    public void connectByEdge_whenNotInvolved_shouldDoNothing() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IVertex<Integer> v3 = g.add(3);

        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);
        v3.connect(e);

        assertEquals(0, v2.getEdgeCount());
    }

    @Test
    public void connectByEdge_whenInvolved_shouldAddEdge() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);

        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertEquals(0, v1.getEdgeCount());
        assertEquals(0, v2.getEdgeCount());

        v2.connect(e);

        assertEquals(0, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
    }

    @Test
    public void connectByEdge_whenInvolvedAndAdded_shouldDoNothing() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);

        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);
        v1.connect(e);
        v2.connect(e);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());

        v2.connect(e);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
    }

    @Test
    public void disconnectByVertex_whenNotConnected_shouldDoNothing() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        v1.disconnect(v2);

        assertEquals(0, v1.getEdgeCount());
        assertEquals(0, v2.getEdgeCount());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
    }

    @Test
    public void disconnectByVertex_whenConnected_shouldDisconnect() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        v1.connect(v2, 3);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v2.isConnectedTo(v1));
        assertEquals(3, v1.findEdge(v2).getWeight(), 0);
        assertNotNull(v1.findEdge(v2));
        assertNotNull(v2.findEdge(v1));

        v1.disconnect(v2);

        assertEquals(0, v1.getEdgeCount());
        assertEquals(0, v2.getEdgeCount());
        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v2.isConnectedTo(v1));
        assertNull(v1.findEdge(v2));
        assertNull(v2.findEdge(v1));
    }

    @Test
    public void disconnectByEdge_whenNotConnected_shouldDoNothing() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IVertex<Integer> v3 = g.add(3);
        IEdge<Integer> e = new BasicEdge<>(v2, v3, 1);
        v1.connect(v2, 1);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertEquals(0, v3.getEdgeCount());

        v2.disconnect(e);

        assertEquals(1, v1.getEdgeCount());
        assertEquals(1, v2.getEdgeCount());
        assertEquals(0, v3.getEdgeCount());
    }

    @Test
    public void isConnectedTo_whenNotConnected_shouldReturnFalse() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        assertFalse(v1.isConnectedTo(v2));
        assertFalse(v1.isConnectedTo(2));
        assertFalse(v2.isConnectedTo(v1));
        assertFalse(v2.isConnectedTo(1));
    }

    @Test
    public void isConnectedTo_whenConnected_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        v1.connect(v2, 1);

        assertTrue(v1.isConnectedTo(v2));
        assertTrue(v1.isConnectedTo(2));
        assertTrue(v2.isConnectedTo(v1));
        assertTrue(v2.isConnectedTo(1));
    }

    @Test
    public void getWeightTo_whenNotConnected_shouldReturnNegativeOne() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        assertEquals(-1, v1.getWeightTo(v2), 0);
        assertEquals(-1, v1.getWeightTo(2), 0);
        assertEquals(-1, v2.getWeightTo(v1), 0);
        assertEquals(-1, v2.getWeightTo(1), 0);
    }

    @Test
    public void getWeightTo_whenConnected_shouldReturnWeight() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);
        IVertex v2 = g.add(2);

        v1.connect(v2, 5);

        assertEquals(5, v1.getWeightTo(v2), 0);
        assertEquals(5, v1.getWeightTo(2), 0);
        assertEquals(5, v2.getWeightTo(v1), 0);
        assertEquals(5, v2.getWeightTo(1), 0);
    }

    @Test
    public void getEdges_whenHasNoEdges_shouldReturnEmptyIterable() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v = g.add(1);

        Iterator<IEdge<Integer>> iter = v.getEdges().iterator();

        assertNotNull(iter);
        assertFalse(v.getEdges().iterator().hasNext());
    }

    @Test
    public void getEdges_whenHasEdges_shouldReturnIterable() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(-1);
        IVertex<Integer> v2;
        for (int i = 0; i < 10; i++) {
            v2 = g.add(i);
            v1.connect(v2, 1);
        }
        Iterator<IEdge<Integer>> iter = v1.getEdges().iterator();

        assertNotNull(iter);
        for (int i = 0; i < 10; i++) {
            assertTrue(iter.hasNext());
            assertNotNull(iter.next());
        }
        assertFalse(iter.hasNext());
    }

    @Test
    public void getAdjacents_whenNotConnected_shouldReturnEmptyIterable() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v = g.add(1);

        Iterator<IVertex<Integer>> iter = v.getAdjacents().iterator();

        assertNotNull(iter);
        assertFalse(iter.hasNext());
    }

    @Test
    public void getAdjacents_whenConnected_shouldReturnAdjacents() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(-1);
        IVertex<Integer> v2, v3;
        for (int i = 0; i < 10; i++) {
            v2 = g.add(i);
            v1.connect(v2, 1);
        }

        Iterator<IVertex<Integer>> iter = v1.getAdjacents().iterator();
        for (int i = 0; i < 10; i++) {
            assertTrue(iter.hasNext());

            v2 = g.find(i);
            v3 = iter.next();

            assertSame(v2, v3);
        }
        assertFalse(iter.hasNext());
    }

    @Test
    public void getEdgeCount_whenHasNoEdges_shouldReturnZero() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex v1 = g.add(1);

        assertEquals(0, v1.getEdgeCount());
    }

    @Test
    public void getEdgeCount_whenHasEdges_shouldReturnEdgeCount() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2;

        for (int i = 0; i < 10; i++) {
            v2 = g.add(i + 2);

            assertEquals(i, v1.getEdgeCount());
            assertEquals(0, v2.getEdgeCount());

            v1.connect(v2, 1);

            assertEquals(i + 1, v1.getEdgeCount());
            assertEquals(1, v2.getEdgeCount());
        }
    }

}
