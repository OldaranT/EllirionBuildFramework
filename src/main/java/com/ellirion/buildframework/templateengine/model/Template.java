package com.ellirion.buildframework.templateengine.model;

import com.ellirion.buildframework.model.BoundingBox;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.material.MaterialData;

public class Template {
    private static String data = "data";

    /**
     * ID of the template.
     */
    @Getter @Setter
    private int templateID;

    /**
     * Name of the template.
     */
    @Getter @Setter
    private String templateName;

    /**
     * Map of all positions with corresponding TemplateBlocks.
     */
    @Getter @Setter
    private TemplateBlock[][][] templateBlocks;

    /**
     * Empty constructor.
     */
    public Template() {
        //
    }

    /**
     *
     * @param name Name of the template
     * @param selection Selected area
     */
    public Template(final String name, final Selection selection) {
        templateName = name;

        //get all blocks from the area
        Location start = selection.getMinimumPoint();
        Location end = selection.getMaximumPoint();

        int startX = Math.min((int) start.getX(), (int) end.getX());
        int startY = Math.min((int) start.getY(), (int) end.getY());
        int startZ = Math.min((int) start.getZ(), (int) end.getZ());

        int endX = Math.max((int) start.getX(), (int) end.getX());
        int endY = Math.max((int) start.getY(), (int) end.getY());
        int endZ = Math.max((int) start.getZ(), (int) end.getZ());

        int templateX = 0;
        int templateY = 0;
        int templateZ = 0;

        int xDepth = endX - startX + 1;
        int yDepth = endY - startY + 1;
        int zDepth = endZ - startZ + 1;
        templateBlocks = new TemplateBlock[xDepth][yDepth][zDepth];

        World world = selection.getWorld();
        CraftWorld w = (CraftWorld) world;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    templateBlocks[templateX][templateY][templateZ] = new TemplateBlock(world.getBlockAt(x, y, z).getType());

                    Block b = world.getBlockAt(x, y, z);
                    BlockState state = b.getState();
                    templateBlocks[templateX][templateY][templateZ].setMetadata(state.getData());

                    TileEntity te = w.getTileEntityAt(x, y, z);
                    if (te != null) {
                        templateBlocks[templateX][templateY][templateZ].setData(te.save(new NBTTagCompound()));
                    }
                    templateZ++;
                }
                templateZ = 0;
                templateY++;
            }
            templateY = 0;
            templateX++;
        }
    }


    /**
     * Place a template in the world at a given location.
     * @param loc location to place the template.
     */
    public void putTemplateInWorld(Location loc) {

        int xDepth = this.getTemplateBlocks().length;
        int yDepth = this.getTemplateBlocks()[0].length;
        int zDepth = this.getTemplateBlocks()[0][0].length;

        CraftWorld w = (CraftWorld) loc.getWorld();

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {

                    int locX = (int) loc.getX() + x;
                    int locY = (int) loc.getY() + y;
                    int locZ = (int) loc.getZ() + z;

                    Block b = w.getBlockAt(locX, locY, locZ);

                    b.setType(this.getTemplateBlocks()[x][y][z].getMaterial());
                    b.getState().update();

                    MaterialData copiedState = this.getTemplateBlocks()[x][y][z].getMetadata();
                    BlockState blockState = b.getState();
                    blockState.setData(copiedState);
                    blockState.update();

                    TileEntity te = w.getHandle().getTileEntity(new BlockPosition(locX, locY, locZ));

                    if (te != null) {
                        NBTTagCompound ntc = this.getTemplateBlocks()[x][y][z].getData();
                        ntc.setInt("x", locX);
                        ntc.setInt("y", locY);
                        ntc.setInt("z", locZ);
                        te.load(ntc);
                    }
                }
            }
        }
    }

    /**
     *
     * @return boundingbox of the template
     */
    public BoundingBox getBoundingBox() {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        return new BoundingBox(0, 0, 0, xDepth - 1, yDepth - 1, zDepth - 1);
    }

    /**
     * Creates an NBTTagCompound from a given template.
     * @param t The template to convert to NBT
     * @return The NBT data for the given template
     */
    public static NBTTagCompound toNBT(Template t) {
        NBTTagCompound ntc = new NBTTagCompound();

        ntc.setString("templateName", t.getTemplateName());
        ntc.set("boundingBox", BoundingBox.toNBT(t.getBoundingBox()));

        TemplateBlock[][][] templateBlocks = t.getTemplateBlocks();
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        NBTTagList ntcArrayX = new NBTTagList();
        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {

                    TemplateBlock tb = templateBlocks[x][y][z];
                    NBTTagCompound block = new NBTTagCompound();

                    //type of the block
                    block.setString("material", tb.getMaterial().name());

                    //metadata of the block
                    NBTTagCompound metadata = new NBTTagCompound();
                    metadata.setInt("type", tb.getMetadata().getItemTypeId());
                    metadata.setByte(data, tb.getMetadata().getData());
                    block.set("metadata", metadata);

                    //nbt data of the block
                    if (tb.getData() != null) {
                        block.set(data, tb.getData());
                    }

                    ntcArrayX.add(block);
                }
            }
        }
        ntc.set("templateBlocks", ntcArrayX);

        return ntc;
    }

    /**
     * Creates a template based on NBT data.
     * @param ntc The NBT data to construct the template out of
     * @return The created template
     */
    public static Template fromNBT(final NBTTagCompound ntc) {
        Template t = new Template();

        t.setTemplateName(ntc.getString("templateName"));

        NBTTagList arrayX = ntc.getList("templateBlocks", 9 + 1);
        BoundingBox bb = BoundingBox.fromNBT(ntc.getCompound("boundingBox"));
        int xDepth = bb.getWidth();
        int yDepth = bb.getHeight();
        int zDepth = bb.getDepth();
        TemplateBlock[][][] tBlocks = new TemplateBlock[xDepth][yDepth][zDepth];
        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    int offZ = z;
                    int offY = y * zDepth;
                    int offX = x * zDepth * yDepth;
                    int i = offX + offY + offZ;

                    NBTTagCompound blockData = arrayX.get(i);

                    //get the material of the block
                    String material = blockData.getString("material");
                    TemplateBlock tb = new TemplateBlock(Material.valueOf(material));

                    //get the metadata of the block
                    NBTTagCompound metadata = blockData.getCompound("metadata");
                    MaterialData meta = new MaterialData(metadata.getInt("type"), metadata.getByte(data));
                    tb.setMetadata(meta);
                    tBlocks[x][y][z] = tb;

                    //get the nbt data of the block
                    NBTTagCompound nbtdata = blockData.getCompound(data);
                    if (nbtdata != null) {
                        tb.setData(nbtdata);
                    }
                }
            }
        }
        t.setTemplateBlocks(tBlocks);

        return t;
    }
}
