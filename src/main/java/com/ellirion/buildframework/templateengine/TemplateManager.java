package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {

    /**
     * Map of players with selected templates.
     */
    @Getter private static HashMap<Player, Template> selectedTemplates = new HashMap<>();

    /**
     * Map of players with selected template holograms.
     */
    @Getter private static HashMap<Player, TemplateHologram> selectedHolograms = new HashMap<>();
}
