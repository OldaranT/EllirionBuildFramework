package com.ellirion.buildframework.templateengine;

import org.bukkit.entity.Player;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;

import java.util.HashMap;

public class TemplateManager {

    private static final HashMap<Player, TemplateHologram> SELECTED_HOLOGRAMS = new HashMap<>();
    private static final HashMap<Player, TemplateSession> TEMPLATE_SESSIONS = new HashMap<>();

    public static HashMap<Player, TemplateHologram> getSelectedHologram() {
        return SELECTED_HOLOGRAMS;
    }

    public static HashMap<Player, TemplateSession> getTemplateSessions() {
        return TEMPLATE_SESSIONS;
    }
}
