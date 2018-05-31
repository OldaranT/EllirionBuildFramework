package com.ellirion.buildframework.templateengine.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

import java.util.Arrays;
import java.util.List;

public class PlayerTemplateGuiSession {

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
        String moveToTool = "Move to Tool";
        ItemStack moveToItem = createTool(Material.DIAMOND_SPADE, moveToTool,
                                          Arrays.asList(moveToTool + ": ", "Left click to move to facing block.",
                                                        "Right click to move to player.", Template.getTemplateTool()),
                                          -1);

        String moveTool = "Move Tool";
        ItemStack moveItem = createTool(Material.DIAMOND_SWORD, moveTool,
                                        Arrays.asList(moveTool + ": ", "Left click to move to away by 1.",
                                                      "Right click to move to closer by 1.",
                                                      Template.getTemplateTool()), -1);
        String rotateTool = "Rotate Tool";
        ItemStack rotateItem = createTool(Material.DIAMOND_HOE, rotateTool,
                                          Arrays.asList(rotateTool + ": ", "Left click to move to facing block.",
                                                        "Right click to move to player.", Template.getTemplateTool()),
                                          -1);
        String metaDataTool = "Meta Data Tool";
        ItemStack metaDataItem = createTool(Material.DIAMOND_AXE, metaDataTool,
                                            Arrays.asList(metaDataTool + ": ", "Left click to +1 metadata value.",
                                                          "Right click to check the current metadata",
                                                          Template.getTemplateTool()), -1);
        String templateConfirmTool = "Template Confirm";
        ItemStack confirmItem = createTool(Material.WOOL, templateConfirmTool,
                                           Arrays.asList(templateConfirmTool + ": ",
                                                         "Left click to confirm the template position.",
                                                         Template.getTemplateTool()), 13);
        String templateCancelTool = "Template Cancel";
        ItemStack cancelItem = createTool(Material.WOOL, templateCancelTool,
                                          Arrays.asList(templateCancelTool + ": ",
                                                        "Left click to cancel template placing.",
                                                        Template.getTemplateTool()), 14);

        player.getInventory().setItem(0, metaDataItem);
        player.getInventory().setItem(3, moveItem);
        player.getInventory().setItem(4, moveToItem);
        player.getInventory().setItem(5, rotateItem);
        player.getInventory().setItem(7, cancelItem);
        player.getInventory().setItem(8, confirmItem);
        player.updateInventory();
    }

    /**
     * Reset player inventory back before he started to use template loader.
     * @param player current player.
     */
    public static void resetInventory(Player player) {
        player.getInventory().setContents(PlayerTemplateGuiSession.getOLD_PLAYER_INVENTORY().getContents());
        player.updateInventory();
    }

    /**
     * Create tools for template engine.
     * @param material that is being used as holder.
     * @param itemName display name of the tool.
     * @param lore the lore that the item needs to have.
     * @param durability durability of the item, if set to -1 it is ignored.
     * @return the item stack for the tool.
     */
    private static ItemStack createTool(Material material, String itemName, List<String> lore, int durability) {
        ItemStack newTool = new ItemStack(material, 1, (short) 1);
        ItemMeta meta = newTool.getItemMeta();

        meta.setUnbreakable(true);
        meta.setDisplayName(itemName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        if (durability != -1) {
            newTool.setDurability((short) durability);
        }

        newTool.setItemMeta(meta);
        return newTool;
    }

    /**
     * To quit the player hologram session.
     * @param templateHologram current selected hologram.
     * @param player current player.
     */
    public static void quitSession(TemplateHologram templateHologram, Player player) {
        templateHologram.remove(player);
        TemplateManager.removeAll(player);
    }
}
