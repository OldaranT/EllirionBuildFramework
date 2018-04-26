package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.junit.Assert;
import org.junit.Test;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;

public class MarkerTests {

    @Test
    public void add_AddMarker_ShouldAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));

        Assert.assertTrue(added);
    }

    @Test
    public void add_AddMarkerWhenWorldLocationIsNot0_ShouldAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(5, 5, 5), new Point(3, 3, 3));

        Assert.assertTrue(added);
    }

    @Test
    public void add_AddingSameMarkerMultipleTimes_ShouldOverwritePosition() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        t.addMarker("DOOR", new Point(1, 1, 1), new Point(0, 0, 0));

        Point p = t.getMarkers().get("DOOR");
        Assert.assertEquals(p, new Point(1, 1, 1));
    }

    @Test
    public void add_AddingMarkerOutsideTemplate_ShouldNotAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

        Assert.assertFalse(added);
    }

    @Test
    public void add_OverwritingMarkerOutsideTemplate_ShouldNotOverwrite() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

        Assert.assertFalse(added);
    }

    @Test
    public void remove_RemoveMarker_ShouldRemoveMarker() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        boolean removed = t.removeMarker("DOOR");

        Assert.assertTrue(removed);
    }

    @Test
    public void remove_RemoveNonExistingMarker_ShouldReturnFalse() {
        Template t = template();

        boolean removed = t.removeMarker("DOOR");

        Assert.assertFalse(removed);
    }

    private Template template() {
        Template template = new Template();

        TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    TemplateBlock block = new TemplateBlock(Material.STONE);
                    block.setMetadata(new MaterialData(1, (byte) 0));
                    block.setData(new NBTTagCompound());
                    blocks[x][y][z] = block;
                }
            }
        }
        template.setTemplateBlocks(blocks);

        return template;
    }
}
