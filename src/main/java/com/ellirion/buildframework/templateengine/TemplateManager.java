package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {

    @Getter private static HashMap<Player, Template> selectedTemplates = new HashMap<>();
    @Getter private static HashMap<Player, TemplateHologram> selectedHolograms = new HashMap<>();
    @Getter private static HashMap<Player, TemplateSession> pointOfTemplate = new HashMap<>();

    /**
     * Get the template.
     * @param player player of current session.
     * @return template.
     */
    public static Template getTemplate(Player player) {

        TemplateSession ts = getPointOfTemplate().get(player);
        if (ts == null) {
            return getSelectedTemplates().get(player);
        }
        return ts.getTemplate();
    }
}
