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

import java.util.ArrayList;
import java.util.List;

import static com.ellirion.buildframework.terraincorrector.util.TerrainTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
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

    private static void setFloor(World world) {
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
    }

    @Before
    public void setup() {
        mockStatic(BuildFramework.class);
        mockStatic(TerrainManager.class);
        final BuildFramework mockPlugin = mock(BuildFramework.class);
        final FileConfiguration mockConfig = mock(FileConfiguration.class);

        when(BuildFramework.getInstance()).thenReturn(mockPlugin);

        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        when(mockPlugin.getBlockValueConfig()).thenReturn(mockConfig);

        when(TerrainManager.getBoundingBoxes()).thenReturn(new ArrayList<>());

        when(mockConfig.getInt("TerrainCorrector.OverheadLimit", 50)).thenReturn(10);
        when(mockConfig.getInt("TerrainCorrector.BlocksLimit", 100)).thenReturn(10);
        when(mockConfig.getInt("TerrainCorrector.TotalLimit", 200)).thenReturn(15);
        when(mockConfig.getInt("TerrainCorrector.Offset", 5)).thenReturn(5);
        when(mockConfig.getInt("TerrainCorrector.BoundingBoxMinDist", 5)).thenReturn(5);

        when(mockConfig.getInt(MOCK_BLOCK_STONE.getType().toString(), 1)).thenReturn(1);
    }

    @Test
    public void Validate_WhenFloorIsFilledAndAreaToCheckIsAir_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledAndAreaToCheckIsAir_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledAndAreaToCheckIsContainsBlocks_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();

        when(world.getBlockAt(anyInt(), eq(5), eq(5))).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledAirIsBelowThresholdAndAreaToCheckIsAir_ShouldReturnTrue() {
        // Arragne
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_AIR);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledWaterIsBelowThresholdAndAreaToCheckIsAir_ShouldReturnTrue() {
        // Arragne
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_LIQUID);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorAndAreaAndTotalBelowThreshold_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 10, MOCK_BLOCK_AIR);

        when(world.getBlockAt(5, 5, 5)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFloorAndAreaBelowThresholdAndTotalAboveThreshold_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 11, MOCK_BLOCK_AIR);

        when(world.getBlockAt(5, 5, 3)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 4)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 5)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 7)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(5, 5, 8)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFloorNotFilledIsAboveThresholdAndAreaToCheckIsAir_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);
        replaceFloorWithSpecifiedBlock(world, boundingBox, 12, MOCK_BLOCK_AIR);

        // Act
        final boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndAreaToCheckContainsLiquid_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        final World world = createDefaultWorld();

        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        when(world.getBlockAt(anyInt(), eq(0), anyInt())).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);
        when(world.getBlockAt(0, 0, 1)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final boolean result = t.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasThreeNormalBlocks_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        final World world = createDefaultWorld();
        setFloor(world);

        when(world.getBlockAt(1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(1, 7, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(world.getBlockAt(0, 0, 5)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        final boolean result = t.validate(boundingBox, world);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasLiquidOneBlockOutsideBoundingBox_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-1, 1, 0)).thenReturn(MOCK_BLOCK_LIQUID);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenFlooredAndHasThreeNormalBlocksOneBlockOutSideTheBoundingBox_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-1, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, -1, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, 2, 2)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenFlooredAndBlocksAreOutsideValidationArea_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-6, 0, 0)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(6, -1, -6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(2, 16, 2)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenCompletelyFilledWithBlocks_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBlockOverLimitAndFloored_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(anyInt(), eq(1), eq(1))).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 2)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBlockUnderLimitAndFloored_ShouldReturnTrue() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        for (int x = 1; x < boundingBox.getX2(); x++) {
            when(mockWorld.getBlockAt(x, 1, 1)).thenReturn(MOCK_BLOCK_STONE);
        }

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenExactlyOnBlockLimitAndFloored_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(anyInt(), eq(1), anyInt())).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenExactlyOnBlockLimitWithSporadicBlockPlacementAndFloored_shouldReturnFalse() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-2, 2, 12)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-3, 8, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 13, 10)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 8)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 13, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-1, 8, 13)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-4, 10, -2)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 7, 9)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(13, 1, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(10, 14, -4)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenOneBelowBlockLimitWithSporadicBlockPlacementAndFloored_shouldReturnTrue() {
        // Arrange
        final TerrainValidator t = new TerrainValidator();
        World mockWorld = createDefaultWorld();
        setFloor(mockWorld);

        when(mockWorld.getBlockAt(-2, 2, 12)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-3, 8, 6)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 13, 10)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(1, 1, 8)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 13, -4)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-1, 8, 13)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(-4, 10, -2)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(14, 7, 9)).thenReturn(MOCK_BLOCK_STONE);
        when(mockWorld.getBlockAt(13, 1, -4)).thenReturn(MOCK_BLOCK_STONE);

        // Act
        boolean result = t.validate(boundingBox, mockWorld);

        // Assert
        assertTrue(result);
    }

    @Test
    public void Validate_WhenAnotherBoundingBoxWithinBoundingBox_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        World world = createDefaultWorld();
        setFloor(world);

        List<BoundingBox> boxList = new ArrayList<>();
        boxList.add(boundingBox);

        when(TerrainManager.getBoundingBoxes()).thenReturn(boxList);

        // Act
        boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
    }

    @Test
    public void Validate_WhenAnotherBoundingBoxWithinCheckingRadius_ShouldReturnFalse() {
        // Arrange
        final TerrainValidator validator = new TerrainValidator();
        World world = createDefaultWorld();
        setFloor(world);

        List<BoundingBox> boxList = new ArrayList<>();
        boxList.add(new BoundingBox(new Point(11, 11, 11), new Point(12, 12, 12)));

        when(TerrainManager.getBoundingBoxes()).thenReturn(boxList);

        // Act
        boolean result = validator.validate(boundingBox, world);

        // Assert
        assertFalse(result);
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
        final World mockWorld = mock(World.class);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(MOCK_BLOCK_AIR);
        return mockWorld;
    }
}
