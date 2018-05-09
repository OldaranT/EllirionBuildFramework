package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.ellirion.buildframework.model.BoundingBox;

import static com.ellirion.buildframework.terraincorrector.util.TerrainTestUtil.*;
import static org.mockito.Mockito.*;

public class TerrainCorrectorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_LIQUID = createMockBlock(false, true, Material.WATER);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private static final BlockFace[] faces = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.DOWN,
            BlockFace.UP
    };
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 10, 10, 10);

    //    @Test
    //    public void correctTerrain_WhenDetectingRiver_ShouldReturnFalse() {
    //        //Arrange
    //        final World mockWorld = createDefaultWorld();
    //        final TerrainCorrector corrector = new TerrainCorrector();
    //        final Block facingLiquidBlock = createMockBlock(false, true, Material.WATER);
    ////        when(facingLiquidBlock.get)
    //        when(mockWorld.getBlockAt(5, 0, 10)).thenReturn(MOCK_BLOCK_LIQUID);
    //        for (BlockFace f : faces) {
    //            when(MOCK_BLOCK_LIQUID.getRelative(f)).thenReturn(MOCK_BLOCK_STONE);
    //        }
    //        when(MOCK_BLOCK_LIQUID.getRelative(BlockFace.SOUTH)).thenReturn(facingLiquidBlock);
    //
    //        //Act
    ////        boolean result = corrector.correctTerrain(boundingBox, mockWorld);
    //
    //        //Assert
    //        assertFalse(result);
    //    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        when(mockWorld.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        return mockWorld;
    }
}
