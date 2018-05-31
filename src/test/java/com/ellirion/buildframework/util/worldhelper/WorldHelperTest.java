package com.ellirion.buildframework.util.worldhelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.util.WorldHelper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WorldHelper.class})
public class WorldHelperTest {

    @Test
    public void getBlock_whenCalledOnUnloadedChunk_shouldLoadChunkAndReturnBlock() {
        // Arrange
        World world = mock(World.class);
        Block block = mock(Block.class);

        int x = 8;
        int y = 8;
        int z = 8;
        int chunkX = 0;
        int chunkY = 0;

        when(world.getBlockAt(x, y, z)).thenReturn(block);
        when(world.isChunkLoaded(chunkX, chunkY)).thenReturn(false);
        doNothing().when(world).loadChunk(chunkX, chunkY);

        // Act
        Block result = WorldHelper.getBlock(world, x, y, z);

        //Assert
        verify(world, times(1)).loadChunk(chunkX, chunkY);
        assertEquals(result, block);
    }

    @Test
    public void getBlock_whenCalledOnLoadedChunk_shouldReturnBlockAndNotLoadChunk() {
        // Arrange
        World world = mock(World.class);
        Block block = mock(Block.class);

        int x = 8;
        int y = 8;
        int z = 8;
        int chunkX = 0;
        int chunkY = 0;

        when(world.getBlockAt(x, y, z)).thenReturn(block);
        when(world.isChunkLoaded(chunkX, chunkY)).thenReturn(true);

        // Act
        Block result = WorldHelper.getBlock(world, x, y, z);

        //Assert
        verify(world, times(0)).loadChunk(chunkX, chunkY);
        assertEquals(result, block);
    }

    @Test
    public void setBlock_whenInvoked_shouldScheduleAndExecuteBlockChange() {

        // Schedule the runner
        Thread t1 = new Thread(() -> {
            while (true) {
                WorldHelper.run();
            }
        });

        Block block = mock(Block.class);
        Material material = mock(Material.class);
        Location location = mock(Location.class);
        byte metaData = 0;

        PowerMockito.stub(PowerMockito.method(WorldHelper.class, "getBlock", Location.class)).toReturn(block);
        doNothing().when(block).setType(material);
        doNothing().when(block).setData(metaData);

        t1.start();

        verify(block, times(0)).setType(material);
        verify(block, times(0)).setData(metaData);

        WorldHelper.setBlock(location, material, metaData);

        try {
            Thread.sleep(100);
        } catch (Exception ex) {
            fail();
        }

        t1.interrupt();

        verify(block, times(1)).setType(material);
        verify(block, times(1)).setData(metaData);
    }
}
