package com.ellirion.buildframework.pathfinder.model;

import lombok.Getter;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.model.graph.IGraph;
import com.ellirion.buildframework.model.graph.IVertex;
import com.ellirion.buildframework.util.Heap;

import java.util.HashMap;
import java.util.Map;

public class PathingGraph implements IGraph<Point> {

    private Map<Point, PathingVertex> vertices;
    private ScoringList<PathingVertex, Double> list;
    private Heap<PathingVertex, Double> heap;
    @Getter private boolean useHeap;

    /**
     * Construct a new empty PathingGraph.
     * @param useHeap Whether to use a heap or scoringlist.
     */
    public PathingGraph(final boolean useHeap) {
        vertices = new HashMap<>();
        list = new ScoringList<>(PathingVertex::getFScore);
        heap = new Heap<>(PathingVertex::getFScore);
        this.useHeap = useHeap;
    }

    /**
     * Gets the lowest PathingVertex to explore.
     * @return The lowest PathingVertex, or null if none remain
     */
    public PathingVertex next() {
        if (useHeap) {
            return heap.next();
        } else {
            return list.lowest();
        }
    }

    /**
     * Remove the vertex {@code v}.
     * @param v The vertex
     */
    public void removeVertex(final PathingVertex v) {
        if (useHeap) {
            heap.remove(v);
        } else {
            list.remove(v);
        }
    }

    /**
     * Re-add the vertex {@code v}.
     * @param v The vertex
     */
    public void addVertex(final PathingVertex v) {
        if (useHeap) {
            heap.insert(v);
        } else {
            list.insert(v);
        }
    }

    @Override
    public PathingVertex find(Point point) {
        return findOrCreate(point);
    }

    @Override
    public PathingVertex findOrCreate(Point point) {
        PathingVertex v = vertices.getOrDefault(point, null);
        if (v == null) {
            v = new PathingVertex(this, point);
            vertices.put(point, v);
            //heap.insert(v);
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
