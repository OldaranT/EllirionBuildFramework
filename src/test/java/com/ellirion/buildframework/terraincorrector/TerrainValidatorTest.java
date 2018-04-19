package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
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
        final World mockWorld = mock(World.class);
        final Block mockBlockAir = mock(Block.class);
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        assertEquals(0, t.validate(bb, mockWorld, 4));
    }

    //    @Test
//    public void CalculateBlocks_WhenContainsLiquid_ShouldReturnPositiveInfinity() {
//
//        final TerrainValidator t = new TerrainValidator();
//        final World mockWorld = mock(World.class);
//        final Block mockBlockAir = mock(Block.class);
//        final Block mockBlockLiquid = mock(Block.class);
//        final Block mockBlockNormal = mock(Block.class);
//        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);
//
//        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
//        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockLiquid);
//        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
//
//        when(mockBlockAir.isEmpty()).thenReturn(true);
//        when(mockBlockAir.isLiquid()).thenReturn(false);
//
//        when(mockBlockLiquid.isEmpty()).thenReturn(false);
//        when(mockBlockLiquid.isLiquid()).thenReturn(true);
//
//        when(mockBlockNormal.isEmpty()).thenReturn(false);
//        when(mockBlockNormal.isLiquid()).thenReturn(false);
//
//        assertEquals(false, t.validate(bb, mockWorld, 4));
//    }
//
//    @Test
//    public void CalculateBlocks_WhenHasThreeNormalBlocks_ShouldReturnThree() {
//        final TerrainValidator t = new TerrainValidator();
//        final World mockWorld = mock(World.class);
//        final Block mockBlockAir = mock(Block.class);
//        final Block mockBlockNormal = mock(Block.class);
//        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);
//
//        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
//        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
//
//        when(mockBlockAir.isEmpty()).thenReturn(true);
//        when(mockBlockAir.isLiquid()).thenReturn(false);
//
//        when(mockBlockNormal.isEmpty()).thenReturn(false);
//        when(mockBlockNormal.isLiquid()).thenReturn(false);
//
//
//        assertEquals(3, t.validate(bb, mockWorld, 4));
//    }
//
//    @Test
//    public void CalculateBlocks_WhenLiquidOneBlockOutsideBoundingBox_ShouldReturnPositiveInfinity() {
//        final TerrainValidator t = new TerrainValidator();
//        final World mockWorld = mock(World.class);
//        final Block mockBlockAir = mock(Block.class);
//        final Block mockBlockLiquid = mock(Block.class);
//        final Block mockBlockNormal = mock(Block.class);
//        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);
//
//        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
//        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(-1, 1, 0)).thenReturn(mockBlockLiquid);
//
//        when(mockBlockAir.isEmpty()).thenReturn(true);
//        when(mockBlockAir.isLiquid()).thenReturn(false);
//
//        when(mockBlockLiquid.isEmpty()).thenReturn(false);
//        when(mockBlockLiquid.isLiquid()).thenReturn(true);
//
//        when(mockBlockNormal.isEmpty()).thenReturn(false);
//        when(mockBlockNormal.isLiquid()).thenReturn(false);
//
//        assertEquals(Double.POSITIVE_INFINITY, t.validate(bb, mockWorld, 4));
//    }
//
//    @Test
//    public void CalculateBlocks_WhenThreeNormalBlocksOneBlockOutSideTheBoundingBox_ShouldReturnThree() {
//        final TerrainValidator t = new TerrainValidator();
//
//        final World mockWorld = mock(World.class);
//        final Block mockBlockAir = mock(Block.class);
//        final Block mockBlockNormal = mock(Block.class);
//        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);
//
//        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
//        when(mockWorld.getBlockAt(-1, 0, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(2, -1, 0)).thenReturn(mockBlockNormal);
//        when(mockWorld.getBlockAt(2, 2, 2)).thenReturn(mockBlockNormal);
//
//        when(mockBlockAir.isEmpty()).thenReturn(true);
//        when(mockBlockAir.isLiquid()).thenReturn(false);
//
//        when(mockBlockNormal.isEmpty()).thenReturn(false);
//        when(mockBlockNormal.isLiquid()).thenReturn(false);
//
//        assertEquals(3, t.validate(bb, mockWorld, 4));
//    }
//
    @Test
    public void CalculateBlocks_WhenBlocksAreOutsideValidationArea_ShouldReturnZero() {
        final TerrainValidator t = new TerrainValidator();

        final World mockWorld = mock(World.class);
        final Block mockBlockAir = mock(Block.class);
        final Block mockBlockNormal = mock(Block.class);
        final BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(-5, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(6, -1, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(2, 8, 2)).thenReturn(mockBlockNormal);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(0, t.validate(bb, mockWorld, 4));
    }
}
