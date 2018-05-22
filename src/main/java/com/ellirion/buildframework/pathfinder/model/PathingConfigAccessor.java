package com.ellirion.buildframework.pathfinder.model;

import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

public interface PathingConfigAccessor {

    /**
     * Gets the value of {@code base}.
     * @param base The tag
     * @param key The key
     * @return The result message
     */
    String get(NBTBase base, String key);

    /**
     * Sets the value of {@code base} derived from string {@code value}
     * and returns the result message.
     * @param compound The compound
     * @param base The tag
     * @param key The key
     * @param value The new value
     * @return The result message
     */
    String set(NBTTagCompound compound, NBTBase base, String key, String value);

}
