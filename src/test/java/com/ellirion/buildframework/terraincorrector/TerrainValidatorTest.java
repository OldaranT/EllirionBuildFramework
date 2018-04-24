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
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 10, 10, 10);

    private static Block createMockBlock(final boolean isEmpty, final boolean isLiquid, final Material material) {
        final Block mockBlock = mock(Block.class);

        when(mockBlock.isEmpty()).thenReturn(isEmpty);
        when(mockBlock.isLiquid()).thenReturn(isLiquid);
        when(mockBlock.getType()).thenReturn(material);

        return mockBlock;
    }

    private static void setFloor(World world) {
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);

        return mockWorld;
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
        when(mockConfig.getInt("TerrainValidation_BlocksLimit", 100)).thenReturn(10);
        when(mockConfig.getInt("TerrainValidation_TotalLimit", 200)).thenReturn(15);
        when(mockConfig.getInt("TerrainValidation_Offset", 5)).thenReturn(5);

        when(mockConfig.getInt(MOCK_BLOCK_STONE.getType().toString(), 1)).thenReturn(1);

        when(mockLogger.isLoggable(Level.INFO)).thenReturn(true);
    }

    @Test
    public void Validate_WhenFloorIsFilledAndAreaToCheckIsAir_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);

        //ACT

        final boolean result = validator.validate(boundingBox, world);

        //ASSERT

        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledAndAreaToCheckIsAir_ShouldReturnFalse() {
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();

        //ACT

        final boolean result = validator.validate(boundingBox, world);

        //ASSERT

        assertFalse(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledAndAreaToCheckIsContainsBlocks_ShouldReturnFalse() {
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        when(world.getBlockAt(anyInt(), eq(5), eq(5))).thenReturn(MOCK_BLOCK_STONE);

        //ACT

        final boolean result = validator.validate(boundingBox, world);

        //ASSERT

        assertFalse(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledIsBelowThresholdAndAreaToCheckIsAir_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator validator = new TerrainValidator();

        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_AIR);

        //ACT

        final boolean result = validator.validate(boundingBox, world);
        //ASSERT

        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorAndAreaAndTotalBelowThreshold_ShouldReturnFalse() {
        final TerrainValidator validator = new TerrainValidator();

        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 10, MOCK_BLOCK_AIR);
        when(world.getBlockAt(eq(5), eq(5), eq(5))).thenReturn(MOCK_BLOCK_STONE);

        // this one is not done yet 
        //ACT

        final boolean result = validator.validate(boundingBox, world);
        //ASSERT

        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledIsAboveThresholdAndAreaToCheckIsAir_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 12, MOCK_BLOCK_AIR);

        //ACT

        final boolean result = validator.validate(boundingBox, world);

        //ASSERT

        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndAreaToCheckContainsLiquid_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        final World world = createDefaultWorld();
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);
        when(world.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        //ACT

        final boolean result = t.validate(boundingBox, world);

        //ASSERT
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasThreeNormalBlocks_ShouldReturnTrue() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 7, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(0, 0, 5)).thenReturn(MOCK_BLOCK_STONE);

        //ACT
        final boolean result = t.validate(boundingBox, world);

        //ASSERT
        assertEquals(true, result);
    }

    @Test
    public void Validate_WhenFlooredAndHasLiquidOneBlockOutsideBoundingBox_ShouldReturnFalse() {
        //ARRANGE
        final TerrainValidator t = new TerrainValidator();

        World mockWorld = createDefaultWorld();

        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);

        //ACT
        boolean result = t.validate(boundingBox, mockWorld);

        //ASSERT
        assertEquals(false, result);
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

        boolean result = t.validate(boundingBox, mockWorld);

        //ASSERT
        assertEquals(true, result);
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
        boolean result = t.validate(boundingBox, mockWorld);

        //ASSERT
        assertEquals(true, result);
    }

    private void replaceFloorWithSpecifiedBlock(final World world, final BoundingBox boundingBox, final int amount,
                                                final Block block) {
        int x, z, v;
        int[] a;
        for (int i = 0; i <= amount; i++) {
            v = i;

            z = v % boundingBox.getDepth();
            v -= z;

            x = v / boundingBox.getDepth();

            when(world.getBlockAt(boundingBox.getX1() + x, 0, boundingBox.getZ1() + z)).thenReturn(block);
        }
    }
}
