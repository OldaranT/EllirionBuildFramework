package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;

public class TemplateBlock {

    @Getter @Setter private Material material;
    @Nullable @Getter @Setter private MaterialData metadata;
    @Getter @Setter private NBTTagCompound data;

    /**
     * @param material create a templateblock with the given block
     */
    public TemplateBlock(final Material material) {
        this.material = material;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateBlock)) {
            return false;
        }
        TemplateBlock other = (TemplateBlock) obj;

        if (material == null && other.material != null ||
            material != null && other.material == null) {
            return false;
        }
        if (material != null && other.material != null && other.material != material) {
            return false;
        }

        if (metadata == null && other.metadata != null ||
            metadata != null && other.metadata == null) {
            return false;
        }
        if (metadata != null && other.metadata != null && !metadata.equals(other.metadata)) {
            return false;
        }

        if (data == null && other.data != null ||
            data != null && other.data == null) {
            return false;
        }
        if (data != null && other.data != null && !data.equals(other.data)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
