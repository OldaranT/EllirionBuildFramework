package com.ellirion.buildframework.pathfinder.model;

import lombok.Getter;
import lombok.Setter;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.model.graph.IEdge;
import com.ellirion.buildframework.model.graph.IVertex;

import java.util.ArrayList;
import java.util.List;

public class PathingVertex implements IVertex<Point>, Comparable<PathingVertex> {

    private PathingGraph graph;
    private Point point;
    private List<PathingVertex> adjacents;

    // A-star variables
    @Getter @Setter private PathingVertex cameFrom;
    @Getter @Setter private double gScore;
    @Getter private double fScore;
    @Getter @Setter private double vScore;
    @Getter @Setter private boolean visited;
    @Getter @Setter private int visitIndex;
    @Getter @Setter private int seenIndex;

    /**
     * Construct a new PathingVertex belonging to PathingGraph {@code graph}.
     * @param graph The graph this vertex belongs to
     * @param point The point this PathingVertex represents
     */
    public PathingVertex(final PathingGraph graph, final Point point) {
        this.graph = graph;
        this.point = point;
        this.adjacents = null;

        this.cameFrom = null;
        this.gScore = Double.POSITIVE_INFINITY;
        this.fScore = Double.POSITIVE_INFINITY;
        this.vScore = 0;

        this.visitIndex = Integer.MAX_VALUE;
        this.seenIndex = Integer.MAX_VALUE;
    }

    /**
     * Sets the fScore of this vertex and reorders it in the remaining queue.
     * @param v The new fScore
     */
    public void setFScore(double v) {
        if (visited) {
            return;
        }

        graph.removeVertex(this);
        fScore = v;
        graph.addVertex(this);
    }

    @Override
    public Point getData() {
        return point;
    }

    @Override
    public IEdge<Point> findEdge(IVertex<Point> vert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IEdge<Point> findEdge(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connect(IVertex<Point> vert, double weight) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void connect(IEdge<Point> edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect(IVertex<Point> vert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void disconnect(IEdge<Point> edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConnectedTo(IVertex<Point> vert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConnectedTo(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getWeightTo(IVertex<Point> vert) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getWeightTo(Point point) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<IEdge<Point>> getEdges() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<PathingVertex> getAdjacents() {
        if (adjacents != null) {
            return adjacents;
        }

        adjacents = new ArrayList<>();
        adjacents.add(graph.findOrCreate(point.north()));
        adjacents.add(graph.findOrCreate(adjacents.get(0).getData().up()));
        adjacents.add(graph.findOrCreate(adjacents.get(0).getData().down()));
        adjacents.add(graph.findOrCreate(point.south()));
        adjacents.add(graph.findOrCreate(adjacents.get(3).getData().up()));
        adjacents.add(graph.findOrCreate(adjacents.get(3).getData().down()));
        adjacents.add(graph.findOrCreate(point.east()));
        adjacents.add(graph.findOrCreate(adjacents.get(6).getData().up()));
        adjacents.add(graph.findOrCreate(adjacents.get(6).getData().down()));
        adjacents.add(graph.findOrCreate(point.west()));
        adjacents.add(graph.findOrCreate(adjacents.get(9).getData().up()));
        adjacents.add(graph.findOrCreate(adjacents.get(9).getData().down()));
        return adjacents;
    }

    @Override
    public int getEdgeCount() {
        return 12;
    }

    @Override
    public String toString() {
        return point + ": g=" + gScore + " v=" + vScore + " f=" + fScore + " from=" +
               (cameFrom != null ? cameFrom.getData() : "nowhere") +
               " visited=" + visitIndex + " seen=" + seenIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PathingVertex) {
            return o == this;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return point.hashCode();
    }

    @Override
    public int compareTo(PathingVertex v) {
        return Double.compare(fScore, v.fScore);
    }
}
