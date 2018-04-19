package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
public class TerrainValidatorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_LIQUID = createMockBlock(false, true, Material.WATER);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private static final World MOCK_WORLD = createDefaultWorld();


    private static Block createMockBlock(boolean isAir, boolean isLiquid, Material material) {
        final Block mockBlock = mock(Block.class);
        when(mockBlock.isEmpty()).thenReturn(isAir);
        when(mockBlock.isLiquid()).thenReturn(isLiquid);

        when(mockBlock.getType()).thenReturn(material);

        return mockBlock;
    }

    private static World createDefaultWorld() {
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


        when(mockConfig.getInt("test")).thenReturn(1);
        when(mockLogger.isLoggable(Level.INFO)).thenReturn(true);

    }


    @Test
    public void CalculateBlocks_WhenOnlyContainsAir_ShouldReturnZero() {
        final TerrainValidator t = new TerrainValidator();
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        System.out.println("material stone:");
        System.out.println(Material.STONE.toString());

        assertEquals(0, t.validate(bb, MOCK_WORLD));
    }

    @Test
    public void CalculateBlocks_WhenContainsLiquid_ShouldReturnPositiveInfinity() {

        final TerrainValidator t = new TerrainValidator();
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);
        when(MOCK_WORLD.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        assertEquals(false, t.validate(bb, MOCK_WORLD));
    }

    @Test
    public void CalculateBlocks_WhenHasThreeNormalBlocks_ShouldReturnThree() {
        final TerrainValidator t = new TerrainValidator();
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        assertEquals(3, t.validate(bb, MOCK_WORLD));
    }

    @Test
    public void CalculateBlocks_WhenLiquidOneBlockOutsideBoundingBox_ShouldReturnPositiveInfinity() {
        final TerrainValidator t = new TerrainValidator();
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(-1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);

        assertEquals(Double.POSITIVE_INFINITY, t.validate(bb, MOCK_WORLD));
    }

    @Test
    public void CalculateBlocks_WhenThreeNormalBlocksOneBlockOutSideTheBoundingBox_ShouldReturnThree() {
        final TerrainValidator t = new TerrainValidator();

        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(-1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(2, -1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(2, 2, 2)).thenReturn(MOCK_BLOCK_STONE);

        assertEquals(3, t.validate(bb, MOCK_WORLD));
    }

    @Test
    public void CalculateBlocks_WhenBlocksAreOutsideValidationArea_ShouldReturnZero() {
        final TerrainValidator t = new TerrainValidator();
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(MOCK_WORLD.getBlockAt(-5, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(6, -1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(MOCK_WORLD.getBlockAt(2, 8, 2)).thenReturn(MOCK_BLOCK_STONE);

        assertEquals(0, t.validate(bb, MOCK_WORLD));
    }

}
