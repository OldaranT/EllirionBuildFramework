package com.ellirion.buildframework.templateengine.model;

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
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.util.MinecraftHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template {

    private static final Material[] PLACE_LATE = new Material[] {
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
            Material.IRON_DOOR_BLOCK,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM
    };

    private static final Material[] TO_ROTATE = new Material[] {

            //Building blocks
            Material.LOG_2,
            Material.LOG,
            Material.BONE_BLOCK,
            Material.HAY_BLOCK,
            Material.JACK_O_LANTERN,
            Material.PUMPKIN,
            Material.QUARTZ_BLOCK,

            //Decoration blocks
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.FURNACE,
            Material.ANVIL,
            Material.LADDER,
            Material.VINE,
            Material.WALL_BANNER,
            Material.STANDING_BANNER,
            Material.BED_BLOCK,
            Material.END_ROD,
            Material.WALL_SIGN,
            Material.SIGN_POST,
            Material.SKULL,
            Material.TORCH,

            //Redstone
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.DISPENSER,
            Material.DROPPER,
            Material.OBSERVER,
            Material.HOPPER,
            Material.IRON_TRAPDOOR,
            Material.TRAP_DOOR,
            Material.LEVER,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.STONE_BUTTON,
            Material.WOOD_BUTTON,
            Material.PISTON_BASE,
            Material.PISTON_EXTENSION,
            Material.PISTON_MOVING_PIECE,
            Material.PISTON_STICKY_BASE,
            Material.TRAPPED_CHEST,

            //Fence Gate
            Material.FENCE_GATE,
            Material.ACACIA_FENCE,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,

            //Rail
            Material.ACTIVATOR_RAIL,
            Material.DETECTOR_RAIL,
            Material.RAILS,
            Material.POWERED_RAIL,

            //Door
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.SPRUCE_DOOR,

            //Terracotta
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.SILVER_GLAZED_TERRACOTTA,
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.LIME_GLAZED_TERRACOTTA,

            //Stairs
            Material.ACACIA_STAIRS,
            Material.BIRCH_WOOD_STAIRS,
            Material.BRICK_STAIRS,
            Material.COBBLESTONE_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.JUNGLE_WOOD_STAIRS,
            Material.NETHER_BRICK_STAIRS,
            Material.PURPUR_STAIRS,
            Material.QUARTZ_STAIRS,
            Material.RED_SANDSTONE_STAIRS,
            Material.SANDSTONE_STAIRS,
            Material.SMOOTH_STAIRS,
            Material.SPRUCE_WOOD_STAIRS,
            Material.WOOD_STAIRS,
            };

    private static final String DATA = "data";
    private static final String TEMPLATE_TOOL = "Template Tool";
    private static final List<String> POSSIBLE_MARKERS = BuildFramework.getInstance().getTemplateFormatConfig().getStringList(
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

        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        int xDepth = selection.getWidth();
        int yDepth = selection.getHeight();
        int zDepth = selection.getDepth();
        templateBlocks = new TemplateBlock[xDepth][yDepth][zDepth];

        CraftWorld w = (CraftWorld) world;

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    Block b = world.getBlockAt(x + startX, y + startY, z + startZ);
                    templateBlocks[x][y][z] = new TemplateBlock(b.getType());

                    BlockState state = b.getState();
                    templateBlocks[x][y][z].setMetadata(
                            new MaterialData(state.getType(), state.getData().getData()));

                    TileEntity te = w.getTileEntityAt(x + startX, y + startY, z + startZ);
                    if (te != null) {
                        templateBlocks[x][y][z].setData(te.save(new NBTTagCompound()));
                    }
                }
            }
        }
    }

    /**
     * Return key for tools as a string.
     * @return key for tool.
     */
    public static String getTemplateTool() {
        return TEMPLATE_TOOL;
    }

    /**
     * List of all markers from the config.
     * @return final marker list.
     */
    public static List<String> getPossibleMarkers() {
        return POSSIBLE_MARKERS;
    }

    /**
     * Place a template in the world at a given location.
     * @param loc location to place the template.
     */
    public void putTemplateInWorld(Location loc) {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        CraftWorld w = (CraftWorld) loc.getWorld();

        HashMap<Point, TemplateBlock> toPlaceLast = new HashMap<>();
        List<DoorWrapper> doors = new ArrayList<>();

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    if (Arrays.asList(PLACE_LATE).contains(templateBlocks[x][y][z].getMaterial())) {
                        if (MinecraftHelper.isDoor(templateBlocks[x][y][z].getMaterial())) {
                            if ((int) templateBlocks[x][y][z].getMetadata().getData() < 8) {
                                doors.add(new DoorWrapper(templateBlocks[x][y][z].getMetadata(),
                                                          templateBlocks[x][y + 1][z].getMetadata(),
                                                          new Point(loc.getBlockX() + x, loc.getBlockY() + y,
                                                                    loc.getBlockZ() + z)));
                            }
                        } else {
                            toPlaceLast.put(new Point(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z),
                                            templateBlocks[x][y][z]);
                        }
                        continue;
                    }

                    int locX = loc.getBlockX() + x;
                    int locY = loc.getBlockY() + y;
                    int locZ = loc.getBlockZ() + z;

                    Block b = w.getBlockAt(locX, locY, locZ);
                    b.setType(templateBlocks[x][y][z].getMaterial(), false);
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

        // Place blocks that need other blocks to stay on their position.
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
                ntc = te.save(ntc);
                te.load(new NBTTagCompound());
            }

            below.setType(Material.STONE, false);

            b.setType(block.getMaterial(), false);
            BlockState state = b.getState();
            state.setData(block.getMetadata());
            state.update();

            below.setTypeIdAndData(belowMaterial.ordinal(), metadata, false);
            te = w.getHandle().getTileEntity(
                    new BlockPosition(below.getX(), below.getY(), below.getZ()));
            if (te != null) {
                te.load(ntc);
            }
        }

        // Place doors last.
        for (DoorWrapper dw : doors) {
            Point p = dw.getPoint();

            Block doorBottom = w.getBlockAt(p.getBlockX(), p.getBlockY(), p.getBlockZ());
            Block doorTop = w.getBlockAt(p.getBlockX(), p.getBlockY() + 1, p.getBlockZ());

            doorBottom.setType(dw.getBottomMaterialData().getItemType(), false);
            doorTop.setType(dw.getTopMaterialData().getItemType(), false);

            BlockState bottomState = doorBottom.getState();
            BlockState topState = doorTop.getState();

            bottomState.setData(dw.getBottomMaterialData());
            topState.setData(dw.getTopMaterialData());

            topState.update();
            bottomState.update();
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

        if (!Arrays.deepEquals(templateBlocks, other.templateBlocks)) {
            return false;
        }

        if (!markers.equals(other.markers)) {
            return false;
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
     * Rotate the template 90 degrees in a direction.
     * @param direction True = clockwise, false = counter clockwise
     */
    public void rotateTemplate(boolean direction) {
        int xDepth = templateBlocks.length;
        int yDepth = templateBlocks[0].length;
        int zDepth = templateBlocks[0][0].length;

        TemplateBlock[][][] rotatedTemplateBlock = new TemplateBlock[zDepth][yDepth][xDepth];

        for (int y = 0; y < yDepth; y++) {
            //for each Y rotate x and z.
            for (int x = 0; x < zDepth; x++) {
                for (int z = 0; z < xDepth; z++) {
                    TemplateBlock block;
                    if (direction) {
                        block = templateBlocks[z][y][zDepth - x - 1];
                    } else {
                        block = templateBlocks[xDepth - z - 1][y][x];
                    }
                    if (Arrays.asList(TO_ROTATE).contains(block.getMaterial())) {
                        Material currentMaterial = block.getMaterial();
                        int currentMetaData = (int) block.getMetadata().getData();
                        // Some materials have the same array, so to prevent copying and pasting of the same array 16 times, we get other arrays that are the same
                        if (currentMaterial.toString().contains("GLAZED_TERRACOTTA")) {
                            currentMaterial = Material.BLACK_GLAZED_TERRACOTTA;
                        }
                        if (MinecraftHelper.isDoor(currentMaterial)) {
                            currentMaterial = Material.ACACIA_DOOR;
                        }
                        if (MinecraftHelper.isStair(currentMaterial)) {
                            currentMaterial = Material.ACACIA_STAIRS;
                        }
                        if (MinecraftHelper.isFenceGate(currentMaterial)) {
                            currentMaterial = Material.FENCE_GATE;
                        }

                        int newMetaData = MinecraftHelper.getMaterialRotationData(currentMaterial, currentMetaData,
                                                                                  direction);
                        block.getMetadata().setData((byte) newMetaData);
                    }
                    rotatedTemplateBlock[x][y][z] = block;
                }
            }
        }

        this.templateBlocks = rotatedTemplateBlock;
        rotateMarkers();
    }

    private void rotateMarkers() {
        int xDepth = templateBlocks.length;

        for (Map.Entry pair : getMarkers().entrySet()) {
            Point oldPoint = (Point) pair.getValue();
            markers.put((String) pair.getKey(),
                        new Point((xDepth - oldPoint.getBlockZ() - 1), oldPoint.getBlockY(), oldPoint.getBlockX()));
        }
    }

    /**
     * Convert the enum to a string and returns it.
     * @return Enum in strong form.
     */
    public static String markersToString() {
        String markers = "";
        markers += ChatColor.RESET;
        markers += ChatColor.BOLD;
        markers += String.join(", ", POSSIBLE_MARKERS);
        markers += ChatColor.RESET;
        return markers;
    }
}

