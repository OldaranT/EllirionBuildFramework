package com.ellirion.buildframework.templateengine.event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

public class TemplateMovementListener implements Listener {

    /**
     * Move or rotate the hologram while using player interaction events.
     * @param event is used to get the tool that the player interacts with.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType() != Material.DIAMOND_SWORD &&
            event.getItem().getType() != Material.DIAMOND_SPADE &&
            event.getItem().getType() != Material.DIAMOND_HOE) {
            return;
        }

        Player player = event.getPlayer();

        TemplateHologram prevHologram = TemplateManager.getSelectedHolograms().get(player);

        if (prevHologram == null) {
            player.sendMessage(ChatColor.RED + "Create a hologram first.");
            return;
        }

        BlockFace blockFace = prevHologram.rotationToFace(player.getLocation().getYaw(),
                                                          player.getLocation().getPitch());
        Template t = prevHologram.getTemplate();
        prevHologram.remove(player);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getItem().getType() == Material.DIAMOND_SPADE) {
                prevHologram.setLocation(new Location(player.getWorld(),
                                                      player.getTargetBlock(null, 35565).getX(),
                                                      player.getTargetBlock(null, 35565).getY(),
                                                      player.getTargetBlock(null, 35565).getZ()));
            } else if (event.getItem().getType() == Material.DIAMOND_HOE) {
                t.rotateTemplate(false);
            } else {
                prevHologram.moveHologram(1, blockFace);
            }
            prevHologram = new TemplateHologram(t, prevHologram.getLocation());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem().getType() == Material.DIAMOND_SPADE) {
                prevHologram.setLocation(new Location(player.getWorld(),
                                                      player.getLocation().getBlockX(),
                                                      player.getLocation().getBlockY(),
                                                      player.getLocation().getBlockZ()));
            } else if (event.getItem().getType() == Material.DIAMOND_HOE) {
                t.rotateTemplate(true);
            } else {
                prevHologram.moveHologram(1, blockFace.getOppositeFace());
            }
            prevHologram = new TemplateHologram(t, prevHologram.getLocation());
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
