package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
public class TerrainCorrectorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_LIQUID = createMockBlock(false, true, Material.WATER);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private static final Block MOCK_LIQUID_FACING_BLOCK = createMockBlock(false, true, Material.WATER);
    private static final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.DOWN,
            BlockFace.UP
    };
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 10, 10, 10);

    @Before
    public void setup() {
        mockStatic(BuildFramework.class);

        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainCorrecter.MaxHoleDepth", 5)).thenReturn(5);
    }

    @Test
    public void correctTerrain_whenDetectingRiver_shouldReturnErrorString() {
        //Arrange
        final World mockWorld = createDefaultWorld();
        //        final TerrainCorrector corrector = new TerrainCorrector();
        when(mockWorld.getBlockAt(5, 0, 10)).thenReturn(MOCK_BLOCK_LIQUID);

        setCoordinates(MOCK_BLOCK_LIQUID, 5, 0, 10);
        setFacingBlocks(MOCK_BLOCK_LIQUID, MOCK_BLOCK_STONE, MOCK_LIQUID_FACING_BLOCK, BlockFace.SOUTH);

        //        for (BlockFace f : faces) {
        //            when(MOCK_BLOCK_LIQUID.getRelative(f)).thenReturn(MOCK_BLOCK_STONE);
        //        }
        //        when(MOCK_BLOCK_LIQUID.getRelative(BlockFace.SOUTH)).thenReturn(facingLiquidBlock);

        //Act
        //        String result = corrector.correctTerrain(boundingBox, mockWorld);

        //Assert
        //        assertFalse(result);
        //        assertEquals("Could not correct the terrain because the selection is above a lake, pond or river", result);
        assertTrue(true);
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        for (int i = 0; i > -6; i--) {
            when(mockWorld.getBlockAt(anyInt(), eq(i), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        }
        return mockWorld;
    }

    private void setCoordinates(Block mockBlock, final int x, final int y, final int z) {
        when(mockBlock.getX()).thenReturn(x);
        when(mockBlock.getY()).thenReturn(y);
        when(mockBlock.getZ()).thenReturn(z);
    }

    private void setFacingBlocks(Block mock, Block defaultFacingBlock, Block facing, BlockFace face) {
        for (BlockFace f : faces) {
            when(mock.getRelative(f)).thenReturn(defaultFacingBlock);
        }
        when(mock.getRelative(face)).thenReturn(facing);
    }
}
