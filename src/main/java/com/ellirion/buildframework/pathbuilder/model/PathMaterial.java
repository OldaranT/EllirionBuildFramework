package com.ellirion.buildframework.pathbuilder.model;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;

public class PathMaterial {

    @Getter private Material mat;
    @Getter private byte data;

    /**
     * Constructor.
     * @param mat the material
     * @param data the data
     */
    public PathMaterial(final Material mat, final byte data) {
        this.mat = mat;
        this.data = data;
    }

    /**
     * serialize a pathmaterial to NBT.
     * @param pm PathMaterial
     * @return NBT
     */
    public static NBTTagCompound serialize(PathMaterial pm) {
        NBTTagCompound ntc = new NBTTagCompound();

        ntc.setString("mat", pm.mat.name());
        ntc.setByte("data", pm.data);

        return ntc;
    }

    /**
     * Deserialize NBT into PathMaterial.
     * @param ntc NBTTagCompound
     * @return pathmaterial
     */
    public static PathMaterial deserialize(NBTTagCompound ntc) {
        Material mat = Material.valueOf(ntc.getString("mat"));
        byte data = ntc.getByte("data");

        PathMaterial pm = new PathMaterial(mat, data);

        return pm;
    }
}
