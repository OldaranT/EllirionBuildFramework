package com.ellirion.buildframework.templateengine.model;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.Position;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.HashMap;
import java.util.logging.Level;

public class Template {
    /**
     * ID of the template.
     */
    @Getter @Setter
    private int templateID;

    /**
     *
     */
    //BoundingBox boundingBox;

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

    @Getter @Setter
    private HashMap<Location, Sign> signList;

    /**
     *
     * @param name Name of the template
     * @param selection Selected area
     */
    public Template(final String name, final Selection selection) {
        templateName = name;
        signList = new HashMap<Location, Sign>();

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

//        logBlocks();
    }

    /**
     *
     * @param pos Position of the block to place a marker
     * @param marker Name of the marker
     * @return whether the marker was added
     */
    public boolean addMarker(Position pos, final String marker) {
        return templateBlocks[(int) pos.getX()][(int) pos.getY()][(int) pos.getZ()].addMarker(marker);
    }

    private void logBlocks() {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        BuildFramework.getInstance().getLogger().log(Level.INFO, "Template " + templateName + ":");
        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    BuildFramework.getInstance().getLogger().log(Level.INFO, "(" + x + "," + y + "," + z + ") " + templateBlocks[x][y][z].getMaterial().name());
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

        return new BoundingBox(0, 0, 0, xDepth, yDepth, zDepth);
    }
}
