package com.ellirion.buildframework.pathfinder.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.PathingManager;

public class PathingListener implements Listener {

    /**
     * @param event The event to handle
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // If the item isn't a stick, ignore,
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.STICK) {
            return;
        }

        Block block = event.getClickedBlock();

        // If no block was selected, ignore.
        if (block == null) {
            block = event.getPlayer().getTargetBlock(null, 35565);
        }

        //

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            PathingManager.getSession(event.getPlayer())
                    .setPoint1(new Point(block.getLocation()));
            event.getPlayer().sendMessage(ChatColor.GREEN + "Point 1 set");
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            PathingManager.getSession(event.getPlayer())
                    .setPoint2(new Point(block.getLocation()));
            event.getPlayer().sendMessage(ChatColor.GREEN + "Point 2 set");
            event.setCancelled(true);
        }
    }

    /**
     * @param event The event to handle
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getItemOnCursor().getType() == Material.STICK) {
            event.setCancelled(true);
        }
    }
}
