package com.ellirion.buildframework.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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

    /**
     * Create undo and redo stacks for the player that logged in.
     * @param event The event of the player joining
     */
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        BuilderManager.createStacks(event.getPlayer());
    }
}
