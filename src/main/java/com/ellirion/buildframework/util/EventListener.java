package com.ellirion.buildframework.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.util.PlayerTemplateGuiSession;

public class EventListener implements Listener {

    /**
     * Add functionality to logout event.
     * @param event The event of the player logging out
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        TemplateManager.removeAll(event.getPlayer());
    }

    /**
     * Prevent the player to drop any template TOOLS.
     * @param event The event of the player dropping a item.
     */
    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent event) {

        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (event.getItemDrop() == null) {
            return;
        }

        // If the item is a template tool return and do nothing.
        if (PlayerTemplateGuiSession.getTools().containsKey(itemStack)) {
            event.setCancelled(true);
            return;
        }
    }
}
