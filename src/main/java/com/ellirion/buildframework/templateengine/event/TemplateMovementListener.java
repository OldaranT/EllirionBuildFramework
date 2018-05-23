package com.ellirion.buildframework.templateengine.event;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;

public class TemplateMovementListener implements Listener {

    /**
     * @param event when the player interacts with a sword.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().getType() != Material.DIAMOND_SWORD &&
            event.getItem().getType() != Material.DIAMOND_SPADE) {
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
        TemplateSession ts = TemplateManager.getTemplateSessions().get(player);
        prevHologram.remove(player);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getItem().getType() == Material.DIAMOND_SPADE) {
                prevHologram.setLocation(new Location(player.getWorld(), player.getLocation().getBlockX(),
                                                      player.getLocation().getBlockY(),
                                                      player.getLocation().getBlockZ()));
            } else {
                prevHologram.moveHologram(1, blockFace);
            }

            prevHologram = new TemplateHologram(ts.getTemplate(), prevHologram.getLocation());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            prevHologram.moveHologram(1, blockFace.getOppositeFace());
            prevHologram = new TemplateHologram(ts.getTemplate(), prevHologram.getLocation());
        }
        prevHologram.create(player);
    }
}
