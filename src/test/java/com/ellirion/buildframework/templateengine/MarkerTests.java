package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
@PowerMockIgnore("javax.management.*")
public class MarkerTests {

    private List<String> markers = new ArrayList<>();

    @Before
    public void setup() {
        markers.add("GROUND");
        markers.add("PATH");
        markers.add("DOOR");

        mockStatic(BuildFramework.class);

        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);
        when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

        when(mockConfig.getStringList("Markers")).thenReturn(markers);
    }

    @Test
    public void addMarker_whenAllValuesEnteredCorrectly_shouldAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));

        Assert.assertTrue(added);
    }

    @Test
    public void addMarker_whenWorldLocationIsNot0_shouldAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(5, 5, 5), new Point(3, 3, 3));

        Assert.assertTrue(added);
    }

    @Test
    public void addMarker_whenSameMarkerAddedMultipleTimes_shouldOverwritePosition() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        t.addMarker("DOOR", new Point(1, 1, 1), new Point(0, 0, 0));

        Point p = t.getMarkers().get("DOOR");
        Assert.assertEquals(p, new Point(1, 1, 1));
    }

    @Test
    public void addMarker_whenOutsideTemplate_shouldNotAddMarker() {
        Template t = template();

        boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

        Assert.assertFalse(added);
    }

    @Test
    public void overwriteMarker_whenOutsideTemplate_shouldNotOverwrite() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

        Assert.assertFalse(added);
    }

    @Test
    public void removeMarker_whenAllValuesEnteredCorrectly_shouldRemoveMarker() {
        Template t = template();

        t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
        boolean removed = t.removeMarker("DOOR");

        Assert.assertTrue(removed);
    }

    @Test
    public void removeMarker_whenMarkerDoesntExist_shouldReturnFalse() {
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
