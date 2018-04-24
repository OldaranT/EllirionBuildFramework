package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {

    /**
     * List of players with selected templates.
     */
    @Getter
    private static HashMap<Player, Template> selectedTemplates = new HashMap<>();
    @Getter
    private static HashMap<Player, TemplateSession> pointOfTemplate = new HashMap<>();
}
