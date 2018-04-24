package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
public class TerrainValidatorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_LIQUID = createMockBlock(false, true, Material.WATER);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private static final BoundingBox BOUNDINGBOX = new BoundingBox(1, 1, 1, 10, 10, 10);

    private static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = mock(Block.class);

        when(mockBlock.isEmpty()).thenReturn(isEmpty);
        when(mockBlock.isLiquid()).thenReturn(isLiquid);
        when(mockBlock.getType()).thenReturn(material);

        return mockBlock;
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        return mockWorld;
    }

    private void setFloor(World mockWorld) {
        when(mockWorld.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
    }

    @Before
    public void setup() {
        mockStatic(BuildFramework.class);
        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);
        final Logger mockLogger = mock(Logger.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockPlugin.getLogger()).thenReturn(mockLogger);
        when(mockPlugin.getBlockValueConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainValidation_OverheadLimit", 50)).thenReturn(10);
        when(mockConfig.getInt("TerrainValidation_BocksLimit", 100)).thenReturn(10);
        when(mockConfig.getInt("TerrainValidation_TotalLimit", 200)).thenReturn(100);
        when(mockConfig.getInt("TerrainValidation_Offset", 5)).thenReturn(5);

        when(mockConfig.getInt(MOCK_BLOCK_STONE.getType().toString(), 1)).thenReturn(1);

        when(mockLogger.isLoggable(Level.INFO)).thenReturn(true);
    }

    @Test
    public void Validate_WhenFloorIsFilledAndAreaToCheckIsAir_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator validator = new TerrainValidator();
        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);

        //ACT

        final boolean result = validator.validate(BOUNDINGBOX, mockWorld);

        //ASSERT

        assertEquals(true, result);
    }

    //    @Test
    //    public void Validate_WhenFloorMissingIsBelowThresholdAndAreaToCheckISAir_ShouldReturnTrue() {
    //        //ARRANGE
    //        final TerrainValidator validator = new TerrainValidator();
    //
    //        setFloor(false);
    //
    //
    //        //ACT
    //
    //        boolean result = validator.validate(BOUNDINGBOX, mockWorld);
    //        //ASSERT
    //        assertEquals(true, result);
    //
    //    }

    @Test
    public void Validate_WhenFlooredAndAreaToCheckContainsLiquid_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        //ACT

        final boolean result = t.validate(BOUNDINGBOX, mockWorld);
        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasThreeNormalBlocks_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        when(mockWorld.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 7, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(0, 0, 5)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        final boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasLiquidOneBlockOutsideBoundingBox_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasThreeNormalBlocksOneBlockOutSideTheBoundingBox_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, -1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, 2, 2)).thenReturn(MOCK_BLOCK_STONE);
        //ACT

        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFlooredAndBlocksAreOutsideValidationArea_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-6, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(6, -1, -6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, 16, 2)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertTrue(result);
    }

    @Test
    public void Validate_WhenCompletelyFilledWithBlocks_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBlockOverLimitAndFloored_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(anyInt(), eq(1), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 2)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBlockUnderLimitAndFloored_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        for (int x = 1; x < BOUNDINGBOX.getX2(); x++) {
            when(mockWorld.getBlockAt(x, 1, 1)).thenReturn(MOCK_BLOCK_STONE);
        }
        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertTrue(result);
    }

    @Test
    public void Validate_WhenExactlyOnBlockLimitAndFloored_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(anyInt(), eq(1), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenExactlyOnBlockLimitWithSporadicBlockPlacementAndFloored_shouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-2, 2, 12)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-3, 8, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 13, 10)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 8)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 13, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-1, 8, 13)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-4, 10, -2)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 7, 9)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(13, 1, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(10, 14, -4)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBelowBlockLimitWithSporadicBlockPlacementAndFloored_shouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-2, 2, 12)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-3, 8, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 13, 10)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 8)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 13, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-1, 8, 13)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-4, 10, -2)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 7, 9)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(13, 1, -4)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        boolean result = t.validate(BOUNDINGBOX, mockWorld);

        //ASSERT
        assertTrue(result);
    }
}
