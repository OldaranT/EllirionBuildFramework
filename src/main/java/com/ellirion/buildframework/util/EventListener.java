package com.ellirion.buildframework.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.util.PlayerTemplateGuiSession;

public class EventListener implements Listener {

    /**
     * Add functionality to logout event.
     * @param event The event of the player logging out
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        TemplateManager.removeAll(event.getPlayer());
        PlayerTemplateGuiSession.resetInventory(event.getPlayer());
    }

    /**
     * Add functionality to player drop event.
     * @param event The event of the player dropping a item.
     */
    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent event) {

        if (event.getItemDrop() == null) {
            return;
        }

        if (event.getItemDrop().getItemStack().getItemMeta().getLore().contains(Template.getTemplateTool())) {
            event.setCancelled(true);
        }
    }
}
