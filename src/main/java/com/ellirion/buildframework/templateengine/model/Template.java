package com.ellirion.buildframework.templateengine.model;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template {

    private static Material[] PLACELATE = new Material[] {
            Material.WALL_SIGN,
            Material.WALL_BANNER,
            Material.BANNER,
            Material.LADDER,
            Material.PAINTING,
            Material.ITEM_FRAME,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.LEVER,
            Material.REDSTONE,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.VINE,
            Material.TRIPWIRE_HOOK,
            Material.PAINTING,
            Material.PISTON_BASE,
            Material.PISTON_EXTENSION,
            Material.PISTON_STICKY_BASE,
            Material.TORCH,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.IRON_DOOR_BLOCK
    };

    private static final String DATA = "data";
    @Getter private static final List<String> FINALMARKERLIST = BuildFramework.getInstance().getTemplateFormatConfig().getStringList(
            "Markers");
    @Getter @Setter private String templateName;
    @Getter @Setter private TemplateBlock[][][] templateBlocks;
    @Getter private HashMap<String, Point> markers;

    /**
     * Empty constructor.
     */
    public Template() {
        this.markers = new HashMap<>();
    }

    /**
     * @param name Name of the template
     * @param selection Selected area
     * @param world the world the template is in
     */
    public Template(final String name, final BoundingBox selection, final World world) {
        templateName = name;
        markers = new HashMap<>();

        // Get all blocks from the area
        Point start = selection.getPoint1();
        Point end = selection.getPoint2();

        int startX = Math.min((int) start.getX(), (int) end.getX());
        int startY = Math.min((int) start.getY(), (int) end.getY());
        int startZ = Math.min((int) start.getZ(), (int) end.getZ());

        int endX = Math.max((int) start.getX(), (int) end.getX());
        int endY = Math.max((int) start.getY(), (int) end.getY());
        int endZ = Math.max((int) start.getZ(), (int) end.getZ());

        int templateX = 0;
        int templateY = 0;
        int templateZ = 0;

        int xDepth = endX - startX + 1;
        int yDepth = endY - startY + 1;
        int zDepth = endZ - startZ + 1;
        templateBlocks = new TemplateBlock[xDepth][yDepth][zDepth];

        CraftWorld w = (CraftWorld) world;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    templateBlocks[templateX][templateY][templateZ] = new TemplateBlock(
                            world.getBlockAt(x, y, z).getType());

                    Block b = world.getBlockAt(x, y, z);
                    BlockState state = b.getState();
                    templateBlocks[templateX][templateY][templateZ].setMetadata(
                            new MaterialData(state.getType(), state.getData().getData()));

                    TileEntity te = w.getTileEntityAt(x, y, z);
                    if (te != null) {
                        templateBlocks[templateX][templateY][templateZ].setData(te.save(new NBTTagCompound()));
                    }
                    templateZ++;
                }
                templateZ = 0;
                templateY++;
            }
            templateY = 0;
            templateX++;
        }
    }

    /**
     * Place a template in the world at a given location.
     * @param loc location to place the template.
     */
    public void putTemplateInWorld(Location loc) {
        int xDepth = this.getTemplateBlocks().length;
        int yDepth = this.getTemplateBlocks()[0].length;
        int zDepth = this.getTemplateBlocks()[0][0].length;

        CraftWorld w = (CraftWorld) loc.getWorld();

        HashMap<Point, TemplateBlock> toPlaceLast = new HashMap<>();
        List<DoorWrapper> doors = new ArrayList<>();

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    if (Arrays.asList(PLACELATE).contains(templateBlocks[x][y][z].getMaterial())) {
                        if (templateBlocks[x][y][z].getMaterial().toString().contains("DOOR") &&
                            !templateBlocks[x][y][z].getMaterial().toString().contains("TRAP")) {
                            if ((int) templateBlocks[x][y][z].getMetadata().getData() < 8) {
                                doors.add(new DoorWrapper(templateBlocks[x][y][z].getMetadata(),
                                                          templateBlocks[x][y + 1][z].getMetadata().getData(),
                                                          templateBlocks[x][y][z].getMetadata().getData(),
                                                          new Point(loc.getBlockX() + x, loc.getBlockY() + y,
                                                                    loc.getBlockZ() + z)));
                            }
                        } else {
                            toPlaceLast.put(new Point(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z),
                                            templateBlocks[x][y][z]);
                        }
                    } else {
                        int locX = loc.getBlockX() + x;
                        int locY = loc.getBlockY() + y;
                        int locZ = loc.getBlockZ() + z;

                        Block b = w.getBlockAt(locX, locY, locZ);
                        b.setType(getTemplateBlocks()[x][y][z].getMaterial());
                        b.getState().update();

                        MaterialData copiedState = getTemplateBlocks()[x][y][z].getMetadata();
                        BlockState blockState = b.getState();
                        blockState.setData(copiedState);
                        blockState.update(false, false);

                        TileEntity te = w.getHandle().getTileEntity(new BlockPosition(locX, locY, locZ));

                        if (te != null) {
                            NBTTagCompound ntc = getTemplateBlocks()[x][y][z].getData();
                            ntc.setInt("x", locX);
                            ntc.setInt("y", locY);
                            ntc.setInt("z", locZ);
                            te.load(ntc);
                        }
                    }
                }
            }
        }

        //Place blocks that need other blocks to stay on their position.
        for (Map.Entry pair : toPlaceLast.entrySet()) {
            Point p = (Point) pair.getKey();
            TemplateBlock block = (TemplateBlock) pair.getValue();
            Block b = w.getBlockAt(p.getBlockX(), p.getBlockY(), p.getBlockZ());

            Block below = b.getRelative(BlockFace.DOWN);
            Material belowMaterial = below.getType();
            byte metadata = below.getState().getData().getData();
            NBTTagCompound ntc = new NBTTagCompound();
            TileEntity te = w.getHandle().getTileEntity(
                    new BlockPosition(below.getX(), below.getY(), below.getZ()));
            if (te != null) {
                ntc.a(te.d());
            }

            below.setType(Material.STONE);

            b.setType(block.getMaterial());
            BlockState state = b.getState();
            state.setData(block.getMetadata());
            state.update();

            below.setType(belowMaterial);
            below.getState().setData(new MaterialData(belowMaterial, metadata));
            below.getState().update(false, false);
        }

        //Place doors as last.
        for (DoorWrapper dw : doors) {
            Point p = dw.getPoint();

            Block doorBottem = w.getBlockAt(p.getBlockX(), p.getBlockY(), p.getBlockZ());
            Block doorTop = w.getBlockAt(p.getBlockX(), p.getBlockY() + 1, p.getBlockZ());

            doorBottem.setType(dw.getMaterialData().getItemType());
            doorTop.setType(dw.getMaterialData().getItemType());

            doorBottem.setData(dw.getBottem());
            doorTop.setData(dw.getTop());

            doorBottem.getState().update();
            doorTop.getState().update();
        }
    }

    private void addMarker(String name, Point point) {
        markers.put(name, point);
    }

    /**
     * Add market to the template.
     * @param name Name of the marker.
     * @param markerPoint Point of the maker.
     * @param worldLocation Point of the template in the world.
     * @return if the markers is in the template reach.
     */
    public boolean addMarker(String name, Point markerPoint, Point worldLocation) {
        if (checkIfMarkerIsWithInTemplate(markerPoint, worldLocation)) {
            this.markers.put(name, markerPoint.translate(worldLocation.invert()));
            return true;
        }
        return false;
    }

    private boolean checkIfMarkerIsWithInTemplate(Point markerPoint, Point worldLocation) {
        BoundingBox boundingBox = getBoundingBox();
        boundingBox = boundingBox.toWorld(worldLocation);

        return boundingBox.intersects(markerPoint);
    }

    /**
     * Remove marker.
     * @param name name of the marker
     * @return Point of the selected marker.
     */
    public boolean removeMarker(String name) {
        return this.markers.remove(name) != null;
    }

    /**
     * @return boundingbox of the template.
     */
    public BoundingBox getBoundingBox() {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        return new BoundingBox(0, 0, 0, xDepth - 1, yDepth - 1, zDepth - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Template)) {
            return false;
        }
        Template other = (Template) obj;

        // Template name has to be either null in both or the same in both
        if (templateName == null ? other.templateName != null : !templateName.equals(other.templateName)) {
            return false;
        }

        // TemplateBlocks has to be null in both or the same in both
        if (templateBlocks == null ^ other.templateBlocks == null) {
            return false;
        }
        if (templateBlocks != null && other.templateBlocks != null) {
            TemplateBlock[][][] otherblocks = other.getTemplateBlocks();
            int x = 0, y = 0, z = 0;
            try {
                for (TemplateBlock[][] blockss : templateBlocks) {
                    for (TemplateBlock[] blocks : blockss) {
                        for (TemplateBlock block : blocks) {
                            if (block.equals(otherblocks[x][y][z])) {
                                z++;
                            } else {
                                return false;
                            }
                        }
                        z = 0;
                        y++;
                    }
                    y = 0;
                    x++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false;
            }
        }

        // Check markers
        if (markers.size() != other.markers.size()) {
            return false;
        }
        for (Map.Entry pair : markers.entrySet()) {
            // Check if all markers have the same position
            if (!other.markers.containsKey(pair.getKey())) {
                return false;
            }
            if (!(other.markers.get(pair.getKey()).equals(pair.getValue()))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Creates an NBTTagCompound from a given template.
     * @param t The template to convert to NBT
     * @return The NBT data for the given template
     */
    public static NBTTagCompound toNBT(Template t) {
        NBTTagCompound ntc = new NBTTagCompound();

        ntc.setString("templateName", t.getTemplateName());
        ntc.set("boundingBox", BoundingBox.toNBT(t.getBoundingBox()));

        TemplateBlock[][][] templateBlocks = t.getTemplateBlocks();
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        NBTTagList listOfMarkers = new NBTTagList();

        for (Map.Entry pair : t.getMarkers().entrySet()) {
            NBTTagCompound marker = new NBTTagCompound();
            Point point = (Point) pair.getValue();
            marker.setString("name", pair.getKey().toString());
            marker.setInt("X", (int) point.getX());
            marker.setInt("Y", (int) point.getY());
            marker.setInt("Z", (int) point.getZ());
            listOfMarkers.add(marker);
        }

        ntc.set("markers", listOfMarkers);

        NBTTagList ntcArrayX = new NBTTagList();
        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {

                    TemplateBlock tb = templateBlocks[x][y][z];
                    NBTTagCompound block = new NBTTagCompound();

                    // Type of the block
                    block.setString("material", tb.getMaterial().name());

                    // Metadata of the block
                    NBTTagCompound metadata = new NBTTagCompound();
                    metadata.setInt("type", tb.getMetadata().getItemTypeId());
                    metadata.setByte(DATA, tb.getMetadata().getData());
                    block.set("metadata", metadata);

                    // NBT data of the block
                    if (tb.getData() != null) {
                        block.set(DATA, tb.getData());
                    }

                    ntcArrayX.add(block);
                }
            }
        }
        ntc.set("templateBlocks", ntcArrayX);

        return ntc;
    }

    /**
     * Creates a template based on NBT data.
     * @param ntc The NBT data to construct the template out of
     * @return The created template
     */
    public static Template fromNBT(NBTTagCompound ntc) {
        Template t = new Template();

        t.setTemplateName(ntc.getString("templateName"));

        NBTTagList markers = ntc.getList("markers", 10);
        for (int i = 0; i < markers.size(); i++) {
            NBTTagCompound marker = markers.get(i);
            String name = marker.getString("name");
            int makerX = marker.getInt("X");
            int makerY = marker.getInt("Y");
            int makerZ = marker.getInt("Z");
            Point markerPoint = new Point(makerX, makerY, makerZ);
            t.addMarker(name, markerPoint);
        }

        NBTTagList arrayX = ntc.getList("templateBlocks", 10);
        BoundingBox bb = BoundingBox.fromNBT(ntc.getCompound("boundingBox"));
        int xDepth = bb.getWidth();
        int yDepth = bb.getHeight();
        int zDepth = bb.getDepth();
        TemplateBlock[][][] tBlocks = new TemplateBlock[xDepth][yDepth][zDepth];
        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    int offZ = z;
                    int offY = y * zDepth;
                    int offX = x * zDepth * yDepth;
                    int i = offX + offY + offZ;

                    NBTTagCompound blockData = arrayX.get(i);

                    // Get the material of the block
                    String material = blockData.getString("material");
                    TemplateBlock tb = new TemplateBlock(Material.valueOf(material));

                    // Get the metadata of the block
                    NBTTagCompound metadata = blockData.getCompound("metadata");
                    MaterialData meta = new MaterialData(metadata.getInt("type"), metadata.getByte(DATA));
                    tb.setMetadata(meta);
                    tBlocks[x][y][z] = tb;

                    // Get the nbt data of the block
                    NBTTagCompound nbtdata = blockData.getCompound(DATA);
                    if (nbtdata != null) {
                        tb.setData(nbtdata);
                    }
                }
            }
        }
        t.setTemplateBlocks(tBlocks);

        return t;
    }

    /**
     * Convert the enum to a string and returns it.
     * @return Enum in strong form.
     */
    public static String markersToString() {
        String markers = "";
        markers += ChatColor.RESET;
        markers += ChatColor.BOLD;
        markers += String.join(", ", FINALMARKERLIST);
        markers += ChatColor.RESET;
        return markers;
    }
}
