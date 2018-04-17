package com.ellirion.buildframework.model;

import lombok.Getter;
import lombok.Setter;

public class Edge<T> {

    @Getter private Vertex<T> a, b;
    @Getter @Setter private double weight;

    Edge(final Vertex<T> a, final Vertex<T> b, final double weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

}
