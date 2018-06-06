package com.ellirion.buildframework.util;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import com.ellirion.buildframework.templateengine.TemplateManager;

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
     * Prevent chunk unloading as long as the chunk was recently accessed by the WorldHelper.
     * @param event The event of the chunk being unloaded
     */
    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        Chunk c = event.getChunk();

        // If this Chunk is active, prevent unloading.
        if (WorldHelper.isChunkActive(c)) {
            event.setCancelled(true);
            return;
        }

        // If we did unload the chunk, forget about it.
        WorldHelper.markChunkInactive(c);
    }
}
