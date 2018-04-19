package com.ellirion.buildframework.util;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldEditHelper {

    /**
     * Get the worldedit selection of a player.
     * @param player Player that you need the selection from.
     * @return Worldedit selection.
     */
    public static Selection getSelection(Player player) {
        WorldEditPlugin worldEditPlugin = null;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage("Error with region undoing! Error: WorldEdit is null.");
        }
        Selection sel = worldEditPlugin.getSelection(player);

        return sel;
    }
}
