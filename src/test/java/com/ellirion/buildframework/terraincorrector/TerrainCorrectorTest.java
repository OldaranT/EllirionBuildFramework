package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@SuppressWarnings("Duplicates")
@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class})
public class TerrainCorrectorTest {

    private static final Material stone = Material.STONE;
    private static final Material air = Material.AIR;
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 3, 2, 3);
    private World mockWorld;
    private TerrainCorrector corrector;
    private final BuildFramework mockPlugin = mock(BuildFramework.class);
    private final FileConfiguration mockConfig = mock(FileConfiguration.class);

    public TerrainCorrectorTest() {
        mockScheduler();
        mockStatic(BuildFramework.class);
        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainCorrector.MaxHoleDepth", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrector.AreaLimitOffset", 5)).thenReturn(1);
    }

    // TODO turn this into a global util.
    private void mockScheduler() {
        BukkitScheduler sched = mock(BukkitScheduler.class);

        mockStatic(Bukkit.class);

        //        when(BuildFramework.getInstance()).thenReturn(null);
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
        corrector = new TerrainCorrector();
        mockWorld = createDefaultWorld();
    }

    @Test
    public void correctTerrain_whenHoleFacesEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange
        int yDepth = 0;

        for (int y = 2; y >= -5; y--) {
            for (int x = 2; x <= 5; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, air);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld);

        // Assert
        for (int depth = 0; depth < 2; depth++) {
            for (int x = 3 - depth; x >= 2; x--) {
                assertTrue(mockWorld.getBlockAt(x, yDepth - depth, 2).getType() == Material.FENCE);
            }
        }
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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

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
        corrector.correctTerrain(boundingBox, mockWorld);

        // Assert
        for (int x = 2; x >= 0; x--) {
            assertEquals(stone, mockWorld.getBlockAt(x, 0, 2).getType());
        }
    }

    private World createDefaultWorld() {
        final World mockWorld = mock(World.class);
        for (int x = -3; x <= 7; x++) {
            for (int z = -3; z <= 7; z++) {
                for (int y = -7; y <= 3; y++) {
                    setBlockAtCoordinates(mockWorld, x, y, z, stone);
                }
            }
        }
        return mockWorld;
    }
}
