package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;

public class TerrainTestUtil {

    protected static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = mock(Block.class);

        when(mockBlock.isEmpty()).thenReturn(isEmpty);
        when(mockBlock.isLiquid()).thenReturn(isLiquid);
        when(mockBlock.getType()).thenReturn(material);

        return mockBlock;
    }

    protected static void setCoordinates(Block mockBlock, final int x, final int y, final int z) {
        when(mockBlock.getX()).thenReturn(x);
        when(mockBlock.getY()).thenReturn(y);
        when(mockBlock.getZ()).thenReturn(z);
    }

    protected static void setBlockAtCoordinates(World world, final int x, final int y, final int z, Material mat) {
        Block mockBlock;
        if (mat == Material.AIR) {
            mockBlock = createMockBlock(true, false, mat);
        } else if (mat == Material.WATER || mat == Material.LAVA) {
            mockBlock = createMockBlock(false, true, mat);
        } else {
            mockBlock = createMockBlock(false, false, mat);
        }
        setCoordinates(mockBlock, x, y, z);
        // when setType is called change the material getType returns
        doAnswer((Answer) invocation -> {
            Material material = invocation.getArgument(0);
            Block b = (Block) invocation.getMock();
            when(b.getType()).thenReturn(material);
            return null;
        }).when(mockBlock).setType(any(Material.class));

        when(world.getBlockAt(x, y, z)).thenReturn(mockBlock);
    }
}
