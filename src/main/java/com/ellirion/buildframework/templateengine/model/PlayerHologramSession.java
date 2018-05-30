package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerHologramSession {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
    @Getter @Setter private static Inventory OLD_PLAYER_INVENTORY;

    /**
     * Give a player the tools to control a hologram.
     * @param player player that create's a hologram.
     */
    public static void givePlayerTools(Player player) {
        OLD_PLAYER_INVENTORY = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        OLD_PLAYER_INVENTORY.setContents(player.getInventory().getContents());

        player.getInventory().clear();
        ItemStack diaAxe = new ItemStack(Material.DIAMOND_AXE);
        ItemStack diaShovel = new ItemStack(Material.DIAMOND_SPADE);
        ItemStack diaSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack diaHoe = new ItemStack(Material.DIAMOND_HOE);
        player.getInventory().setItem(0, diaAxe);
        player.getInventory().setItem(3, diaSword);
        player.getInventory().setItem(4, diaShovel);
        player.getInventory().setItem(5, diaHoe);
        player.updateInventory();
    }
}
