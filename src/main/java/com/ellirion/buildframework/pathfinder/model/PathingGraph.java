package com.ellirion.buildframework.pathfinder.model;

import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.model.graph.IGraph;
import com.ellirion.buildframework.model.graph.IVertex;
import com.ellirion.buildframework.util.Heap;

import java.util.HashMap;
import java.util.Map;

public class PathingGraph implements IGraph<Point> {

    private Map<Point, PathingVertex> vertices;
    private Heap<PathingVertex> heap;

    /**
     * Construct a new empty PathingGraph.
     */
    public PathingGraph() {
        vertices = new HashMap<>();
        heap = new Heap<>();
    }

    /**
     * Gets the lowest PathingVertex to explore.
     * @return The lowest PathingVertex, or null if none remain
     */
    public PathingVertex next() {
        return heap.next();
    }

    /**
     * Remove the vertex {@code v}.
     * @param v The vertex
     */
    public void removeVertex(final PathingVertex v) {
        heap.remove(v);
    }

    /**
     * Re-add the vertex {@code v}.
     * @param v The vertex
     */
    public void addVertex(final PathingVertex v) {
        heap.add(v);
    }

    @Override
    public PathingVertex find(Point point) {
        return vertices.getOrDefault(point, null);
    }

    @Override
    public PathingVertex findOrCreate(Point point) {
        PathingVertex v = vertices.getOrDefault(point, null);
        if (v == null) {
            v = new PathingVertex(this, point);
            vertices.put(point, v);
        }
        return v;
    }

    @Override
    public IVertex<Point> add(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(IVertex<Point> vert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connect(Point a, Point b, double weight) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect(Point a, Point b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean areConnected(Point a, Point b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<? extends IVertex<Point>> getVertices() {
        return vertices.values();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }
}
