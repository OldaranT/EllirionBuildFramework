package com.ellirion.buildframework.pathbuilder;

import org.bukkit.entity.Player;

import com.ellirion.buildframework.pathbuilder.model.PathBuilder;

import java.util.HashMap;

public class BuilderManager {

    private static final HashMap<Player, PathBuilder> BUILDER_SESSIONS = new HashMap<>();

    public static HashMap<Player, PathBuilder> getBuilderSessions() {
        return BUILDER_SESSIONS;
    }
}
