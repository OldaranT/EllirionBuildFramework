package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.model.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TerrainValidatorTest {


    @Test
    public void CalculateBlock_WhenOnlyContainsAir_ShouldReturnZero() {
        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(0, 0, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 1, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 1)).thenReturn(mockBlockAir);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        assertEquals(t.validate(bb, mockWorld), 0);
    }

    @Test
    public void CalculateBlock_WhenContainsLiquid_ShouldReturnPositiveInfinity() {
        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockLiquid = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(0, 0, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockLiquid);
        when(mockWorld.getBlockAt(1, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(0, 1, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 1)).thenReturn(mockBlockAir);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockLiquid.isEmpty()).thenReturn(false);
        when(mockBlockLiquid.isLiquid()).thenReturn(true);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(t.validate(bb, mockWorld), Double.POSITIVE_INFINITY);
    }

    @Test
    public void CalculateBlock_WhenHasThreeNormalBlocks_ShouldReturnThree() {
        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(0, 0, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(1, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 1, 1)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(0, 1, 0)).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 1)).thenReturn(mockBlockAir);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);
        
        assertEquals(t.validate(bb, mockWorld), 3);
    }
}
