package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import javax.annotation.Nullable;

public class TemplateBlock {
    @Getter @Setter private Material material;

    @Nullable
    @Getter @Setter private MaterialData metadata;

    @Getter @Setter private NBTTagCompound data;

    /**
     * @param material create a templateblock with the given block
     */
    public TemplateBlock(final Material material) {
        this.material = material;
    }
}
