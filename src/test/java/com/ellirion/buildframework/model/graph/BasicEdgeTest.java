package com.ellirion.buildframework.model.graph;

import org.junit.Test;

import static org.junit.Assert.*;

public class BasicEdgeTest {

    @Test
    public void involvesByVertex_whenNotInvolved_shouldReturnFalse() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IVertex<Integer> v3 = g.add(3);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertFalse(e.involves(v3));
    }

    @Test
    public void involvesByVertex_whenInvolved_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertTrue(e.involves(v1));
        assertTrue(e.involves(v1));
    }

    @Test
    public void involvesByData_whenNotInvolved_shouldReturnFalse() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IVertex<Integer> v3 = g.add(3);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertFalse(e.involves(3));
    }

    @Test
    public void involvesByData_whenInvolved_shouldReturnTrue() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertTrue(e.involves(1));
        assertTrue(e.involves(2));
    }

    @Test
    public void other_whenNotInvolved_shouldReturnNull() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IVertex<Integer> v3 = g.add(3);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertNull(e.other(v3));
    }

    @Test
    public void other_whenInvolvedOnce_shouldReturnOther() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v1 = g.add(1);
        IVertex<Integer> v2 = g.add(2);
        IEdge<Integer> e = new BasicEdge<>(v1, v2, 1);

        assertSame(v2, e.other(v1));
        assertSame(v1, e.other(v2));
    }

    @Test
    public void other_whenInvolvedTwice_shouldReturnSelf() {
        IGraph<Integer> g = new BasicGraph<>();
        IVertex<Integer> v = g.add(1);
        IEdge<Integer> e = new BasicEdge<>(v, v, 1);

        assertSame(v, e.other(v));
    }

}
