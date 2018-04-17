package com.ellirion.buildframework.templateengine.model;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.server.v1_12_R1.Position;
import org.bukkit.Location;
import org.bukkit.World;

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

        templateBlocks = new TemplateBlock[endX - startX + 1][endY - startY + 1][endZ - startZ + 1];

        World world = selection.getWorld();

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    templateBlocks[templateX][templateY][templateZ] = new TemplateBlock(world.getBlockAt(x, y, z));
                    templateZ++;
                }
                templateZ = 0;
                templateY++;
            }
            templateY = 0;
            templateX++;
        }

        logBlocks();
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
                    BuildFramework.getInstance().getLogger().log(Level.INFO, "(" + x + "," + y + "," + z + ") " + templateBlocks[x][y][z].block.getType().name());
                }
            }
        }
    }

    public BoundingBox getBoundingBox(){
        int x = templateBlocks[0][0][0].block.getX();
        int y = templateBlocks[0][0][0].block.getY();
        int z = templateBlocks[0][0][0].block.getZ();

        int x2 = templateBlocks[templateBlocks.length - 1][templateBlocks[0].length - 1][templateBlocks[0][0].length - 1].block.getX();
        int y2 = templateBlocks[templateBlocks.length - 1][templateBlocks[0].length - 1][templateBlocks[0][0].length - 1].block.getY();
        int z2 = templateBlocks[templateBlocks.length - 1][templateBlocks[0].length - 1][templateBlocks[0][0].length - 1].block.getZ();

        return new BoundingBox(x, y, z, x2 - x, y2 - y, z2 - z);
    }
}
