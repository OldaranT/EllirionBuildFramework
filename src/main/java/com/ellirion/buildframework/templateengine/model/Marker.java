package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.stream.Location;

public class Marker {

    /**
     * Name of the marker.
     */
    @Getter @Setter
    private String name;
    /**
     * Location of the marker.
     */
    @Getter @Setter
    private Location location;

    public Marker(String name, Location location) {
        this.name = name;
        this.location = location;
    }

}
