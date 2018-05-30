package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.ellirion.buildframework.templateengine.TemplateManager;

public class PlayerHologramSession implements Listener {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
    @Getter @Setter private static Inventory OLD_PLAYER_INVENTORY;
    private Player player;

    /**
     * Constructor.
     * @param p plugin
     * @param player player
     */
    public PlayerHologramSession(final Plugin p, final Player player) {
        this.player = player;
        givePlayerTools();
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
    }

    private void givePlayerTools() {
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

    private void setLocation(TemplateHologram hologram, Location loc) {
        hologram.setLocation(loc);
    }

    private void rotate(Template t, boolean clockwise) {
        t.rotateTemplate(clockwise);
    }

    private void move(TemplateHologram hologram, BlockFace direction, int amount) {
        hologram.moveHologram(amount, direction);
    }

    /**
     * Move or rotate the hologram while using player interaction events.
     * @param event is used to get the tool that the player interacts with.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        // First check if the tool used is a template control tool
        if (!event.getItem().getItemMeta().getLore().get(0).equals("Template controls")) {
            return;
        }

        Player player = event.getPlayer();

        TemplateHologram prevHologram = TemplateManager.getSelectedHolograms().get(player);

        if (prevHologram == null) {
            player.sendMessage(ChatColor.RED + "Create a hologram first.");
            return;
        }

        prevHologram.remove(player);

        // Depending on what tool was used, do a different transformation
        switch (event.getItem().getType()) {
            case DIAMOND_SWORD:
                // Depending on right click/left click we want either targetblock or player location respectively
                Location l = event.getAction().name().contains("LEFT_CLICK")
                             ? player.getTargetBlock(null, 35565).getLocation()
                             : player.getLocation();
                setLocation(prevHologram, l);
                break;
            case DIAMOND_SPADE:
                // Depending on right/left click we want anticlockwise or clockwise rotation respectively
                boolean clockwise = event.getAction().name().contains("LEFT_CLICK") ? false : true;
                rotate(prevHologram.getTemplate(), clockwise);
                break;
            case DIAMOND_HOE:
                // If the right button was clicked, invert the BlockFace, otherwise not
                BlockFace blockFace = prevHologram.rotationToFace(player.getLocation().getYaw(),
                                                                  player.getLocation().getPitch());
                if (event.getAction().name().contains("RIGHT_CLICK")) {
                    blockFace = blockFace.getOppositeFace();
                }
                move(prevHologram, blockFace, 1);
                break;
            default:
                break;
        }

        prevHologram.create(player);
        event.setCancelled(true);
    }

    /**
     * Move or rotate the hologram while using player interaction events.
     * @param event is used to get the tool that the player interacts with.
     */
    @EventHandler
    public void onPlayerInteractWithBlock(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType() != Material.DIAMOND_AXE) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }
        Byte b = block.getData();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            player.sendMessage(
                    ChatColor.BOLD + "Location: " + ChatColor.RESET + block.getX() + " " + block.getY() + " " +
                    block.getZ());
            player.sendMessage(ChatColor.BOLD + "Data: " + ChatColor.RESET + b.toString());
            player.sendMessage(ChatColor.BOLD + "Type: " + ChatColor.RESET + block.getType().toString());
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            try {
                b = (byte) (b + 1);
                block.setData(b);
            } catch (Exception e) {
                b = 0;
                block.setData(b);
            }
        }
        event.setCancelled(true);
    }
}
