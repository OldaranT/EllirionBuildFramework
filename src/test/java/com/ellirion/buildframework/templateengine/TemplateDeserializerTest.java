package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.junit.Assert;
import org.junit.Test;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateBlock;

public class TemplateDeserializerTest {

    @Test
    public void Deserialize_SingleBlock_ShouldDeserialize() {
        Template t = new Template();
        t.setTemplateName("template");

        TemplateBlock[][][] blocks = new TemplateBlock[1][1][1];
        TemplateBlock block = new TemplateBlock(Material.STONE);
        block.setMetadata(new MaterialData(1, (byte) 0));
        block.setData(new NBTTagCompound());
        blocks[0][0][0] = block;
        t.setTemplateBlocks(blocks);

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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }

    @Test
    public void Deserialize3DNormalBlocks() {
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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }

    @Test
    public void DeserializeSingleTileEntity() {
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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }

    @Test
    public void Deserialize3DTileEntities() {
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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }

    @Test
    public void DeserializeCombinationTileEntitiesAndNormalBlocks() {
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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }

    @Test
    public void Deserialize_TemplateWithMarkers_ShouldDeserialize() {

        Template t = new Template();
        t.setTemplateName("template");

        t.getMarkers().put("DOOR", new Point(0, 0, 0));

        TemplateBlock tb = new TemplateBlock(Material.STONE);
        tb.setMetadata(new MaterialData(1, (byte) 0));
        tb.setData(new NBTTagCompound());
        t.setTemplateBlocks(new TemplateBlock[][][] {{{tb}}});

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

        Template actual = Template.fromNBT(expected);

        Assert.assertEquals(t, actual);
    }
}
