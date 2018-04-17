package com.ellirion.buildframework.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vertex<T> implements Iterable<Edge<T>> {

    private Graph<T> graph;
    private List<Edge<T>> edges;
    @Getter private T data;

    /**
     * Construct a Vertex using {@code data} as the data.
     * @param graph The graph this Vertex belongs to
     * @param data The data this Vertex represents
     */
    Vertex(final Graph<T> graph, final T data) {
        this.graph = graph;
        this.edges = new ArrayList<>();
        this.data = data;
    }

    /**
     * Find the Edge connecting this Vertex to Vertex {@code v}.
     * @param v The other Vertex the Edge connects to
     * @return The Edge connecting this Vertex to the other Vertex, if any.
     */
    public Edge<T> findEdge(Vertex<T> v) {
        for (Edge<T> edge : edges) {
            if ((edge.getA() == this && edge.getB() == v)
                || (edge.getB() == this && edge.getA() == v)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Find the Edge connecting this Vertex to a Vertex representing data {@code data}.
     * @param data The data the other Vertex represents that the Edge connects to
     * @return The Edge connecting this Vertex to the other Vertex, if any.
     */
    public Edge<T> findEdge(T data) {
        for (Edge<T> edge : edges) {
            if ((edge.getA() == this && edge.getB().data == data)
                    || (edge.getB() == this && edge.getA().data == data)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Connect this Vertex to Vertex {@code v} with a weight of 1.
     * NOTE: If they were already connected with a weight GREATER than 1, the weight will
     * be updated to the minimum of the previous and new weight.
     * @param v The Vertex to connect to
     */
    public void connect(Vertex<T> v) {
        connect(v, 1);
    }

    /**
     * Connect this Vertex to Vertex {@code v} with a weight of {@code weight}.
     * NOTE: If they were already connected with a weight GREATER than {@code weight}, the weight will
     * be updated to the minimum of the previous and new weight.
     * @param v The Vertex to connect to
     * @param weight The weight of the connection
     */
    public void connect(Vertex<T> v, double weight) {
        Edge<T> edge = findEdge(v);
        if (edge != null) {
            edge.setWeight(Math.min(edge.getWeight(), weight));
            return;
        }
        edge = new Edge<>(this, v, weight);
        edges.add(edge);
        v.edges.add(edge);
    }

    /**
     * Remove an Edge between this Vertex and Vertex {@code v}, if it exists.
     * @param v The other Vertex the Edge connects to
     */
    public void disconnect(Vertex<T> v) {
        Edge<T> edge = findEdge(v);
        if (edge != null) {
            v.edges.remove(edge);
            edges.remove(edge);
        }
    }

    /**
     * Checks if this Vertex is connected to Vertex {@code v}.
     * @param v The other Vertex
     * @return Whether the two Vertices are connected
     */
    public boolean isConnectedTo(Vertex<T> v) {
        return findEdge(v) != null;
    }

    /**
     * Checks if this Vertex is connected to a Vertex representing data {@code data}.
     * @param data The data the other Vertex represents
     * @return Whether the two Vertices are connected
     */
    public boolean isConnectedTo(T data) {
        return findEdge(data) != null;
    }

    /**
     * Gets the weight between this and Vertex {@code v}, or -1 if no connection exists.
     * @param v The other Vertex
     * @return The weight to Vertex {@code v}, or -1 if no connection exists
     */
    public double getWeightTo(Vertex<T> v) {
        Edge<T> edge = findEdge(v);
        if (edge != null) {
            return edge.getWeight();
        }
        return -1;
    }

    /**
     * Gets the weight between this and a Vertex representing data {@code data}, or -1 if no connection exists.
     * @param data The data the other Vertex represents
     * @return The weight to the Vertex that represents {@code data}, or -1 if no connection exists
     */
    public double getWeightTo(T data) {
        Edge<T> edge = findEdge(data);
        if (edge != null) {
            return edge.getWeight();
        }
        return -1;
    }

    /**
     * Destroy this Vertex. All Edges connecting this Vertex to other Vertices will be deleted,
     * and this Vertex will be removed from the Graph.
     */
    public void destroy() {
        Iterator<Edge<T>> i = edges.iterator();
        while (i.hasNext()) {
            Edge<T> e = i.next();
            if (e.getA() != this) {
                e.getA().edges.remove(e);
            } else {
                e.getB().edges.remove(e);
            }
            i.remove();
        }
        graph.remove(data);
    }

    /**
     * Gets the size of this Vertex.
     * @return The amount of Edges this Vertex has
     */
    public int size() {
        return edges.size();
    }

    @Override
    public Iterator<Edge<T>> iterator() {
        return edges.iterator();
    }
}
