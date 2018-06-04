package com.ellirion.buildframework.templateengine.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerTemplateGuiSession implements Listener {

    @Getter @Setter private static Inventory OLD_PLAYER_INVENTORY;
    private static Map<ItemStack, ToolHandler> TOOLS;
    private TemplateHologram hologram;
    private Player player;

    /**
     * Constructor.
     * @param p plugin
     * @param player player
     * @param hologram the TemplateHologram in this session
     */
    public PlayerTemplateGuiSession(final Plugin p, final Player player, final TemplateHologram hologram) {
        this.player = player;
        this.hologram = hologram;
        TOOLS = new HashMap<>();
        this.givePlayerTools();
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
        TemplateManager.getTemplateGuiSessions().put(player, this);
    }

    public static Map<ItemStack, ToolHandler> getTools() {
        return TOOLS;
    }

    /**
     * Give a player the TOOLS to control a hologram.
     */
    private void givePlayerTools() {
        OLD_PLAYER_INVENTORY = Bukkit.getServer().createInventory(null, InventoryType.PLAYER);
        OLD_PLAYER_INVENTORY.setContents(player.getInventory().getContents());

        player.getInventory().clear();

        createTool(Material.DIAMOND_SPADE, "Move to Tool", this::setLocation, -1, 3,
                   "Left click to move to facing block.",
                   "Right click to move to player.");
        createTool(Material.DIAMOND_SWORD, "Move Tool", this::move, -1, 4,
                   "Left click to move to away by 1.",
                   "Right click to move to closer by 1.");
        createTool(Material.DIAMOND_HOE, "Rotate Tool", this::rotate, -1, 5,
                   "Left click to move to facing block.",
                   "Right click to move to player.");
        createTool(Material.WOOL, "Template Confirm", this::confirm, 13, 7,
                   "Left click to confirm the template position.");
        createTool(Material.WOOL, "Template Cancel", this::quit, 14, 8,
                   "Left click to cancel template placing.");

        player.updateInventory();
    }

    /**
     * Reset player inventory back before he started to use template loader.
     */
    private void resetInventory() {
        player.getInventory().setContents(PlayerTemplateGuiSession.getOLD_PLAYER_INVENTORY().getContents());
        player.updateInventory();
    }

    /**
     * Create TOOLS for template engine.
     * @param material define what material the item stack needs to be made of.
     * @param itemName display name of the tool.
     * @param lore the lore that the item needs to have.
     * @param durability durability of the item, if set to -1 it is ignored.
     * @param handler the handler the tool is assigned with.
     */
    private void createTool(Material material, String itemName, ToolHandler handler,
                            int durability, int slot, String... lore) {
        ItemStack newTool = new ItemStack(material, 1, (short) 1);
        ItemMeta meta = newTool.getItemMeta();
        List<String> loreList = new ArrayList<>(Arrays.asList(lore));
        loreList.add(0, itemName + ": ");

        meta.setUnbreakable(true);
        meta.setDisplayName(itemName);
        meta.setLore(loreList);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        if (durability != -1) {
            newTool.setDurability((short) durability);
        }

        newTool.setItemMeta(meta);

        player.getInventory().setItem(slot, newTool);
        TOOLS.put(newTool, handler);
    }

    /**
     * To quit the player hologram session.
     */
    public void quit() {
        hologram.remove(player);
        TemplateManager.removeAll(player);

        // Give old inventory back to the player.
        resetInventory();

        HandlerList.unregisterAll(this);
    }

    /**
     * Move or rotate the hologram while using player interaction events.
     * @param event is used to get the tool that the player interacts with.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        // Check if the item is not null and not in the off-hand slot.
        if (stack == null || event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // Next, we check if the tool used is a template control tool.
        if (!TOOLS.containsKey(stack)) {
            return;
        }

        // Cancel normal event effect.
        event.setCancelled(true);

        // Delete the hologram as it is now, since we're changing it.
        hologram.remove(player);

        // Apply the corresponding transformation. If the handler returns false, it means we do not
        // need to re-create the template hologram.
        if (!TOOLS.get(stack).apply(player, hologram, event.getAction().name().contains("RIGHT_CLICK"))) {
            return;
        }

        // Re-create the template hologram.
        hologram.create(player);
    }

    private boolean setLocation(Player player, TemplateHologram hologram, boolean isRightHand) {
        // Depending on right click/left click we want either target block or player location respectively
        Location targetLoc = player.getTargetBlock(null, 35565).getLocation();
        Location playerLoc = player.getLocation();
        Location hologramLoc = !isRightHand
                               ? new Point(targetLoc).floor().toLocation(player.getWorld())
                               : new Point(playerLoc).floor().toLocation(player.getWorld());
        hologram.setLocation(hologramLoc);
        return true;
    }

    private boolean rotate(Player player, TemplateHologram hologram, boolean isRightHand) {
        hologram.getTemplate().rotateTemplate(isRightHand);
        this.hologram = new TemplateHologram(hologram.getTemplate(), hologram.getLocation());

        if (isRightHand) {
            player.sendMessage("Template has been rotated clockwise.");
            return true;
        }
        player.sendMessage("Template has been rotated counter clockwise.");
        return true;
    }

    private boolean move(Player player, TemplateHologram hologram, boolean isRightClick) {
        // If the right button was clicked, invert the BlockFace, otherwise not
        BlockFace blockFace = hologram.rotationToFace(player.getLocation().getYaw(),
                                                      player.getLocation().getPitch());
        if (isRightClick) {
            blockFace = blockFace.getOppositeFace();
        }
        hologram.moveHologram(1, blockFace);
        return true;
    }

    private boolean confirm(Player player, TemplateHologram hologram, boolean isRightClick) {
        // Place the template
        Template t = hologram.getTemplate();
        t.putTemplateInWorld(hologram.getLocation());
        quit();
        return false;
    }

    private boolean quit(Player player, TemplateHologram hologram, boolean isRightClick) {
        quit();
        return false;
    }

    private interface ToolHandler {

        boolean apply(Player player, TemplateHologram hologram, boolean isRightHand);
    }
}
