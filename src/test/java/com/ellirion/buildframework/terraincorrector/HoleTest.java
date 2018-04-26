package com.ellirion.buildframework.terraincorrector;

import org.bukkit.block.Block;
import org.junit.Test;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

public class HoleTest {

    @Test
    public void containsLiquid_WhenDoesNotContainALiquidBlock_ThenReturnFalse() {
        Hole h = new Hole();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        h.getBlockList().add(mockBlock1);
        h.getBlockList().add(mockBlock2);

        boolean result = h.containsLiquid();

        assertFalse(result);
    }

    @Test
    public void containsLiquid_WhenListContainsAnLiquidBlock_ThenReturnTrue() {
        Hole h = new Hole();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        when(mockBlock2.isLiquid()).thenReturn(true);

        h.getBlockList().add(mockBlock1);
        h.getBlockList().add(mockBlock2);

        boolean result = h.containsLiquid();

        assertTrue(result);
    }

    @Test
    public void add_WhenGivenBlockNotInList_ThenAddBlockAndReturnTrue() {
        Hole h = new Hole();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        h.add(mockBlock1);
        boolean result = h.add(mockBlock2);

        assertTrue(result);
        assertTrue(h.getBlockList().contains(mockBlock1));
        assertTrue(h.getBlockList().contains(mockBlock2));
    }

    @Test
    public void add_WhenGivenBlockIsInList_ThenReturnFalse() {
        Hole h = new Hole();

        Block mockBlock1 = mock(Block.class);

        h.add(mockBlock1);
        boolean result = h.add(mockBlock1);

        assertFalse(result);
        assertTrue(h.getBlockList().contains(mockBlock1));
    }

    @Test
    public void geTopBlocks_WhenOneTopBlockIsGiven_ThenReturnTheTopBlock() {
        Hole h = new Hole();
        List<Block> expected = new ArrayList<>();
        Block mockBlock = mock(Block.class);

        when(mockBlock.getY()).thenReturn(1);

        h.add(mockBlock);
        expected.add(mockBlock);

        List<Block> result = h.getTopBlocks();

        assertEquals(expected, result);
    }

    @Test
    public void geTopBlocks_WhenMoreTopBlockAreGiven_ThenReturnTheTopBlocks() {
        List<Block> expected = new ArrayList<>();
        Hole h = new Hole();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        when(mockBlock1.getY()).thenReturn(1);
        when(mockBlock2.getY()).thenReturn(1);
        expected.add(mockBlock1);
        expected.add(mockBlock2);

        h.add(mockBlock1);
        h.add(mockBlock2);
        List<Block> result = h.getTopBlocks();

        assertEquals(expected, result);
    }

    @Test
    public void geTopBlocks_WhenMoreTopAndMoreBlocksBelowBlockAreGiven_ThenReturnTheTopBlocks() {
        Hole h = new Hole();
        List<Block> expected = new ArrayList<>();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);
        Block mockBlock3 = mock(Block.class);
        Block mockBlock4 = mock(Block.class);

        when(mockBlock1.getY()).thenReturn(1);
        when(mockBlock2.getY()).thenReturn(1);
        when(mockBlock3.getY()).thenReturn(0);
        when(mockBlock4.getY()).thenReturn(0);

        expected.add(mockBlock1);
        expected.add(mockBlock2);

        h.add(mockBlock1);
        h.add(mockBlock2);
        h.add(mockBlock3);
        h.add(mockBlock4);

        List<Block> result = h.getTopBlocks();

        assertEquals(expected, result);
    }
}
