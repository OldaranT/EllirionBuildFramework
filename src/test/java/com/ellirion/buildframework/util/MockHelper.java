package com.ellirion.buildframework.util;

import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class MockHelper {

    /**
     * Mocked air block.
     */
    public static final Block MOCK_AIR_BLOCK = createMockBlock(true, false, Material.AIR);

    /**
     * Create a mockblock.
     * @param isEmpty check if empty.
     * @param isLiquid check for liquid.
     * @param material material to make the block with.
     * @return a mocked block.
     */
    public static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = Mockito.mock(Block.class);
        final BlockState state = Mockito.mock(BlockState.class);

        Mockito.when(mockBlock.getType()).thenReturn(material);
        Mockito.when(mockBlock.getState()).thenReturn(state);

        return mockBlock;
    }

    /**
     * Create a mock world.
     * @return return a mocked world.
     */
    public static World createDefaultWorld() {
        final World mockWorld = Mockito.mock(CraftWorld.class);
        final WorldServer mockHandle = Mockito.mock(WorldServer.class);

        Mockito.when(mockWorld.getBlockAt(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
                                          ArgumentMatchers.anyInt())).thenReturn(MOCK_AIR_BLOCK);
        Mockito.when(((CraftWorld) mockWorld).getHandle()).thenReturn(mockHandle);

        return mockWorld;
    }
}
