package com.ellirion.buildframework.terraincorrector;

import org.bukkit.block.Block;
import org.junit.Before;
import org.junit.Test;
import com.ellirion.buildframework.terraincorrector.model.Hole;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

public class HoleTest {

    private Hole h;

    @Before
    public void setup() {
        h = new Hole();
    }

    @Test
    public void containsLiquid_whenDoesNotContainAnLiquidBlock_shouldReturnFalse() {
        // Arrange
        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        h.getBlockList().add(mockBlock1);
        h.getBlockList().add(mockBlock2);

        // Act
        boolean result = h.containsLiquid();

        //Assert
        assertFalse(result);
    }

    @Test
    public void containsLiquid_whenListContainsAnLiquidBlock_shouldReturnTrue() {
        // Arrange
        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        when(mockBlock2.isLiquid()).thenReturn(true);

        h.getBlockList().add(mockBlock1);
        h.getBlockList().add(mockBlock2);

        // Act
        boolean result = h.containsLiquid();

        // Assert
        assertTrue(result);
    }

    @Test
    public void add_whenGivenBlockNotInList_shouldAddBlockAndReturnTrue() {
        // Arrange
        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        h.add(mockBlock1);

        // Act
        boolean result = h.add(mockBlock2);

        // Assert
        assertTrue(result);
        assertTrue(h.getBlockList().contains(mockBlock1));
        assertTrue(h.getBlockList().contains(mockBlock2));
    }

    @Test
    public void add_whenGivenBlockIsInList_shouldReturnFalse() {
        // Arrange
        Block mockBlock1 = mock(Block.class);

        h.add(mockBlock1);

        // Act
        boolean result = h.add(mockBlock1);

        // Assert
        assertFalse(result);
        assertTrue(h.getBlockList().contains(mockBlock1));
    }

    @Test
    public void geTopBlocks_whenOneTopBlockIsGiven_shouldReturnTheTopBlock() {
        // Arrange
        List<Block> expected = new ArrayList<>();
        Block mockBlock = mock(Block.class);

        when(mockBlock.getY()).thenReturn(1);

        // Act
        h.add(mockBlock);
        expected.add(mockBlock);

        List<Block> result = h.getTopBlocks();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void geTopBlocks_whenMoreTopBlockAreGiven_shouldReturnTheTopBlocks() {
        // Arrange
        List<Block> expected = new ArrayList<>();

        Block mockBlock1 = mock(Block.class);
        Block mockBlock2 = mock(Block.class);

        when(mockBlock1.getY()).thenReturn(1);
        when(mockBlock2.getY()).thenReturn(1);
        expected.add(mockBlock1);
        expected.add(mockBlock2);

        // Act
        h.add(mockBlock1);
        h.add(mockBlock2);
        List<Block> result = h.getTopBlocks();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    public void geTopBlocks_whenMoreTopAndMoreBlocksBelowBlockAreGiven_shouldReturnTheTopBlocks() {
        // Arrange
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

        // Act
        h.add(mockBlock1);
        h.add(mockBlock2);
        h.add(mockBlock3);
        h.add(mockBlock4);

        List<Block> result = h.getTopBlocks();

        // Assert
        assertEquals(expected, result);
    }
}
