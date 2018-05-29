package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class})
public class TerrainCorrectorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 3, 2, 3);

    public TerrainCorrectorTest() {
        mockScheduler();
    }

    // TODO turn this into a global util.
    private void mockScheduler() {
        BukkitScheduler sched = mock(BukkitScheduler.class);

        mockStatic(BuildFramework.class);
        mockStatic(Bukkit.class);

        when(BuildFramework.getInstance()).thenReturn(null);
        when(Bukkit.getScheduler()).thenReturn(sched);
        when(sched.runTask(anyObject(), Mockito.any(Runnable.class))).thenAnswer((inv) -> {
            Runnable r = (Runnable) inv.getArguments()[1];
            r.run();
            return null;
        });
        when(sched.runTaskAsynchronously(anyObject(), Mockito.any(Runnable.class))).thenAnswer((inv) -> {
            Runnable r = (Runnable) inv.getArguments()[1];
            r.run();
            return null;
        });
    }

    @Before
    public void setup() {
        mockStatic(BuildFramework.class);

        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainCorrecter.MaxHoleDepth", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrecter.AreaLimitOffset", 5)).thenReturn(1);
    }

    @Test
    public void correctTerrain_whenHoleFacesSouthAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange
        Material stone = Material.STONE;
        Material air = Material.AIR;
        World mockWorld = createDefaultWorld();
        TerrainCorrector corrector = new TerrainCorrector();
        int yDepth = 0;

        for (int y = 2; y >= -5; y--) {
            for (int x = -1; x <= 4; x++) {
                setBlockAtCoordinates(mockWorld, x, y, 0, stone);
                setBlockAtCoordinates(mockWorld, x, y, 3, stone);
            }
            for (int z = -1; z <= 4; z++) {
                setBlockAtCoordinates(mockWorld, 0, y, z, stone);
                setBlockAtCoordinates(mockWorld, 4, y, z, air);
                setBlockAtCoordinates(mockWorld, 5, y, z, air);
            }
            setBlockAtCoordinates(mockWorld, 1, y, 1, stone);
            setBlockAtCoordinates(mockWorld, 1, y, 2, stone);
            setBlockAtCoordinates(mockWorld, 1, y, 3, stone);
            setBlockAtCoordinates(mockWorld, 2, y, 1, air);
            setBlockAtCoordinates(mockWorld, 2, y, 2, air);
            setBlockAtCoordinates(mockWorld, 2, y, 3, air);
            setBlockAtCoordinates(mockWorld, 3, y, 1, air);
            setBlockAtCoordinates(mockWorld, 3, y, 2, air);
            setBlockAtCoordinates(mockWorld, 3, y, 3, air);
        }

        for (int x = -1; x <= 5; x++) {
            for (int z = -1; z <= 5; z++) {
                setBlockAtCoordinates(mockWorld, x, -6, z, stone);
            }
        }
        //Act
        corrector.correctTerrain(boundingBox, mockWorld);
        //Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 3 - depth; x >= 2; x--) {
                assertTrue(mockWorld.getBlockAt(x, yDepth - depth, 2).getType() == Material.FENCE);
            }
        }
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        for (int i = 0; i > -6; i--) {
            when(mockWorld.getBlockAt(anyInt(), eq(i), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        }
        return mockWorld;
    }

    private void setCoordinates(Block mockBlock, final int x, final int y, final int z) {
        when(mockBlock.getX()).thenReturn(x);
        when(mockBlock.getY()).thenReturn(y);
        when(mockBlock.getZ()).thenReturn(z);
    }

    private void setBlockAtCoordinates(World world, final int x, final int y, final int z, Material mat) {
        Block mockBlock;
        if (mat == Material.AIR) {
            mockBlock = createMockBlock(true, false, mat);
        } else if (mat == Material.WATER) {
            mockBlock = createMockBlock(false, true, mat);
        } else {
            mockBlock = createMockBlock(false, false, mat);
        }
        setCoordinates(mockBlock, x, y, z);
        doAnswer((Answer) invocation -> {
            Material material = invocation.getArgument(0);
            Block b = (Block) invocation.getMock();
            when(b.getType()).thenReturn(material);
            return null;
        }).when(mockBlock).setType(any(Material.class));
        when(world.getBlockAt(x, y, z)).thenReturn(mockBlock);
    }
}
