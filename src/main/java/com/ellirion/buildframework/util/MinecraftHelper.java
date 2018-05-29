package com.ellirion.buildframework.util;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;

public class MinecraftHelper {

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
            Material.BROWN_MUSHROOM,
            };

    private static final HashMap<Material, int[]> MATERIAL_ROTATION_DATA;
    static {
        HashMap<Material, int[]> aMap = new HashMap<>();

        aMap.put(Material.ACACIA_DOOR, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 8, 9, 10, 11, 3, 0, 1, 2, 7, 4, 5, 6, 8, 9, 10, 11
        });

        aMap.put(Material.ACACIA_STAIRS, new int[] {2, 3, 1, 0, 3, 2, 0, 1});

        aMap.put(Material.ACTIVATOR_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 20, 21, 9, 8, 12, 13, 11, 10
        });

        aMap.put(Material.ANVIL, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10
        });

        aMap.put(Material.BED_BLOCK, new int[] {
                1, 2, 3, 0, 1, 2, 3, 0, 9, 10, 11, 8, 9, 10, 11, 8, 3, 0, 1, 2, 3, 0, 1, 2, 11, 8, 9, 10, 11, 8, 9, 10
        });

        aMap.put(Material.BLACK_GLAZED_TERRACOTTA, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });

        aMap.put(Material.BONE_BLOCK, new int[] {
                0, 1, 2, 3, 8, 5, 6, 7, 4, 0, 1, 2, 3, 8, 5, 6, 7, 4
        });

        aMap.put(Material.CHEST, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3});

        aMap.put(Material.DETECTOR_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 20, 21, 9, 8, 12, 13, 11, 10
        });

        aMap.put(Material.DIODE_BLOCK_OFF, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 13, 14, 15, 12, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10, 15, 12, 13,
                14
        });

        aMap.put(Material.DIODE_BLOCK_ON, new int[] {
                1, 2, 3, 0, 5, 6, 7, 4, 9, 10, 11, 8, 13, 14, 15, 12, 3, 0, 1, 2, 7, 4, 5, 6, 11, 8, 9, 10, 15, 12, 13,
                14
        });

        aMap.put(Material.DISPENSER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.DROPPER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.END_ROD, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.ENDER_CHEST, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.FENCE_GATE, new int[] {1, 2, 3, 0, 5, 6, 7, 4, 3, 0, 1, 2, 7, 4, 5, 6});

        aMap.put(Material.FURNACE, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3});

        aMap.put(Material.HAY_BLOCK, new int[] {
                0, 1, 2, 3, 8, 5, 6, 7, 4, 0, 1, 2, 3, 8, 5, 6, 7, 4
        });

        aMap.put(Material.HOPPER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.IRON_DOOR, new int[] {
        });

        aMap.put(Material.IRON_TRAPDOOR, new int[] {
                3, 2, 0, 1, 7, 6, 4, 5, 11, 10, 8, 9, 15, 14, 12, 13, 2, 3, 1, 0, 6, 7, 5, 4, 10, 11, 9, 8, 14, 15, 13,
                12
        });

        aMap.put(Material.JACK_O_LANTERN, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });

        aMap.put(Material.LADDER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3});

        aMap.put(Material.LEVER, new int[] {
                7, 3, 4, 3, 1, 6, 5, 0, 15, 11, 12, 10, 9, 14, 13, 8, 7, 4, 3, 1, 2, 6, 5, 0, 15, 12, 11, 9, 10, 14, 13,
                8
        });

        aMap.put(Material.LOG_2, new int[] {
                0, 1, 2, 3, 8, 9, 6, 7, 4, 5, 10, 11, 12, 13, 8, 9, 16, 17, 4, 5
        });

        aMap.put(Material.LOG, new int[] {
                0, 1, 2, 3, 8, 9, 10, 11, 4, 5, 6, 7, 12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7
        });

        aMap.put(Material.OBSERVER, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.PISTON_BASE, new int[] {
        });

        aMap.put(Material.PISTON_EXTENSION, new int[] {
        });

        aMap.put(Material.PISTON_MOVING_PIECE, new int[] {
        });

        aMap.put(Material.PISTON_STICKY_BASE, new int[] {
        });

        aMap.put(Material.POWERED_RAIL, new int[] {
                1, 0, 5, 4, 2, 3, 6, 7, 9, 8, 13, 12, 10, 11, 1, 0, 4, 5, 3, 2, 20, 21, 9, 8, 12, 13, 11, 10
        });

        aMap.put(Material.PUMPKIN, new int[] {
                1, 2, 3, 0, 3, 0, 1, 2
        });

        aMap.put(Material.QUARTZ_BLOCK, new int[] {0, 1, 2, 4, 3, 5, 6, 7, 3, 4});

        aMap.put(Material.RAILS, new int[] {
                1, 0, 5, 4, 2, 3, 7, 8, 9, 6, 1, 0, 4, 5, 3, 2, 9, 6, 7, 8
        });

        aMap.put(Material.REDSTONE_TORCH_OFF, new int[] {0, 3, 4, 2, 1, 5, 6, 4, 3, 1, 2, 11});

        aMap.put(Material.REDSTONE_TORCH_ON, new int[] {0, 3, 4, 2, 1, 5, 6, 4, 3, 1, 2, 11});

        aMap.put(Material.SIGN_POST, new int[] {
                4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11
        });

        aMap.put(Material.SKULL, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3, 2});

        aMap.put(Material.STANDING_BANNER, new int[] {
                4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                11
        });

        aMap.put(Material.STONE_BUTTON, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});

        aMap.put(Material.TORCH, new int[] {0, 3, 4, 2, 1, 5, 6, 4, 3, 1, 2, 11});

        aMap.put(Material.TRAP_DOOR, new int[] {
                3, 2, 0, 1, 7, 6, 4, 5, 11, 10, 8, 9, 15, 14, 12, 13, 2, 3, 1, 0, 6, 7, 5, 4, 10, 11, 9, 8, 14, 15, 13,
                12
        });

        aMap.put(Material.TRAPPED_CHEST, new int[] {0, 1, 5, 4, 2, 3, 6, 7, 4, 5, 3});

        aMap.put(Material.VINE, new int[] {
                2, 2, 4, 6, 8, 10, 12, 14, 1, 3, 5, 7, 9, 11, 13, 15, 8, 8, 1, 9, 2, 10, 3, 11, 4, 12, 5, 13, 6, 14, 7,
                15
        });

        aMap.put(Material.WALL_BANNER, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 2, 5, 3});

        aMap.put(Material.WALL_SIGN, new int[] {0, 1, 5, 4, 2, 3, 0, 1, 4, 2, 5, 3});

        aMap.put(Material.WOOD_BUTTON, new int[] {0, 3, 4, 2, 1, 5, 0, 4, 3, 1, 2, 5});

        MATERIAL_ROTATION_DATA = aMap;
    }
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
}

