package com.ellirion.buildframework.templateengine;

import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TemplateManager {

    @Getter private static final HashMap<Player, TemplateHologram> SELECTEDHOLOGRAMS = new HashMap<>();
    @Getter private static final HashMap<Player, TemplateSession> TEMPLATESESSIONS = new HashMap<>();
}
