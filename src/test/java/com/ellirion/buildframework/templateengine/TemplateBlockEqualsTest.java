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
import com.ellirion.buildframework.templateengine.model.TemplateBlock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
@PowerMockIgnore("javax.management.*")
public class TemplateBlockEqualsTest {

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
    public void equals_SameBlock_ShouldBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        Assert.assertEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_DifferentMaterial_ShouldNotBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        blocks[1].setMaterial(Material.AIR);

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_MissingMaterial_ShouldNotBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        blocks[1].setMaterial(null);

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_DifferentMetadata_ShouldNotBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        blocks[1].setMetadata(new MaterialData(0, (byte) 0));

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_SameWithoutMetadata_ShouldBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        blocks[0].setMetadata(null);
        blocks[1].setMetadata(null);

        Assert.assertEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_MissingMetadata_ShouldNotBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        blocks[1].setMetadata(null);

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_SameWithSameNBTData_ShouldBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        NBTTagCompound ntc = new NBTTagCompound();
        ntc.setString("a", "a");
        blocks[0].setData(ntc);
        blocks[1].setData(ntc);

        Assert.assertEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_SameWithDifferentNBTData_ShouldNotBeEquals() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        NBTTagCompound ntc = new NBTTagCompound();
        ntc.setString("a", "a");
        NBTTagCompound ntc2 = new NBTTagCompound();
        ntc.setString("a", "b");
        blocks[0].setData(ntc);
        blocks[1].setData(ntc2);

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    @Test
    public void equals_MissingNBTData_ShouldBeEqual() {
        TemplateBlock[] blocks = createTemplateBlocks(2);

        NBTTagCompound ntc = new NBTTagCompound();
        ntc.setString("a", "a");
        blocks[0].setData(null);
        blocks[1].setData(ntc);

        Assert.assertNotEquals(blocks[0], blocks[1]);
    }

    private TemplateBlock[] createTemplateBlocks(int n) {
        TemplateBlock[] templates = new TemplateBlock[n];

        for (int i = 0; i < n; i++) {
            TemplateBlock a = new TemplateBlock(Material.STONE);
            a.setMetadata(new MaterialData(1, (byte) 0));
            templates[i] = a;
        }

        return templates;
    }
}
