package com.ellirion.buildframework.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.ellirion.buildframework.pathbuilder.BuilderManager;
import com.ellirion.buildframework.templateengine.TemplateManager;

public class EventListener implements Listener {

    /**
     * Add functionality to logout event.
     * @param event The event of the player logging out
     */
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        TemplateManager.removeAll(event.getPlayer());
        BuilderManager.removeAll(event.getPlayer());
    }
}
