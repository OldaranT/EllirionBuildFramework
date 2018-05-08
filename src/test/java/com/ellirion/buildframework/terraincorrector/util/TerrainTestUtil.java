package com.ellirion.buildframework.terraincorrector.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import static org.mockito.Mockito.*;

public class TerrainTestUtil {

    public static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = mock(Block.class);

        when(mockBlock.isEmpty()).thenReturn(isEmpty);
        when(mockBlock.isLiquid()).thenReturn(isLiquid);
        when(mockBlock.getType()).thenReturn(material);

        return mockBlock;
    }
}
