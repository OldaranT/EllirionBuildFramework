package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.Template;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {
    /**
     * List of players with selected templates
     */
    public static final HashMap<Player, Template> selectedTemplates = new HashMap<Player, Template>();
}
