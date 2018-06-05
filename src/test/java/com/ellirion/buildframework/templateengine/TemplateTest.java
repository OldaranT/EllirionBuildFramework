package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.util.MockHelper;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.transact.SequenceTransaction;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(Enclosed.class)
public class TemplateTest {

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class MovementTest {

        @Before
        public void setup() {
            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);
        }

        private Template createTemplate() {
            Template template = new Template();
            template.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
            for (int x = 0; x < 1; x++) {
                for (int y = 0; y < 1; y++) {
                    for (int z = 0; z < 1; z++) {
                        TemplateBlock block;
                        block = new TemplateBlock(Material.STONE);
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }

        private TemplateHologram createTemplateHologram(Template template, Location location) {
            return new TemplateHologram(template, location);
        }

        @Test
        public void moveHologram_whenMoveHologramIsCalled_shouldUpdateLocation() {
            World w = MockHelper.createDefaultWorld();

            // Arrange
            for (BlockFace bf : BlockFace.values()) {
                int x = 0;
                int y = 0;
                int z = 0;

                switch (bf) {
                    case UP:
                        y++;
                        break;
                    case DOWN:
                        y--;
                        break;
                    case EAST:
                        x++;
                        break;
                    case WEST:
                        x--;
                        break;
                    case NORTH:
                        z--;
                        break;
                    case SOUTH:
                        z++;
                        break;
                    default:
                        break;
                }

                Location toCheckLocation = new Location(w, 0, 0, 0);
                Location resultLocation = new Location(w, x, y, z);

                Template template = createTemplate();

                TemplateHologram toCheckHologram = createTemplateHologram(template, toCheckLocation);
                TemplateHologram resultHologram = createTemplateHologram(template, resultLocation);

                // Act
                toCheckHologram.moveHologram(1, bf);

                // Assert
                Assert.assertEquals(resultHologram, toCheckHologram);
            }
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class RotationTests {

        @Before
        public void setup() {
            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);
        }

        private Template createRotateTemplate(int xLength, int zLength) {
            Template template = new Template();
            template.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[xLength][1][zLength];
            for (int x = 0; x < xLength; x++) {
                for (int y = 0; y < 1; y++) {
                    for (int z = 0; z < zLength; z++) {
                        TemplateBlock block;
                        if (z < 1) {
                            block = new TemplateBlock(Material.STONE);
                        } else {
                            block = new TemplateBlock(Material.DIAMOND_BLOCK);
                        }
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }

        private Template createRotateClockwiseTemplate(int xLength, int zLength) {
            Template template = new Template();
            template.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[zLength][1][xLength];
            for (int x = 0; x < zLength; x++) {
                for (int y = 0; y < 1; y++) {
                    for (int z = 0; z < xLength; z++) {
                        TemplateBlock block;
                        if (x == zLength - 1) {
                            block = new TemplateBlock(Material.STONE);
                        } else {
                            block = new TemplateBlock(Material.DIAMOND_BLOCK);
                        }
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }

        private Template createRotateCounterClockwiseTemplate(int xLength, int zLength) {
            Template template = new Template();
            template.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[zLength][1][xLength];
            for (int x = 0; x < zLength; x++) {
                for (int y = 0; y < 1; y++) {
                    for (int z = 0; z < xLength; z++) {
                        TemplateBlock block;
                        if (x == 0) {
                            block = new TemplateBlock(Material.STONE);
                        } else {
                            block = new TemplateBlock(Material.DIAMOND_BLOCK);
                        }
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }

        @Test
        public void rotateTemplate_whenRotatingClockwiseSquareTemplate_shouldUpdateTemplate() {
            // Arrange
            int xLength = 3;
            int zLength = 3;
            Template templateToCheck = createRotateTemplate(xLength, zLength);
            Template templateResult = createRotateClockwiseTemplate(xLength, zLength);

            // Act
            templateToCheck.rotateTemplate(true);

            // Assert
            Assert.assertEquals(templateResult, templateToCheck);
        }

        @Test
        public void rotateTemplate_whenRotatingCounterClockwiseSquareTemplate_shouldUpdateTemplate() {
            // Arrange
            int xLength = 3;
            int zLength = 3;
            Template templateToCheck = createRotateTemplate(xLength, zLength);
            Template templateResult = createRotateCounterClockwiseTemplate(xLength, zLength);

            // Act
            templateToCheck.rotateTemplate(false);

            // Assert
            Assert.assertEquals(templateResult, templateToCheck);
        }

        @Test
        public void rotateTemplate_whenRotatingRectangularTemplateClockwise_shouldReturnTrue() {
            // Arrange
            int xLength = 2;
            int zLength = 4;
            Template templateToCheck = createRotateTemplate(xLength, zLength);
            Template templateResult = createRotateClockwiseTemplate(xLength, zLength);

            // Act
            templateToCheck.rotateTemplate(true);

            // Assert
            Assert.assertEquals(templateResult, templateToCheck);
        }

        @Test
        public void rotateTemplate_whenRotatingCounterClockwiseRectangleTemplate_shouldShouldReturnTrue() {
            // Arrange
            int xLength = 2;
            int zLength = 4;
            Template templateToCheck = createRotateTemplate(xLength, zLength);
            Template templateResult = createRotateCounterClockwiseTemplate(xLength, zLength);

            // Act
            templateToCheck.rotateTemplate(false);

            // Assert
            Assert.assertEquals(templateResult, templateToCheck);
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class MarkerTests {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void addMarker_whenAllValuesEnteredCorrectly_shouldAddMarker() {
            // Arrange
            Template t = template();

            // Act
            boolean added = t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));

            // Assert
            Assert.assertTrue(added);
        }

        @Test
        public void addMarker_whenWorldLocationIsNot0_shouldAddMarker() {
            // Arrange
            Template t = template();

            // Act
            boolean added = t.addMarker("DOOR", new Point(5, 5, 5), new Point(3, 3, 3));

            // Assert
            Assert.assertTrue(added);
        }

        @Test
        public void addMarker_whenSameMarkerAddedMultipleTimes_shouldOverwritePosition() {
            // Arrange
            Template t = template();

            t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));
            t.addMarker("DOOR", new Point(1, 1, 1), new Point(0, 0, 0));

            // Act
            Point p = t.getMarkers().get("DOOR");

            // Assert
            Assert.assertEquals(p, new Point(1, 1, 1));
        }

        @Test
        public void addMarker_whenOutsideTemplate_shouldNotAddMarker() {
            // Arrange
            Template t = template();

            // Act
            boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

            // Assert
            Assert.assertFalse(added);
        }

        @Test
        public void addMarker_whenMarkerAlreadyExistsButAddingOutsideTemplate_shouldNotAdd() {
            // Arrange
            Template t = template();

            t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));

            // Act
            boolean added = t.addMarker("DOOR", new Point(10, 10, 10), new Point(0, 0, 0));

            // Assert
            Assert.assertFalse(added);
        }

        @Test
        public void removeMarker_whenAllValuesEnteredCorrectly_shouldRemoveMarker() {
            // Arrange
            Template t = template();

            t.addMarker("DOOR", new Point(0, 0, 0), new Point(0, 0, 0));

            // Act
            boolean removed = t.removeMarker("DOOR");

            // Assert
            Assert.assertTrue(removed);
        }

        @Test
        public void removeMarker_whenMarkerDoesNotExist_shouldReturnFalse() {
            // Arrange
            Template t = template();

            // Act
            boolean removed = t.removeMarker("DOOR");

            // Assert
            Assert.assertFalse(removed);
        }

        private Template template() {
            Template template = new Template();

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.STONE);
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class, WorldHelper.class, TransactionManager.class})
    @PowerMockIgnore("javax.management.*")
    public static class TemplatePutTests {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void putTemplateInWorld_whenCorrect_shouldSetBlocksCorrectAmountOfTimes() throws Exception {
            Template t = createTemplate();

            Location l = createDefaultLocation();

            mockStatic(WorldHelper.class);
            mockStatic(TransactionManager.class);
            Transaction mockTransaction = mock(Transaction.class);
            when(WorldHelper.setBlock(any(Location.class), any(Material.class),
                                      anyByte())).thenReturn(mockTransaction);
            when(WorldHelper.setBlock(eq(l.getWorld()), anyInt(), anyInt(), anyInt(), any(),
                                      eq((byte) 0))).thenCallRealMethod();
            when(WorldHelper.getBlock(any(Location.class))).thenCallRealMethod();

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            PowerMockito.doNothing().when(
                    TransactionManager.class, "addDoneTransaction", any(Player.class), captor.capture());

            t.putTemplateInWorld(l, mock(Player.class));

            assertEquals(27, ((SequenceTransaction) captor.getValue()).getChildren().size());
        }

        private Template createTemplate() {
            Template template = new Template();
            template.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.STONE);
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            template.setTemplateBlocks(blocks);

            return template;
        }

        private Location createDefaultLocation() {
            final Location mockLocation = mock(Location.class);
            World w = MockHelper.createDefaultWorld();
            when(mockLocation.getWorld()).thenReturn(w);
            return mockLocation;
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class TemplateSerializerTest {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void serialize_whenSingleBlock_shouldSerialize() {
            // Arrange
            // Create template with 1 block
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
            TemplateBlock block = new TemplateBlock(Material.STONE);
            block.setMetadata(new MaterialData(1, (byte) 0));
            blocks[0][0][0] = block;
            t.setTemplateBlocks(blocks);

            // Create expected NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox()));

            NBTTagList list = new NBTTagList();

            NBTTagCompound myBlock = new NBTTagCompound();
            myBlock.setString("material", "STONE");

            NBTTagCompound metadata = new NBTTagCompound();
            metadata.setInt("type", 1);
            metadata.setByte("data", (byte) 0);

            myBlock.set("metadata", metadata);
            list.add(myBlock);

            expected.set("templateBlocks", list);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }

        @Test
        public void serialize_when3DNormalBlocks_shouldSerialize() {
            // Arrange
            // Create a Template with a 3D selection of blocks
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.STONE);
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        blocks[x][y][z] = block;
                    }
                }
            }
            TemplateBlock b = new TemplateBlock(Material.COBBLESTONE);
            b.setMetadata(new MaterialData(4, (byte) 0));
            blocks[2][2][2] = b;
            t.setTemplateBlocks(blocks);

            // Create the expected NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        NBTTagCompound block = new NBTTagCompound();
                        block.setString("material", "STONE");

                        NBTTagCompound meta = new NBTTagCompound();
                        meta.setInt("type", 1);
                        meta.setByte("data", (byte) 0);
                        block.set("metadata", meta);

                        blockList.add(block);
                    }
                }
            }
            NBTTagCompound block = new NBTTagCompound();
            block.setString("material", "COBBLESTONE");

            NBTTagCompound meta = new NBTTagCompound();
            meta.setInt("type", 4);
            meta.setByte("data", (byte) 0);
            block.set("metadata", meta);

            blockList.add(block);
            blockList.remove(9);
            expected.set("templateBlocks", blockList);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }

        @Test
        public void serialize_whenSingleTileEntity_shouldSerialize() {
            // Arrange
            // Create a Template with a single tile entity
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
            TemplateBlock block = new TemplateBlock(Material.CHEST);
            block.setMetadata(new MaterialData(54, (byte) 0));

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);
            block.setData(chestData);

            blocks[0][0][0] = block;
            t.setTemplateBlocks(blocks);

            // Create expected NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox()));

            NBTTagList list = new NBTTagList();

            NBTTagCompound myBlock = new NBTTagCompound();
            myBlock.setString("material", "CHEST");

            NBTTagCompound metadata = new NBTTagCompound();
            metadata.setInt("type", 54);
            metadata.setByte("data", (byte) 0);

            myBlock.set("metadata", metadata);
            myBlock.set("data", chestData);
            list.add(myBlock);

            expected.set("templateBlocks", list);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }

        @Test
        public void serialize_when3DTileEntities_shouldSerialize() {
            // Arrange
            // Create Template with 3D structure of Tile Entities
            Template t = new Template();
            t.setTemplateName("template");

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.CHEST);
                        block.setMetadata(new MaterialData(54, (byte) 0));

                        block.setData(chestData);

                        blocks[x][y][z] = block;
                    }
                }
            }
            t.setTemplateBlocks(blocks);

            // Create expected NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        NBTTagCompound myBlock = new NBTTagCompound();
                        myBlock.setString("material", "CHEST");

                        NBTTagCompound metadata = new NBTTagCompound();
                        metadata.setInt("type", 54);
                        metadata.setByte("data", (byte) 0);

                        myBlock.set("metadata", metadata);
                        myBlock.set("data", chestData);
                        blockList.add(myBlock);
                    }
                }
            }
            expected.set("templateBlocks", blockList);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }

        @Test
        public void serialize_whenCombinationTileEntitiesAndNormalBlocks_shouldSerialize() {
            // Arrange
            // Create Template with 3D structure with both normal blocks and tile entities
            Template t = new Template();
            t.setTemplateName("template");

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (x % 2 != y % 2 && y % 2 != z % 2) {
                            TemplateBlock block = new TemplateBlock(Material.CHEST);
                            block.setMetadata(new MaterialData(54, (byte) 0));

                            block.setData(chestData);

                            blocks[x][y][z] = block;
                        } else {
                            TemplateBlock block = new TemplateBlock(Material.STONE);
                            block.setMetadata(new MaterialData(1, (byte) 0));
                            blocks[x][y][z] = block;
                        }
                    }
                }
            }
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (x % 2 != y % 2 && y % 2 != z % 2) {
                            NBTTagCompound myBlock = new NBTTagCompound();
                            myBlock.setString("material", "CHEST");

                            NBTTagCompound metadata = new NBTTagCompound();
                            metadata.setInt("type", 54);
                            metadata.setByte("data", (byte) 0);

                            myBlock.set("metadata", metadata);
                            myBlock.set("data", chestData);
                            blockList.add(myBlock);
                        } else {
                            NBTTagCompound block = new NBTTagCompound();
                            block.setString("material", "STONE");

                            NBTTagCompound meta = new NBTTagCompound();
                            meta.setInt("type", 1);
                            meta.setByte("data", (byte) 0);
                            block.set("metadata", meta);

                            blockList.add(block);
                        }
                    }
                }
            }
            expected.set("templateBlocks", blockList);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }

        @Test
        public void serialize_whenTemplateWithMarkers_shouldSerialize() {
            // Arrange
            // Create Template with markers
            Template t = new Template();
            t.setTemplateName("template");

            t.getMarkers().put("DOOR", new Point(0, 0, 0));

            TemplateBlock tb = new TemplateBlock(Material.STONE);
            tb.setMetadata(new MaterialData(1, (byte) 0));
            t.setTemplateBlocks(new TemplateBlock[][][] {{{tb}}});

            // Create expected NBTTagCompound
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0)));

            NBTTagList markerList = new NBTTagList();
            NBTTagCompound marker = new NBTTagCompound();
            marker.setString("name", "DOOR");
            marker.setInt("X", 0);
            marker.setInt("Y", 0);
            marker.setInt("Z", 0);
            markerList.add(marker);
            expected.set("markers", markerList);

            NBTTagList blockList = new NBTTagList();
            NBTTagCompound block = new NBTTagCompound();
            block.setString("material", "STONE");
            NBTTagCompound meta = new NBTTagCompound();
            meta.setInt("type", 1);
            meta.setByte("data", (byte) 0);
            block.set("metadata", meta);
            blockList.add(block);
            expected.set("templateBlocks", blockList);

            // Act
            NBTTagCompound actual = Template.toNBT(t);

            // Assert
            Assert.assertEquals(expected, actual);
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class TemplateEqualsTest {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void equals_whenEmptyTemplate_shouldBeEqual() {
            Template a = new Template();
            Template b = new Template();

            Assert.assertEquals(a, b);
        }

        @Test
        public void equals_whenFilledTemplate_shouldBeEqual() {
            Template[] templates = createSameTemplates(2);

            Assert.assertEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenDifferentName_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[1].setTemplateName("not template");

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenDifferentSize_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            TemplateBlock block = new TemplateBlock(Material.STONE);
            block.setMetadata(new MaterialData(1, (byte) 0));
            TemplateBlock[][][] blocks = new TemplateBlock[][][] {{{block}}};
            templates[1].setTemplateBlocks(blocks);

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenSameSizeDifferentBlocks_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            TemplateBlock block = new TemplateBlock(Material.COBBLESTONE);
            block.setMetadata(new MaterialData(4, (byte) 0));
            templates[0].getTemplateBlocks()[0][0][0] = block;

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenMissingMarker_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[1].getMarkers().remove("GROUND");

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenExtraMarker_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[1].getMarkers().put("TEST", new Point(0, 0, 0));

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenDifferentMarker_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[1].getMarkers().remove("GROUND");
            templates[1].getMarkers().put("TEST", new Point(0, 0, 0));

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenSameMarkerAtDifferentPlace_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[1].getMarkers().put("GROUND", new Point(1, 0, 0));

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenDifferentType_shouldNotBeEqual() {
            Assert.assertNotEquals(new Template(), new Object());
        }

        @Test
        public void equals_whenMissingName_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[0].setTemplateName(null);

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        @Test
        public void equals_whenMissingBlocks_shouldNotBeEqual() {
            Template[] templates = createSameTemplates(2);

            templates[0].setTemplateBlocks(null);

            Assert.assertNotEquals(templates[0], templates[1]);
        }

        private Template[] createSameTemplates(int n) {
            Template[] templates = new Template[n];

            for (int i = 0; i < n; i++) {
                Template t = new Template();
                t.setTemplateName("template");

                TemplateBlock a = new TemplateBlock(Material.STONE);
                a.setMetadata(new MaterialData(1, (byte) 0));
                TemplateBlock b = new TemplateBlock(Material.STONE);
                b.setMetadata(new MaterialData(1, (byte) 3));
                TemplateBlock c = new TemplateBlock(Material.SIGN);
                c.setMetadata(new MaterialData(323, (byte) 5));
                TemplateBlock[][][] blocks = new TemplateBlock[][][] {{{a, b, c}}};
                t.setTemplateBlocks(blocks);

                t.getMarkers().put("DOOR", new Point(0, 0, 0));
                t.getMarkers().put("GROUND", new Point(0, 0, 2));

                templates[i] = t;
            }

            return templates;
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class TemplateDeserializerTest {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void deserialize_whenSingleBlock_shouldDeserialize() {
            // Arrange
            // Create expected template
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
            TemplateBlock block = new TemplateBlock(Material.STONE);
            block.setMetadata(new MaterialData(1, (byte) 0));
            block.setData(new NBTTagCompound());
            blocks[0][0][0] = block;
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound with a single block
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox()));

            NBTTagList list = new NBTTagList();

            NBTTagCompound myBlock = new NBTTagCompound();
            myBlock.setString("material", "STONE");

            NBTTagCompound metadata = new NBTTagCompound();
            metadata.setInt("type", 1);
            metadata.setByte("data", (byte) 0);
            myBlock.set("metadata", metadata);

            list.add(myBlock);

            expected.set("templateBlocks", list);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }

        @Test
        public void deserialize_when3DNormalBlocks_shouldDeserialize() {
            // Arrange
            // Create expected Template
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.STONE);
                        block.setMetadata(new MaterialData(1, (byte) 0));
                        block.setData(new NBTTagCompound());
                        blocks[x][y][z] = block;
                    }
                }
            }
            TemplateBlock b = new TemplateBlock(Material.COBBLESTONE);
            b.setMetadata(new MaterialData(4, (byte) 0));
            b.setData(new NBTTagCompound());
            blocks[2][2][2] = b;
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound with 3D structure of normal blocks
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        NBTTagCompound block = new NBTTagCompound();
                        block.setString("material", "STONE");

                        NBTTagCompound meta = new NBTTagCompound();
                        meta.setInt("type", 1);
                        meta.setByte("data", (byte) 0);
                        block.set("metadata", meta);

                        blockList.add(block);
                    }
                }
            }
            NBTTagCompound block = new NBTTagCompound();
            block.setString("material", "COBBLESTONE");

            NBTTagCompound meta = new NBTTagCompound();
            meta.setInt("type", 4);
            meta.setByte("data", (byte) 0);
            block.set("metadata", meta);

            blockList.remove(8);
            blockList.add(block);
            expected.set("templateBlocks", blockList);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }

        @Test
        public void deserialize_whenSingleTileEntity_shouldDeserialize() {
            // Arrange
            // Create expected Template
            Template t = new Template();
            t.setTemplateName("template");

            TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
            TemplateBlock block = new TemplateBlock(Material.CHEST);
            block.setMetadata(new MaterialData(54, (byte) 0));

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);
            block.setData(chestData);

            blocks[0][0][0] = block;
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound with a single tile entity
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox()));

            NBTTagList list = new NBTTagList();

            NBTTagCompound myBlock = new NBTTagCompound();
            myBlock.setString("material", "CHEST");

            NBTTagCompound metadata = new NBTTagCompound();
            metadata.setInt("type", 54);
            metadata.setByte("data", (byte) 0);

            myBlock.set("metadata", metadata);
            myBlock.set("data", chestData);
            list.add(myBlock);

            expected.set("templateBlocks", list);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }

        @Test
        public void deserialize_when3DTileEntities_shouldDeserialize() {
            // Arrange
            // Create expected Template
            Template t = new Template();
            t.setTemplateName("template");

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        TemplateBlock block = new TemplateBlock(Material.CHEST);
                        block.setMetadata(new MaterialData(54, (byte) 0));

                        block.setData(chestData);

                        blocks[x][y][z] = block;
                    }
                }
            }
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound with 3D structure of tile entities
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        NBTTagCompound myBlock = new NBTTagCompound();
                        myBlock.setString("material", "CHEST");

                        NBTTagCompound metadata = new NBTTagCompound();
                        metadata.setInt("type", 54);
                        metadata.setByte("data", (byte) 0);

                        myBlock.set("metadata", metadata);
                        myBlock.set("data", chestData);
                        blockList.add(myBlock);
                    }
                }
            }
            expected.set("templateBlocks", blockList);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }

        @Test
        public void deserialize_whenCombinationTileEntitiesAndNormalBlocks_shouldWork() {
            // Arrange
            // Create expected Template
            Template t = new Template();
            t.setTemplateName("template");

            NBTTagCompound chestData = new NBTTagCompound();
            NBTTagList items = new NBTTagList();
            NBTTagCompound item = new NBTTagCompound();
            item.setInt("slot", 0);
            item.setString("id", "minecraft:sign");
            item.setInt("count", 1);
            item.setInt("damage", 0);
            items.add(item);
            chestData.set("items", items);

            TemplateBlock[][][] blocks = new TemplateBlock[3][3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (x % 2 != y % 2 && y % 2 != z % 2) {
                            TemplateBlock block = new TemplateBlock(Material.CHEST);
                            block.setMetadata(new MaterialData(54, (byte) 0));

                            block.setData(chestData);

                            blocks[x][y][z] = block;
                        } else {
                            TemplateBlock block = new TemplateBlock(Material.STONE);
                            block.setMetadata(new MaterialData(1, (byte) 0));
                            block.setData(new NBTTagCompound());
                            blocks[x][y][z] = block;
                        }
                    }
                }
            }
            t.setTemplateBlocks(blocks);

            // Create NBTTagCompound with 3D structure of normal blocks and tile entities combined
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("markers", new NBTTagList());
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0, 2, 2, 2)));

            NBTTagList blockList = new NBTTagList();
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (x % 2 != y % 2 && y % 2 != z % 2) {
                            NBTTagCompound myBlock = new NBTTagCompound();
                            myBlock.setString("material", "CHEST");

                            NBTTagCompound metadata = new NBTTagCompound();
                            metadata.setInt("type", 54);
                            metadata.setByte("data", (byte) 0);

                            myBlock.set("metadata", metadata);
                            myBlock.set("data", chestData);
                            blockList.add(myBlock);
                        } else {
                            NBTTagCompound block = new NBTTagCompound();
                            block.setString("material", "STONE");

                            NBTTagCompound meta = new NBTTagCompound();
                            meta.setInt("type", 1);
                            meta.setByte("data", (byte) 0);
                            block.set("metadata", meta);
                            block.set("data", new NBTTagCompound());

                            blockList.add(block);
                        }
                    }
                }
            }
            expected.set("templateBlocks", blockList);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }

        @Test
        public void deserialize_whenTemplateWithMarkers_shouldDeserialize() {
            // Arrange
            // Create expected Template
            Template t = new Template();
            t.setTemplateName("template");

            t.getMarkers().put("DOOR", new Point(0, 0, 0));

            TemplateBlock tb = new TemplateBlock(Material.STONE);
            tb.setMetadata(new MaterialData(1, (byte) 0));
            tb.setData(new NBTTagCompound());
            t.setTemplateBlocks(new TemplateBlock[][][] {{{tb}}});

            // Create NBTTagCompound with markers
            NBTTagCompound expected = new NBTTagCompound();
            expected.setString("templateName", "template");
            expected.set("boundingBox", BoundingBox.toNBT(new BoundingBox(0, 0, 0)));

            NBTTagList markerList = new NBTTagList();
            NBTTagCompound marker = new NBTTagCompound();
            marker.setString("name", "DOOR");
            marker.setInt("X", 0);
            marker.setInt("Y", 0);
            marker.setInt("Z", 0);
            markerList.add(marker);
            expected.set("markers", markerList);

            NBTTagList blockList = new NBTTagList();
            NBTTagCompound block = new NBTTagCompound();
            block.setString("material", "STONE");
            NBTTagCompound meta = new NBTTagCompound();
            meta.setInt("type", 1);
            meta.setByte("data", (byte) 0);
            block.set("metadata", meta);
            block.set("data", new NBTTagCompound());
            blockList.add(block);
            expected.set("templateBlocks", blockList);

            // Act
            Template actual = Template.fromNBT(expected);

            // Assert
            Assert.assertEquals(t, actual);
        }
    }

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({BuildFramework.class})
    @PowerMockIgnore("javax.management.*")
    public static class TemplateBlockEqualsTest {

        private List<String> markers = new ArrayList<>();

        @Before
        public void setup() {
            markers.add("GROUND");
            markers.add("PATH");
            markers.add("DOOR");

            mockStatic(BuildFramework.class);

            final BuildFramework mockPlugin = mock(BuildFramework.class);
            final FileConfiguration mockConfig = mock(FileConfiguration.class);

            when(BuildFramework.getInstance()).thenReturn(mockPlugin);
            when(mockPlugin.getTemplateFormatConfig()).thenReturn(mockConfig);

            when(mockConfig.getStringList("Markers")).thenReturn(markers);
        }

        @Test
        public void equals_whenSameBlock_shouldBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            Assert.assertEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenDifferentMaterial_shouldNotBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            blocks[1].setMaterial(Material.AIR);

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenMissingMaterial_shouldNotBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            blocks[1].setMaterial(null);

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenDifferentMetadata_shouldNotBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            blocks[1].setMetadata(new MaterialData(0, (byte) 0));

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenSameWithoutMetadata_shouldBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            blocks[0].setMetadata(null);
            blocks[1].setMetadata(null);

            Assert.assertEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenMissingMetadata_shouldNotBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            blocks[1].setMetadata(null);

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenSameWithSameNBTData_shouldBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            NBTTagCompound ntc = new NBTTagCompound();
            ntc.setString("a", "a");
            blocks[0].setData(ntc);
            blocks[1].setData(ntc);

            Assert.assertEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenSameWithDifferentNBTData_shouldNotBeEquals() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            NBTTagCompound ntc = new NBTTagCompound();
            ntc.setString("a", "a");
            NBTTagCompound ntc2 = new NBTTagCompound();
            ntc.setString("a", "b");
            blocks[0].setData(ntc);
            blocks[1].setData(ntc2);

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        @Test
        public void equals_whenMissingNBTData_shouldBeEqual() {
            TemplateBlock[] blocks = createTemplateBlocks(2);

            NBTTagCompound ntc = new NBTTagCompound();
            ntc.setString("a", "a");
            blocks[0].setData(null);
            blocks[1].setData(ntc);

            Assert.assertNotEquals(blocks[0], blocks[1]);
        }

        private TemplateBlock[] createTemplateBlocks(int n) {
            TemplateBlock[] templates = new TemplateBlock[n];

            for (int i = 0; i < n; i++) {
                TemplateBlock a = new TemplateBlock(Material.STONE);
                a.setMetadata(new MaterialData(1, (byte) 0));
                templates[i] = a;
            }

            return templates;
        }
    }
}
