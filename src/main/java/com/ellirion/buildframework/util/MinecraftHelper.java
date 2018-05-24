package com.ellirion.buildframework.util;

import org.bukkit.Material;

import java.util.Arrays;

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

    /**
     * A method to determine whether a certain material can be used as an anchor point for path supports.
     * @param mat the material to check
     * @return whether the given material can be used as an anchor point
     */
    public static boolean isAnchorPoint(Material mat) {
        return !Arrays.asList(NON_ANCHOR_POINTS).contains(mat);
    }
}
