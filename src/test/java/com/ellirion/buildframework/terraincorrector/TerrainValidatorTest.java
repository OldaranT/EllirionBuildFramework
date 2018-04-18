package com.ellirion.buildframework.terraincorrector;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class})
public class TerrainValidatorTest {

    @Mock
    private Logger logger;
//
//    private BuildFramework spyFramework;
//
//    @Before
//    public void setup() {
//        spyFramework = spy(new BuildFramework());
//        when(spyFramework.getLogger()).thenReturn(logger);
//    }

    @Test
    public void CalculateBlocks_WhenOnlyContainsAir_ShouldReturnZero() throws Exception {
        PowerMockito.mockStatic(BuildFramework.class);
        System.out.println("got here 2");
//        PowerMockito.;
        System.out.println("past when then return logger");

//        PowerMockito.spy(new TerrainValidator());
//        given(TerrainValidator.getLogger()).willReturn(logger);
//        suppressConstructor(BuildFramework.class);
//        mockStatic(BuildFramework.class);
//        BuildFramework mockFramework = mock(BuildFramework.class);

        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

//        when(mockFramework.getInstance().getLogger()).thenReturn((Logger) LoggerFactory.getLogger(t.getClass()));

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        assertEquals(0, t.validate(bb, mockWorld, 4));
    }

    @Test
    public void CalculateBlocks_WhenContainsLiquid_ShouldReturnPositiveInfinity() {

        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockLiquid = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockLiquid);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockLiquid.isEmpty()).thenReturn(false);
        when(mockBlockLiquid.isLiquid()).thenReturn(true);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(Double.POSITIVE_INFINITY, t.validate(bb, mockWorld, 4));
    }

    @Test
    public void CalculateBlocks_WhenHasThreeNormalBlocks_ShouldReturnThree() {
        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(1, 1, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);


        assertEquals(3, t.validate(bb, mockWorld, 4));
    }

    @Test
    public void CalculateBlocks_WhenLiquidOneBlockOutsideBoundingBox_ShouldReturnPositiveInfinity() {
        TerrainValidator t = new TerrainValidator();
        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockLiquid = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(0, 0, 1)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(-1, 1, 0)).thenReturn(mockBlockLiquid);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockLiquid.isEmpty()).thenReturn(false);
        when(mockBlockLiquid.isLiquid()).thenReturn(true);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(Double.POSITIVE_INFINITY, t.validate(bb, mockWorld, 4));
    }

    @Test
    public void CalculateBlocks_WhenThreeNormalBlocksOneBlockOutSideTheBoundingBox_ShouldReturnThree() {
        TerrainValidator t = new TerrainValidator();

        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(-1, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(2, -1, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(2, 2, 2)).thenReturn(mockBlockNormal);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(3, t.validate(bb, mockWorld, 4));
    }

    @Test
    public void CalculateBlocks_WhenBlocksAreOutsideValidationArea_ShouldReturnZero() {
        TerrainValidator t = new TerrainValidator();

        World mockWorld = mock(World.class);
        Block mockBlockAir = mock(Block.class);
        Block mockBlockNormal = mock(Block.class);
        BoundingBox bb = new BoundingBox(0, 0, 0, 1, 1, 1);

        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockAir);
        when(mockWorld.getBlockAt(-5, 0, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(6, -1, 0)).thenReturn(mockBlockNormal);
        when(mockWorld.getBlockAt(2, 8, 2)).thenReturn(mockBlockNormal);

        when(mockBlockAir.isEmpty()).thenReturn(true);
        when(mockBlockAir.isLiquid()).thenReturn(false);

        when(mockBlockNormal.isEmpty()).thenReturn(false);
        when(mockBlockNormal.isLiquid()).thenReturn(false);

        assertEquals(0, t.validate(bb, mockWorld, 4));
    }
}
