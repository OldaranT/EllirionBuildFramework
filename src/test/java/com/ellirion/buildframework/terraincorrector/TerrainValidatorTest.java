package com.ellirion.buildframework.terraincorrector;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.model.TerrainValidatorModel;

import java.util.ArrayList;
import java.util.List;

import static com.ellirion.buildframework.terraincorrector.TerrainTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, TerrainManager.class})
public class TerrainValidatorTest {

    private static final Block MOCK_BLOCK_AIR = createMockBlock(true, false, Material.AIR);
    private static final Block MOCK_BLOCK_LIQUID = createMockBlock(false, true, Material.WATER);
    private static final Block MOCK_BLOCK_STONE = createMockBlock(false, false, Material.STONE);
    private final BoundingBox boundingBox = new BoundingBox(1, 1, 1, 10, 10, 10);
    private World world;
    private TerrainValidator validator;

    public TerrainValidatorTest() {
        mockStatic(BuildFramework.class);
        mockStatic(TerrainManager.class);
        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockPlugin.getBlockValueConfig()).thenReturn(mockConfig);

        when(TerrainManager.getBoundingBoxes()).thenReturn(new ArrayList<>());

        when(mockConfig.getInt(any(String.class), eq(5))).thenReturn(2);
        when(mockConfig.getInt("TerrainCorrector.OverheadLimit", 50)).thenReturn(10);
        when(mockConfig.getInt("TerrainCorrector.BlocksLimit", 100)).thenReturn(10);
        when(mockConfig.getInt("TerrainCorrector.TotalLimit", 200)).thenReturn(15);
        when(mockConfig.getInt("TerrainCorrector.Offset", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrector.BoundingBoxMinDist", 5)).thenReturn(5);

        when(mockConfig.getInt(MOCK_BLOCK_STONE.getType().toString(), 1)).thenReturn(1);
    }

    @Before
    public void setup() {
        validator = new TerrainValidator();
        world = createDefaultWorld();
    }

    @Test
    public void validate_whenFloorIsFilledAndAreaToCheckIsAir_shouldReturnTrue() {
        // Arrange
        setFloor(world);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorNotFilledAndAreaToCheckIsAir_shouldReturnFalse() {
        // Arrange happens in setup()

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorNotFilledAndAreaToCheckIsContainsBlocks_shouldReturnFalse() {
        // Arrange
        when(world.getBlockAt(anyInt(), eq(5), eq(5))).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorNotFilledAirIsBelowThresholdAndAreaToCheckIsAir_shouldReturnTrue() {
        // Arrange
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_AIR);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorNotFilledWaterIsBelowThresholdAndAreaToCheckIsAir_shouldReturnTrue() {
        // Arrange
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_LIQUID);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorAndAreaAndTotalBelowThreshold_shouldReturnTrue() {
        // Arrange
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 10, MOCK_BLOCK_AIR);

        when(world.getBlockAt(5, 5, 5)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorAndAreaBelowThresholdAndTotalAboveThreshold_shouldReturnFalse() {
        // Arrange
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_AIR);

        when(world.getBlockAt(5, 5, 3)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 4)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 5)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 7)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(anyInt(), eq(5), eq(8))).thenReturn(MOCK_BLOCK_STONE);
        for (int y = 1; y <= 10; y++) {
            for (int x = 1; x <= 10; x++) {
                when(world.getBlockAt(x, y, 1)).thenReturn(MOCK_BLOCK_STONE);
            }
        }

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFloorNotFilledIsAboveThresholdAndAreaToCheckIsAir_shouldReturnFalse() {
        // Arrange
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 12, MOCK_BLOCK_AIR);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFlooredAndAreaToCheckContainsLiquid_shouldReturnFalse() {
        // Arrange

        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);
        when(world.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFlooredAndHasThreeNormalBlocks_shouldReturnTrue() {
        // Arrange
        setFloor(world);

        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 7, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(0, 0, 5)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFlooredAndHasLiquidOneBlockOutsideBoundingBox_shouldReturnFalse() {
        // Arrange
        setFloor(world);

        when(world.getBlockAt(-1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenFlooredAndHasThreeNormalBlocksOneBlockOutSideTheBoundingBox_shouldReturnTrue() {
        // Arrange
        setFloor(world);

        when(world.getBlockAt(-1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(2, -1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(2, 2, 2)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenFlooredAndBlocksAreOutsideValidationArea_shouldReturnTrue() {
        // Arrange
        setFloor(world);
        when(world.getBlockAt(-6, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(6, -1, -6)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(2, 16, 2)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenCompletelyFilledWithBlocks_shouldReturnFalse() {
        // Arrange
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenOneBlockOverLimitAndFloored_shouldReturnFalse() {
        // Arrange
        setFloor(world);
        when(world.getBlockAt(anyInt(), eq(1), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(anyInt(), eq(2), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(anyInt(), eq(3), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(anyInt(), eq(4), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        for (int y = 1; y <= 10; y++) {
            for (int x = 1; x <= 10; x++) {
                when(world.getBlockAt(x, y, 2)).thenReturn(MOCK_BLOCK_STONE);
            }
        }
        for (int x = 1; x <= 7; x++) {
            when(world.getBlockAt(x, 1, 3)).thenReturn(MOCK_BLOCK_STONE);
        }

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenOneBlockUnderLimitAndFloored_shouldReturnTrue() {
        // Arrange
        setFloor(world);
        for (int x = 1; x < boundingBox.getX2(); x++) {
            when(world.getBlockAt(x, 1, 1)).thenReturn(MOCK_BLOCK_STONE);
        }

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenExactlyOnBlockLimitAndFloored_shouldReturnFalse() {
        // Arrange
        setFloor(world);

        when(world.getBlockAt(anyInt(), eq(1), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenOneBelowBlockLimitWithSporadicBlockPlacementAndFloored_shouldReturnTrue() {
        // Arrange
        setFloor(world);

        when(world.getBlockAt(-2, 2, 12)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(-3, 8, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 13, 10)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 1, 8)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(14, 13, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(-1, 8, 13)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(-4, 10, -2)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(14, 7, 9)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(13, 1, -4)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result.isSucceeded());
    }

    @Test
    public void validate_whenAnotherBoundingBoxWithinBoundingBox_shouldReturnFalse() {
        // Arrange
        setFloor(world);

        List<BoundingBox> boxList = new ArrayList<>();
        boxList.add(boundingBox);

        when(TerrainManager.getBoundingBoxes()).thenReturn(boxList);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenAnotherBoundingBoxWithinCheckingRadius_shouldReturnFalse() {
        // Arrange
        setFloor(world);

        List<BoundingBox> boxList = new ArrayList<>();
        boxList.add(new BoundingBox(new Point(11, 11, 11), new Point(12, 12, 12)));

        when(TerrainManager.getBoundingBoxes()).thenReturn(boxList);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    @Test
    public void validate_whenHasRiverUnderBoundingBox_shouldReturnFalse() {
        // Arrange
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 3, 3, 3);
        for (int y = 0; y >= -5; y--) {
            for (int x = 0; x <= 5; x++) {
                for (int z = 0; z <= 5; z++) {
                    setBlockAtCoordinates(world, x, y, z, Material.STONE);
                }
            }
        }

        setBlockAtCoordinates(world, 1, 0, 1, Material.WATER);
        setBlockAtCoordinates(world, 0, 0, 1, Material.WATER);

        // Act
        TerrainValidatorModel result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result.isSucceeded());
    }

    private void setFloor(World world) {
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
    }

    private void replaceFloorWithSpecifiedBlock(final World world, final BoundingBox boundingBox, final int amount,
                                                final Block block) {
        int x, z, v;
        for (int i = 0; i <= amount; i++) {
            v = i;

            z = v % boundingBox.getDepth();
            v -= z;

            x = v / boundingBox.getDepth();

            when(world.getBlockAt(boundingBox.getX1() + x, 0, boundingBox.getZ1() + z)).thenReturn(block);
        }
    }

    private World createDefaultWorld() {
        final World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        return world;
    }
}
