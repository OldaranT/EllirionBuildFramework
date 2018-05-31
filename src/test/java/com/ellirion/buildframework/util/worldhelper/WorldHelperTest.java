package com.ellirion.buildframework.util.worldhelper;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
public class WorldHelperTest {

    //    @Before
    //    public void setup() {
    //        mockStatic(BuildFramework.class);
    //        BuildFramework buildFramework = mock(BuildFramework.class);
    //        FileConfiguration config = mock(FileConfiguration.class);
    //
    //        when(BuildFramework.getInstance()).thenReturn(buildFramework);
    //        when(buildFramework.getConfig()).thenReturn(config);
    //        when(config.getInt(anyString(), anyInt())).thenReturn(1000);
    //    }
    //
    //    @Test
    //    public void getBlockAt_whenCalledOnUnloadedChunk_shouldLoadChunkAndReturnBlock() {
    //        // Arrange
    //        World world = mock(World.class);
    //        Block block = mock(Block.class);
    //
    //        int x = 8;
    //        int y = 8;
    //        int z = 8;
    //        int chunkX = 0;
    //        int chunkY = 0;
    //
    //        when(world.getBlockAt(x, y, z)).thenReturn(block);
    //        when(world.isChunkLoaded(chunkX, chunkY)).thenReturn(false);
    //        doNothing().when(world).loadChunk(chunkX, chunkY);
    //
    //        // Act
    //        Block result = WorldHelper.getBlockAt(world, x, y, z);
    //
    //        //Assert
    //        verify(world, times(1)).loadChunk(chunkX, chunkY);
    //        assertEquals(result, block);
    //    }
    //
    //    @Test
    //    public void getBlockAt_whenCalledOnLoadedChunk_shouldReturnBlockAndNotLoadChunk() {
    //        // Arrange
    //        World world = mock(World.class);
    //        Block block = mock(Block.class);
    //
    //        int x = 8;
    //        int y = 8;
    //        int z = 8;
    //        int chunkX = 0;
    //        int chunkY = 0;
    //
    //        when(world.getBlockAt(x, y, z)).thenReturn(block);
    //        when(world.isChunkLoaded(chunkX, chunkY)).thenReturn(true);
    //
    //        // Act
    //        Block result = WorldHelper.getBlockAt(world, x, y, z);
    //
    //        //Assert
    //        verify(world, times(0)).loadChunk(chunkX, chunkY);
    //        assertEquals(result, block);
    //    }
    //
    //    @Test
    //    public void queueBlockChange_whenInvoked_shouldScheduleAndExecuteBlockChange() {
    //        World world = mock(World.class);
    //        Block block = mock(Block.class);
    //        Material material = mock(Material.class);
    //        Location location = mock(Location.class);
    //        byte metaData = 0;
    //
    //        when(location.getWorld()).thenReturn(world);
    //        when(world.getBlockAt(location)).thenReturn(block);
    //        doNothing().when(block).setType(material);
    //        doNothing().when(block).setData(metaData);
    //
    //        BlockChange change = new BlockChange(material, metaData, location);
    //
    //        WorldHelper.queueBlockChange(change).await();
    //
    //        verify(block, times(1)).setType(material);
    //        verify(block, times(1)).setData(metaData);
    //    }
}
