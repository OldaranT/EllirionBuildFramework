package com.ellirion.buildframework.templateengine;

import org.bukkit.entity.Player;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;

import java.util.HashMap;

public class TemplateManager {

    private static final HashMap<Player, TemplateHologram> SELECTED_HOLOGRAMS = new HashMap<>();
    private static final HashMap<Player, TemplateSession> TEMPLATE_SESSIONS = new HashMap<>();

    public static HashMap<Player, TemplateHologram> getSelectedHolograms() {
        return SELECTED_HOLOGRAMS;
    }

    public static HashMap<Player, TemplateSession> getTemplateSessions() {
        return TEMPLATE_SESSIONS;
    }

    /**
     * Remove all holograms and templates of a player.
     * @param player the player of which to remove all holograms and templates
     */
    public static void removeAll(Player player) {
        SELECTED_HOLOGRAMS.remove(player);
        TEMPLATE_SESSIONS.remove(player);
    }
}
