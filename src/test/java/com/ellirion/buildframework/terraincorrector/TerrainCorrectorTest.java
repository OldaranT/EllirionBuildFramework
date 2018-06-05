package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.util.WorldHelper;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class, TerrainCorrector.class, WorldHelper.class, Player.class})
public class TerrainCorrectorTest {

    private static final Material stone = Material.STONE;
    private static final Material air = Material.AIR;
    private static final Player player = mock(Player.class);
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 3, 2, 3);
    private final BuildFramework mockPlugin = mock(BuildFramework.class);
    private final FileConfiguration mockConfig = mock(FileConfiguration.class);
    //    private final WorldHelper mWH = Mockito.spy(WorldHelper.class);
    private final WorldHelper mockHelper = PowerMockito.mock(WorldHelper.class);
    //    private Transaction t;
    private World mockWorld;
    private TerrainCorrector corrector;

    /*TODO mockstatic worldhelper
     * TODO verify setType has been called certain amount of times
     * TODO fix problem where the thread with the Act is excecuted after the Assert
     * */

    public TerrainCorrectorTest() {
        mockStatic(BuildFramework.class);
        mockStatic(WorldHelper.class);
        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainCorrector.MaxHoleDepth", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrector.AreaLimitOffset", 5)).thenReturn(1);
    }

    @Before
    public void setup() {
        corrector = new TerrainCorrector();
        mockWorld = createDefaultWorld();
        //        //        t = mock(Transaction.class);
        //        when(setBlock(any(Location.class), any(Material.class), anyByte())).thenReturn(
        //                Mockito.spy(Transaction.class));
    }

    @Test
    public void correctTerrain_whenHoleFacesEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() throws InterruptedException {
        // Arrange
        int yDepth = 0;
        when(mockHelper.setBlock(any(Location.class), any(Material.class),
                                 anyByte())).thenReturn(null);
        //        PowerMockito.spy(WorldHelper.class);

        for (int y = 2; y >= -5; y--) {
            for (int x = 2; x <= 5; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        Thread.sleep(1000);
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        //        for (int depth = 0; depth < 2; depth++) {
        //            for (int x = 3 - depth; x >= 2; x--) {
        //                assertTrue(mockWorld.getBlockAt(x, yDepth - depth, 2).getType() == Material.FENCE);
        //            }
        //        }
        Thread.sleep(2000);

        verifyStatic(WorldHelper.class, times(400));
        mockHelper.setBlock(mockWorld, 1, 0, 1, Material.FENCE, (byte) 0);

        //        verify(mockHelper, times(20)).setBlock(any(), any(), any());
    }

    @Test
    public void correctTerrain_whenHoleFacesWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -5; y--) {
            for (int x = -1; x < 3; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 1 + depth; x <= 2; x++) {
                assertTrue(mockWorld.getBlockAt(x, yDepth - depth, 2).getType() == Material.FENCE);
            }
        }
    }

    @Test
    public void correctTerrain_whenHoleFacesNorthAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -5; y--) {
            for (int x = 1; x <= 3; x++) {
                for (int z = -1; z < 3; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int z = 1 + depth; z <= 2; z++) {
                assertTrue(mockWorld.getBlockAt(2, yDepth - depth, z).getType() == Material.FENCE);
            }
        }
    }

    @Test
    public void correctTerrain_whenHoleFacesSouthAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -5; y--) {
            for (int x = 1; x <= 3; x++) {
                for (int z = 2; z <= 5; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int z = 3 - depth; z >= 2; z--) {
                assertTrue(mockWorld.getBlockAt(2, yDepth - depth, z).getType() == Material.FENCE);
            }
        }
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesSouthEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -6; y--) {
            for (int z = 2; z <= 4; z++) {
                for (int x = 2; x <= 4; x++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 3 - depth; x >= 2; x--) {
                for (int z = 3 - depth; z >= 2; z--) {
                    assertTrue(mockWorld.getBlockAt(x, yDepth - depth, z).getType() == Material.FENCE);
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesNorthEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -6; y--) {
            for (int z = -1; z <= 2; z++) {
                for (int x = 2; x <= 4; x++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 3 - depth; x >= 2; x--) {
                for (int z = 1 + depth; z <= 2; z++) {
                    assertTrue(mockWorld.getBlockAt(x, yDepth - depth, z).getType() == Material.FENCE);
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesSouthWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -6; y--) {
            for (int z = 2; z <= 4; z++) {
                for (int x = -1; x <= 2; x++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 1 + depth; x <= 2; x++) {
                for (int z = 3 - depth; z >= 2; z--) {
                    assertTrue(mockWorld.getBlockAt(x, yDepth - depth, z).getType() == Material.FENCE);
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesNorthWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -6; y--) {
            for (int z = -1; z <= 2; z++) {
                for (int x = -1; x <= 2; x++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 1 + depth; x <= 2; x++) {
                for (int z = 1 + depth; z <= 2; z++) {
                    assertTrue(mockWorld.getBlockAt(x, yDepth - depth, z).getType() == Material.FENCE);
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenOverRavineOnZAxisAndExceedsDepthAndExceedsAreaLimit_shouldBuildBridgeSupportsOnXAxis() {
        // Arrange
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 5, 2, 2);
        int yDepth = 0;
        int centreX = 3;

        for (int y = 2; y >= -6; y--) {
            for (int x = 2; x <= 4; x++) {
                for (int z = 0; z <= 3; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int z = 1; z <= 2; z++) {
                if (Math.abs(z) % 2 == 0) {
                    assertEquals(Material.FENCE, mockWorld.getBlockAt(centreX + depth, yDepth - depth, z).getType());
                    assertEquals(Material.FENCE, mockWorld.getBlockAt(centreX - depth, yDepth - depth, z).getType());
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenOverRavineOnXAxisAndExceedsDepthAndExceedsAreaLimit_shouldBuildBridgeSupportsOnZAxis() {
        // Arrange
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 2, 2, 5);
        int yDepth = 0;
        int centreZ = 3;

        for (int y = 2; y >= -6; y--) {
            for (int x = 0; x <= 3; x++) {
                for (int z = 2; z <= 4; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 1; x <= 2; x++) {
                if (Math.abs(x) % 2 == 0) {
                    assertEquals(Material.FENCE, mockWorld.getBlockAt(x, yDepth - depth, centreZ + depth).getType());
                    assertEquals(Material.FENCE, mockWorld.getBlockAt(x, yDepth - depth, centreZ - depth).getType());
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenNoHolesButHasBlocksInBoundingBox_shouldClearBlocksInBoundingBox() {
        // Arrange
        // Happens in the setup.

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int y = 1; y <= 2; y++) {
            for (int x = 1; x <= 3; x++) {
                for (int z = 1; z <= 3; z++) {
                    assertEquals(air, mockWorld.getBlockAt(x, y, z).getType());
                }
            }
        }
    }

    @Test
    public void correctTerrain_whenHoleOnlyUnderBoundingBox_shouldSetTopBlocksToBarrier() {
        // Arrange
        setBlockAtCoordinates(mockWorld, 2, 0, 2, air);

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        assertEquals(Material.BARRIER, mockWorld.getBlockAt(2, 0, 2).getType());
    }

    @Test
    public void correctTerrain_whenHoleNotExceedsAreaLimit_shouldFillWithMostCommonMaterial() {
        // Arrange
        for (int x = 2; x >= 0; x--) {
            setBlockAtCoordinates(mockWorld, x, 0, 2, air);
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        for (int x = 2; x >= 0; x--) {
            assertEquals(stone, mockWorld.getBlockAt(x, 0, 2).getType());
        }
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        when(mockWorld.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        for (int x = -3; x <= 7; x++) {
            for (int z = -3; z <= 7; z++) {
                for (int y = -7; y <= 0; y++) {
                    setBlockAtCoordinatesHelper(x, y, z, stone, mockWorld);
                }
            }
        }
        for (int x = 1; x <= 3; x++) {
            for (int z = 1; z <= 3; z++) {
                for (int y = 1; y <= 2; y++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }
        return mockWorld;
    }

    private void setBlockAtCoordinatesHelper(final int x, final int y, final int z, Material mat, World world) {
        Block mockBlock;
        if (mat == Material.AIR) {
            mockBlock = createMockBlock(true, false, mat);
        } else if (mat == Material.WATER || mat == Material.LAVA) {
            mockBlock = createMockBlock(false, true, mat);
        } else {
            mockBlock = createMockBlock(false, false, mat);
        }
        setCoordinates(mockBlock, x, y, z);
        when(mockHelper.getBlock(any(), eq(x), eq(y), eq(z))).thenReturn(mockBlock);
        when(mockHelper.getBlock(any(Location.class))).thenCallRealMethod();
        when(world.getBlockAt(x, y, z)).thenReturn(mockBlock);
    }
}
