package com.ellirion.buildframework.util;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MinecraftHelper {

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
            Material.PUMPKIN_STEM,
            Material.WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA,
            Material.STATIONARY_WATER
    };

    private static final Material[] TO_ROTATE = new Material[] {

            // Building blocks
            Material.LOG_2,
            Material.LOG,
            Material.BONE_BLOCK,
            Material.HAY_BLOCK,
            Material.JACK_O_LANTERN,
            Material.PUMPKIN,
            Material.QUARTZ_BLOCK,

            // Decoration blocks
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

            // Redstone
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

            // Fence Gate
            Material.FENCE_GATE,
            Material.ACACIA_FENCE,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,

            // Rail
            Material.ACTIVATOR_RAIL,
            Material.DETECTOR_RAIL,
            Material.RAILS,
            Material.POWERED_RAIL,

            // Door
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR_BLOCK,
            Material.JUNGLE_DOOR,
            Material.WOOD_DOOR,
            Material.WOODEN_DOOR,
            Material.SPRUCE_DOOR,

            // Terracotta
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

            // Stairs
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
            Material.WOOD_STAIRS
    };

    private static final Material[] NON_ANCHOR_POINTS = new Material[] {
            Material.AIR,
            Material.TORCH,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.SAPLING,
            Material.RAILS,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.ACTIVATOR_RAIL,
            Material.CACTUS,
            Material.DEAD_BUSH,
            Material.VINE,
            Material.LONG_GRASS,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.SUGAR_CANE_BLOCK,
            Material.MELON_STEM,
            Material.PUMPKIN_STEM,
            Material.CROPS,
            Material.BEETROOT_BLOCK,
            Material.CARROT,
            Material.POTATO,
            Material.TRIPWIRE,
            Material.TRIPWIRE_HOOK,
            Material.LADDER,
            Material.LEVER,
            Material.WOOD_BUTTON,
            Material.STONE_BUTTON,
            Material.FLOWER_POT,
            Material.SKULL,
            Material.END_ROD,
            Material.CARPET,
            Material.PAINTING,
            Material.FENCE,
            Material.SNOW,
            Material.WATER,
            Material.LAVA,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM
    };

    private static final HashMap<Material, List<Integer>> SPECIAL_SNOWFLAKE_DATA = new HashMap<>();
    static {
        SPECIAL_SNOWFLAKE_DATA.put(Material.TORCH, Arrays.asList(0, 5));
        SPECIAL_SNOWFLAKE_DATA.put(Material.REDSTONE_TORCH_OFF, Arrays.asList(0, 5));
        SPECIAL_SNOWFLAKE_DATA.put(Material.REDSTONE_TORCH_ON, Arrays.asList(0, 5));
        SPECIAL_SNOWFLAKE_DATA.put(Material.STONE_BUTTON, Arrays.asList(5));
        SPECIAL_SNOWFLAKE_DATA.put(Material.WOOD_BUTTON, Arrays.asList(5));
        SPECIAL_SNOWFLAKE_DATA.put(Material.LEVER, Arrays.asList(5, 6));
    }

    private static final HashMap<Material, int[]> MATERIAL_ROTATION_DATA = new HashMap<>();
    static {
        MATERIAL_ROTATION_DATA.put(Material.ACACIA_DOOR, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 8, 9, 10, 11, 3, 0, 1, 2, 7, 4, 5, 6, 8, 9, 10, 11
        });
        MATERIAL_ROTATION_DATA.put(Material.ACACIA_STAIRS, new int[] {2, 3, 1, 0, 6, 7, 5, 4, 3, 2, 0, 1, 7, 6, 4, 5});
        MATERIAL_ROTATION_DATA.put(Material.ACTIVATOR_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 6, 7, 9, 8, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.ANVIL, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.BED_BLOCK, new int[] {
                1, 2, 3, 0, 1, 2, 3, 0, 9, 10, 11, 8, 9, 10, 11, 8, 3, 0, 1, 2, 3, 0, 1, 2, 11, 8, 9, 10, 11, 8, 9, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.BLACK_GLAZED_TERRACOTTA, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });
        MATERIAL_ROTATION_DATA.put(Material.BONE_BLOCK, new int[] {
                0, 1, 2, 3, 8, 5, 6, 7, 4, 0, 1, 2, 3, 8, 5, 6, 7, 4
        });
        MATERIAL_ROTATION_DATA.put(Material.CHEST, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.DETECTOR_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 6, 7, 9, 8, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.DIODE_BLOCK_OFF, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 13, 14, 15, 12, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10, 15, 12, 13,
                14
        });
        MATERIAL_ROTATION_DATA.put(Material.DIODE_BLOCK_ON, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 13, 14, 15, 12, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10, 15, 12, 13,
                14
        });
        MATERIAL_ROTATION_DATA.put(Material.DISPENSER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.DROPPER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.END_ROD, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.ENDER_CHEST, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.FENCE_GATE, new int[] {1, 2, 3, 0, 5, 6, 7, 4, 3, 0, 1, 2, 7, 4, 5, 6});
        MATERIAL_ROTATION_DATA.put(Material.FURNACE, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.HAY_BLOCK, new int[] {
                0, 1, 2, 3, 8, 5, 6, 7, 4, 0, 1, 2, 3, 8, 5, 6, 7, 4
        });
        MATERIAL_ROTATION_DATA.put(Material.HOPPER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.IRON_TRAPDOOR, new int[] {
                3, 2, 0, 1, 7, 6, 4, 5, 11, 10, 8, 9, 15, 14, 12, 13, 2, 3, 1, 0, 6, 7, 5, 4, 10, 11, 9, 8, 14, 15, 13,
                12
        });
        MATERIAL_ROTATION_DATA.put(Material.JACK_O_LANTERN, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });
        MATERIAL_ROTATION_DATA.put(Material.LADDER, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.LEVER, new int[] {
                7, 3, 4, 3, 1, 6, 5, 0, 15, 11, 12, 10, 9, 14, 13, 8, 7, 4, 3, 1, 2, 6, 5, 0, 15, 12, 11, 9, 10, 14, 13,
                8
        });
        MATERIAL_ROTATION_DATA.put(Material.LOG_2,
                                   new int[] {0, 1, 2, 3, 8, 9, 6, 7, 4, 5, 0, 1, 2, 3, 8, 9, 6, 7, 4, 5});
        MATERIAL_ROTATION_DATA.put(Material.LOG, new int[] {
                0, 1, 2, 3, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3, 8, 9, 10, 11, 4, 5, 6, 7
        });
        MATERIAL_ROTATION_DATA.put(Material.OBSERVER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.PISTON_BASE, new int[] {
                0, 1, 5, 4, 2, 3, 6, 7, 8, 9, 13, 12, 10, 11, 0, 1, 4, 5, 3, 2, 6, 7, 8, 9, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.PISTON_EXTENSION, new int[] {
                0, 1, 5, 4, 2, 3, 6, 7, 8, 9, 13, 12, 10, 11, 0, 1, 4, 5, 3, 2, 6, 7, 8, 9, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.PISTON_STICKY_BASE, new int[] {
                0, 1, 5, 4, 2, 3, 6, 7, 8, 9, 13, 12, 10, 11, 0, 1, 4, 5, 3, 2, 6, 7, 8, 9, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.POWERED_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 6, 7, 9, 8, 12, 13, 11, 10
        });
        MATERIAL_ROTATION_DATA.put(Material.PUMPKIN, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });
        MATERIAL_ROTATION_DATA.put(Material.QUARTZ_BLOCK, new int[] {0, 1, 2, 4, 3, 0, 1, 2, 4, 3});
        MATERIAL_ROTATION_DATA.put(Material.RAILS, new int[] {
                1, 0, 5, 4, 2, 3, 7, 8, 9, 6, 1, 0, 4, 5, 3, 2, 9, 6, 7, 8
        });
        MATERIAL_ROTATION_DATA.put(Material.REDSTONE_TORCH_OFF, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});
        MATERIAL_ROTATION_DATA.put(Material.REDSTONE_TORCH_ON, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});
        MATERIAL_ROTATION_DATA.put(Material.SIGN_POST, new int[] {
                4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11
        });
        MATERIAL_ROTATION_DATA.put(Material.SKULL, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.STANDING_BANNER, new int[] {
                4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11
        });
        MATERIAL_ROTATION_DATA.put(Material.STONE_BUTTON, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});
        MATERIAL_ROTATION_DATA.put(Material.TORCH, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});
        MATERIAL_ROTATION_DATA.put(Material.TRAP_DOOR, new int[] {
                3, 2, 0, 1, 7, 6, 4, 5, 11, 10, 8, 9, 15, 14, 12, 13, 2, 3, 1, 0, 6, 7, 5, 4, 10, 11, 9, 8, 14, 15, 13,
                12
        });
        MATERIAL_ROTATION_DATA.put(Material.TRAPPED_CHEST, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.VINE, new int[] {
                2, 2, 4, 6, 8, 10, 12, 14, 1, 3, 5, 7, 9, 11, 13, 15, 8, 8, 1, 9, 2, 10, 3, 11, 4, 12, 5, 13, 6, 14, 7,
                15
        });
        MATERIAL_ROTATION_DATA.put(Material.WALL_BANNER, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.WALL_SIGN, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 5, 3, 2});
        MATERIAL_ROTATION_DATA.put(Material.WOOD_BUTTON, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});
    }

    private static final List<Material> PATH_FINDER_SOLIDS = Arrays.asList(
            Material.STONE,
            Material.GRASS,
            Material.DIRT,
            Material.COBBLESTONE,
            Material.WOOD,
            Material.BEDROCK,
            Material.SAND,
            Material.GRAVEL,
            Material.GOLD_ORE,
            Material.IRON_ORE,
            Material.COAL_ORE,
            Material.LOG,
            Material.LEAVES,
            Material.SPONGE,
            Material.GLASS,
            Material.LAPIS_ORE,
            Material.LAPIS_BLOCK,
            Material.SANDSTONE,
            Material.WOOL,
            Material.GOLD_BLOCK,
            Material.IRON_BLOCK,
            Material.DOUBLE_STEP,
            Material.STEP,
            Material.BRICK,
            Material.MOSSY_COBBLESTONE,
            Material.OBSIDIAN,
            Material.WOOD_STAIRS,
            Material.DIAMOND_ORE,
            Material.DIAMOND_BLOCK,
            Material.SOIL,
            Material.COBBLESTONE_STAIRS,
            Material.ICE,
            Material.SNOW_BLOCK,
            Material.CLAY,
            Material.NETHERRACK,
            Material.SOUL_SAND,
            Material.GLOWSTONE,
            Material.STAINED_GLASS,
            Material.SMOOTH_BRICK,
            Material.BRICK_STAIRS,
            Material.SMOOTH_STAIRS,
            Material.MYCEL,
            Material.NETHER_BRICK,
            Material.NETHER_BRICK_STAIRS,
            Material.WOOD_DOUBLE_STEP,
            Material.WOOD_STEP,
            Material.SANDSTONE_STAIRS,
            Material.EMERALD_ORE,
            Material.EMERALD_BLOCK,
            Material.SPRUCE_WOOD_STAIRS,
            Material.BIRCH_WOOD_STAIRS,
            Material.JUNGLE_WOOD_STAIRS,
            Material.REDSTONE_BLOCK,
            Material.QUARTZ_ORE,
            Material.HOPPER,
            Material.QUARTZ_BLOCK,
            Material.QUARTZ_STAIRS,
            Material.STAINED_CLAY,
            Material.LEAVES_2,
            Material.LOG_2,
            Material.ACACIA_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.SLIME_BLOCK,
            Material.BARRIER,
            Material.PRISMARINE,
            Material.SEA_LANTERN,
            Material.HAY_BLOCK,
            Material.HARD_CLAY,
            Material.COAL_BLOCK,
            Material.PACKED_ICE,
            Material.RED_SANDSTONE,
            Material.RED_SANDSTONE_STAIRS,
            Material.DOUBLE_STONE_SLAB2,
            Material.STONE_SLAB2,
            Material.PURPUR_PILLAR,
            Material.PURPUR_STAIRS,
            Material.PURPUR_DOUBLE_SLAB,
            Material.PURPUR_SLAB,
            Material.END_BRICKS,
            Material.GRASS_PATH,
            Material.FROSTED_ICE,
            Material.MAGMA,
            Material.NETHER_WART_BLOCK,
            Material.RED_NETHER_BRICK,
            Material.BONE_BLOCK,
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA,
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.SILVER_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.RED_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA,
            Material.CONCRETE,
            Material.CONCRETE_POWDER
    );

    /**
     * A method to determine whether a certain material can be used as an anchor point for path supports.
     * @param mat the material to check
     * @return whether the given material can be used as an anchor point
     */
    public static boolean isAnchorPoint(Material mat) {
        return !Arrays.asList(NON_ANCHOR_POINTS).contains(mat);
    }

    /**
     * A method to determine whether a certain material is a door or not.
     * @param mat the material to check
     * @return if the material is a door return true.
     */
    public static boolean isDoor(Material mat) {
        return mat.toString().contains("DOOR") && !mat.toString().contains("TRAP");
    }

    /**
     * Check if their is a specific combination of material and metadata.
     * @param mat current material to check.
     * @param meta current metadata to check.
     * @return true if their is a specific occasion.
     */
    public static boolean isSpecialSnowflake(Material mat, int meta) {
        List<Integer> list = SPECIAL_SNOWFLAKE_DATA.get(mat);
        if (list == null) {
            return false;
        }
        return list.contains(meta);
    }

    /**
     * A method to determine whether a certain material is a stair or not.
     * @param mat the material to check
     * @return if the material is a stair return true.
     */
    public static boolean isStair(Material mat) {
        return mat.toString().contains("STAIRS");
    }

    /**
     * A method to determine whether a certain material is a fence gate or not.
     * @param mat the material to check
     * @return if the material is a fence gate return true.
     */
    public static boolean isFenceGate(Material mat) {
        return mat.toString().contains("FENCE_GATE");
    }

    /**
     * Return a data value of a rotated metadata.
     * @param material of the block that needs to be rotated
     * @param metaData the metadata before the block has been rotated
     * @param direction true is clockwise false is counter clockwise.
     * @return value of new metadata.
     */
    public static int getMaterialRotationData(Material material, int metaData, boolean direction) {
        int[] data = MATERIAL_ROTATION_DATA.get(material);
        if (direction) {
            return data[metaData];
        }
        return data[metaData + data.length / 2];
    }

    public static Material[] getPlaceLate() {
        return PLACE_LATE.clone();
    }

    public static Material[] getToRotate() {
        return TO_ROTATE.clone();
    }

    /**
     * Check if the material is 'solid' for the path finder.
     * @param m the material to check
     * @return whether the given material is solid or not
     */
    public static boolean isPathSolid(Material m) {
        return PATH_FINDER_SOLIDS.contains(m);
    }
}

