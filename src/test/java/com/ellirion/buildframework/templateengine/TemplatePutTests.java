package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.material.MaterialData;
import org.junit.Test;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TemplatePutTests {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);

    private static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = mock(Block.class);
        final BlockState state = mock(BlockState.class);

        when(mockBlock.getType()).thenReturn(material);
        when(mockBlock.getState()).thenReturn(state);

        return mockBlock;
    }

    @Test
    public void put_PutTemplateInWorld_ShouldPutTemplateInWorld() {
        Template t = createTemplate();

        t.putTemplateInWorld(createDefaultLocation());

        TemplateBlock[][][] blocks = t.getTemplateBlocks();
        int invocationCount = blocks.length * blocks[0].length * blocks[0][0].length;

        verify(MOCK_BLOCK_AIR, times(invocationCount)).setType(any());
    }

    private Template createTemplate() {
        Template template = new Template();
        template.setTemplateName("template");

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

    private World createDefaultWorld() {
        final World mockWorld = mock(CraftWorld.class);
        final WorldServer mockHandle = mock(WorldServer.class);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        when(((CraftWorld) mockWorld).getHandle()).thenReturn(mockHandle);

        return mockWorld;
    }

    private Location createDefaultLocation() {
        final Location mockLocation = mock(Location.class);
        World w = createDefaultWorld();
        when(mockLocation.getWorld()).thenReturn(w);
        return mockLocation;
    }
}
