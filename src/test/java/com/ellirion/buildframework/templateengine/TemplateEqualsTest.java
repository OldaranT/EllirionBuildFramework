package com.ellirion.buildframework.templateengine;

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
public class TemplateEqualsTest {

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
    public void equals_whenEmptyTemplate_shouldBeEqual() {
        Template a = new Template();
        Template b = new Template();

        Assert.assertEquals(a, b);
    }

    @Test
    public void equals_whenFilledTemplate_shouldBeEqual() {
        Template[] templates = createSameTemplates(2);

        Assert.assertEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenDifferentName_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[1].setTemplateName("not template");

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenDifferentSize_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        TemplateBlock block = new TemplateBlock(Material.STONE);
        block.setMetadata(new MaterialData(1, (byte) 0));
        TemplateBlock[][][] blocks = new TemplateBlock[][][] {{{block}}};
        templates[1].setTemplateBlocks(blocks);

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenSameSizeDifferentBlocks_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        TemplateBlock block = new TemplateBlock(Material.COBBLESTONE);
        block.setMetadata(new MaterialData(4, (byte) 0));
        templates[0].getTemplateBlocks()[0][0][0] = block;

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenMissingMarker_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[1].getMarkers().remove("GROUND");

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenExtraMarker_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[1].getMarkers().put("TEST", new Point(0, 0, 0));

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenDifferentMarker_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[1].getMarkers().remove("GROUND");
        templates[1].getMarkers().put("TEST", new Point(0, 0, 0));

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenSameMarkerAtDifferentPlace_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[1].getMarkers().put("GROUND", new Point(1, 0, 0));

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenDifferentType_shouldNotBeEqual() {
        Assert.assertNotEquals(new Template(), new Object());
    }

    @Test
    public void equals_whenMissingName_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[0].setTemplateName(null);

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    @Test
    public void equals_whenMissingBlocks_shouldNotBeEqual() {
        Template[] templates = createSameTemplates(2);

        templates[0].setTemplateBlocks(null);

        Assert.assertNotEquals(templates[0], templates[1]);
    }

    private Template[] createSameTemplates(int n) {
        Template[] templates = new Template[n];

        for (int i = 0; i < n; i++) {
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock a = new TemplateBlock(Material.STONE);
            a.setMetadata(new MaterialData(1, (byte) 0));
            TemplateBlock b = new TemplateBlock(Material.STONE);
            b.setMetadata(new MaterialData(1, (byte) 3));
            TemplateBlock c = new TemplateBlock(Material.SIGN);
            c.setMetadata(new MaterialData(323, (byte) 5));
            TemplateBlock[][][] blocks = new TemplateBlock[][][] {{{a, b, c}}};
            t.setTemplateBlocks(blocks);

            t.getMarkers().put("DOOR", new Point(0, 0, 0));
            t.getMarkers().put("GROUND", new Point(0, 0, 2));

            templates[i] = t;
        }

        return templates;
    }
}
