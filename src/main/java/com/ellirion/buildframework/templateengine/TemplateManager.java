package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {

    private static final HashMap<Player, TemplateHologram> SELECTEDHOLOGRAMS = new HashMap<>();
    private static final HashMap<Player, TemplateSession> TEMPLATESESSIONS = new HashMap<>();

    /**
     * list of selected hologram for each  player
     * @return selected holograms.
     */
    public static HashMap<Player, TemplateHologram> getSelectedHolograms() {
        return SELECTEDHOLOGRAMS;
    }

    /**
     * List of template session for each player.
     * @return template sessions.
     */
    public static HashMap<Player, TemplateSession> getTemplateSessions() {
        return TEMPLATESESSIONS;
    }

    /**
     * Remove all holograms and templates of a player.
     * @param player the player of which to remove all holograms and templates
     */
    public static void removeAll(Player player) {
        SELECTEDHOLOGRAMS.remove(player);
        TEMPLATESESSIONS.remove(player);
    }
}
