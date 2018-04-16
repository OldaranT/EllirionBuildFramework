package com.ellirion.buildframework.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Graph<T> implements Iterable<Vertex<T>> {

    private List<Vertex<T>> vertices;

    /**
     * Construct a new Graph with no vertices.
     */
    public Graph() {
        this.vertices = new ArrayList<>();
    }

    /**
     * Find a Vertex representing the data {@code data}.
     * @param data The data the Vertex represents
     * @return The Vertex representing the data, if it exists
     */
    public Vertex<T> find(T data) {
        for (Vertex<T> v : vertices) {
            if (v.getData().equals(data)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Find or create a Vertex representing the data {@code data}.
     * @param data The data the Vertex represents
     * @return The Vertex representing the data
     */
    public Vertex<T> findOrCreate(T data) {
        Vertex<T> v = find(data);
        if (v != null) {
            return v;
        }

        v = new Vertex<>(this, data);
        vertices.add(v);
        return v;
    }

    /**
     * Adds a Vertex using {@code data} as the data.
     * @param data The data
     * @return The Vertex representing the data
     */
    public Vertex<T> add(T data) {
        return findOrCreate(data);
    }

    /**
     * Remove the Vertex belonging to the {@code data}.
     * @param data The data the to-be-deleted Vertex represents
     */
    public void remove(T data) {
        Vertex<T> v = find(data);
        if (v != null) {
            vertices.remove(v);
            v.destroy();
        }
    }

    /**
     * Connect two data points with the given {@code weight}. The vertices representing the data are
     * created if they did not already exist.
     * @param a The first data point
     * @param b The second data point
     * @param weight The weight of this connection
     */
    public void connect(T a, T b, int weight) {
        Vertex<T> va = findOrCreate(a);
        Vertex<T> vb = findOrCreate(b);
        va.connect(vb, weight);
    }

    /**
     * Disconnect two data points if they exist and are connected.
     * @param a The first data point
     * @param b The second data point
     */
    public void disconnect(T a, T b) {
        Vertex<T> va = find(a);
        Vertex<T> vb = find(b);
        if (va != null && vb != null) {
            va.disconnect(vb);
        }
    }

    /**
     * Checks whether the Vertices representing the given data points are connected.
     * @param a The first data point
     * @param b the second data point
     * @return Whether the two data points are connected
     */
    public boolean areConnected(T a, T b) {
        Vertex<T> va = find(a);
        Vertex<T> vb = find(b);
        if (va == null || vb == null) {
            return false;
        }
        return va.isConnectedTo(vb);
    }

    /**
     * Gets the size of this Graph.
     * @return The amount of Vertices in this Graph.
     */
    public int size() {
        return vertices.size();
    }

    @Override
    public Iterator<Vertex<T>> iterator() {
        return vertices.iterator();
    }

}
