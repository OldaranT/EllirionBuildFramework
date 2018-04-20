package com.ellirion.buildframework.templateengine.model;

import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.material.MaterialData;

import java.util.HashMap;

public class Template {
    /**
     * Enum of markers.
     */
    public enum Markers { DOOR, GROUND, PATH }
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

    private HashMap<String, Point> markers;


    /**
     *
     * @param name Name of the template.
     * @param selection Selected area.
     */
    public Template(final String name, final Selection selection) {
        templateName = name;
        markers = new HashMap<>();

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
     * Add market to the template.
     * @param name Name of the marker.
     * @param point Point of the maker.
     */
    public void addMarker(String name, Point point) {
        markers.put(name, point);
    }

    /**
     * Get point of a marker.
     * @param name name of the marker
     * @return Point of the selected marker.
     */
    public Point findMarker(String name) {
        return markers.get(name);
    }

    /**
     * Remove marker.
     * @param name name of the marker
     * @return Point of the selected marker.
     */
    public boolean removeMarker(String name) {
       return markers.remove(name) != null;
    }

    /**
     *
     * @return boundingbox of the template.
     */
    public BoundingBox getBoundingBox() {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        return new BoundingBox(0, 0, 0, xDepth, yDepth, zDepth);
    }


    /**
     * Convert the enum to a string and returns it.
     * @return Enum in strong form.
     */
    public static String markersToString() {
        String markers = "";
        for (Template.Markers m : Template.Markers.values()) {
            markers += ChatColor.RESET + "" + ChatColor.BOLD + m.name().toLowerCase();
            if (m != Template.Markers.values()[Template.Markers.values().length - 1]) {
                markers += ChatColor.RESET + ", ";
            }
        }
        return  markers;
    }
}
