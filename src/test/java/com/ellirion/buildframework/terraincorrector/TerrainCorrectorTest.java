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
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.transact.SequenceTransaction;
import com.ellirion.buildframework.util.transact.Transaction;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        BuildFramework.class, Bukkit.class, WorldHelper.class, TerrainCorrector.class, TransactionManager.class
})
public class TerrainCorrectorTest {

    private static final Material stone = Material.STONE;
    private static final Material air = Material.AIR;
    private static final Player player = mock(Player.class);
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 3, 2, 3);
    private final BuildFramework mockPlugin = mock(BuildFramework.class);
    private final FileConfiguration mockConfig = mock(FileConfiguration.class);

    private World mockWorld;
    private TerrainCorrector corrector;
    private ArgumentCaptor<Transaction> captor;

    public TerrainCorrectorTest() {
        mockStatic(WorldHelper.class);
        mockStatic(BuildFramework.class);
        mockStatic(TransactionManager.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);

        when(mockConfig.getInt("TerrainCorrector.MaxHoleDepth", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrector.AreaLimitOffset", 5)).thenReturn(1);
        when(mockConfig.getInt("TerrainCorrector.HoleFillerChanceToChangeDepth", 10)).thenReturn(0);

        Transaction mockTransaction = mock(Transaction.class);
        when(WorldHelper.setBlock(any(Location.class), any(Material.class),
                                  anyByte())).thenReturn(mockTransaction);
        when(WorldHelper.setBlock(eq(mockWorld), anyInt(), anyInt(), anyInt(), any(),
                                  eq((byte) 0))).thenCallRealMethod();
        when(WorldHelper.getBlock(any(Location.class))).thenCallRealMethod();
    }

    @Before
    public void setup() throws Exception {

        corrector = new TerrainCorrector();
        mockWorld = createDefaultWorld();
        captor = ArgumentCaptor.forClass(Transaction.class);

        PowerMockito.doNothing().when(
                TransactionManager.class, "addDoneTransaction", any(Player.class), captor.capture());
    }

    @Test
    public void correctTerrain_whenHoleFacesEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange

        for (int y = 2; y >= -5; y--) {
            for (int x = 2; x <= 5; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act

        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenHoleFacesWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange

        for (int y = 2; y >= -5; y--) {
            for (int x = -1; x < 3; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenHoleFacesNorthAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange

        for (int y = 2; y >= -5; y--) {
            for (int x = 1; x <= 3; x++) {
                for (int z = -1; z < 3; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenHoleFacesSouthAndExceedsDepthAndExceedsAreaLimit_shouldBuildSupports() {
        // Arrange

        for (int y = 2; y >= -5; y--) {
            for (int x = 1; x <= 3; x++) {
                for (int z = 2; z <= 5; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesSouthEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange

        for (int y = 2; y >= -6; y--) {
            for (int z = 2; z <= 4; z++) {
                for (int x = 2; x <= 4; x++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(5, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesNorthEastAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange

        for (int y = 2; y >= -6; y--) {
            for (int z = -1; z <= 2; z++) {
                for (int x = 2; x <= 4; x++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(5, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesSouthWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange

        for (int y = 2; y >= -6; y--) {
            for (int z = 2; z <= 4; z++) {
                for (int x = -1; x <= 2; x++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(5, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenCornerHoleFacesNorthWestAndExceedsDepthAndExceedsAreaLimit_shouldBuildCornerSupports() {
        // Arrange

        for (int y = 2; y >= -6; y--) {
            for (int z = -1; z <= 2; z++) {
                for (int x = -1; x <= 2; x++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(5, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenOverRavineOnZAxisAndExceedsDepthAndExceedsAreaLimit_shouldBuildBridgeSupportsOnXAxis() {
        // Arrange
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 5, 2, 2);

        for (int y = 2; y >= -6; y--) {
            for (int x = 2; x <= 4; x++) {
                for (int z = 0; z <= 3; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenOverRavineOnXAxisAndExceedsDepthAndExceedsAreaLimit_shouldBuildBridgeSupportsOnZAxis() {
        // Arrange
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 2, 2, 5);

        for (int y = 2; y >= -6; y--) {
            for (int x = 0; x <= 3; x++) {
                for (int z = 2; z <= 4; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, air, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(6, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenNoHolesButHasBlocksInBoundingBox_shouldClearBlocksInBoundingBox() {
        // Arrange
        for (int y = 1; y <= 2; y++) {
            for (int x = 1; x <= 3; x++) {
                for (int z = 1; z <= 3; z++) {
                    setBlockAtCoordinatesHelper(x, y, z, stone, mockWorld);
                }
            }
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(18, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenHoleOnlyUnderBoundingBox_shouldSetTopBlocksToBarrier() {
        // Arrange
        setBlockAtCoordinatesHelper(2, 0, 2, air, mockWorld);

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(1, ((SequenceTransaction) captor.getValue()).getChildren().size());
    }

    @Test
    public void correctTerrain_whenHoleNotExceedsAreaLimit_shouldFillWithMostCommonMaterial() {
        // Arrange
        for (int x = 2; x >= 0; x--) {
            setBlockAtCoordinatesHelper(x, 0, 2, air, mockWorld);
        }

        // Act
        corrector.correctTerrain(boundingBox, mockWorld, player);

        // Assert

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
        assertEquals(3, ((SequenceTransaction) captor.getValue()).getChildren().size());
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
        for (int x = -1; x <= 7; x++) {
            for (int z = -1; z <= 7; z++) {
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
        //        when(world.getBlockAt(x, y, z)).thenReturn(mockBlock);
        when(WorldHelper.getBlock(world, x, y, z)).thenReturn(mockBlock);
        when(WorldHelper.getBlock(any(Location.class))).thenCallRealMethod();
    }
}
