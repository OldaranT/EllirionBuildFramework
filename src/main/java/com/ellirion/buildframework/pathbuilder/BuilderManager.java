package com.ellirion.buildframework.pathbuilder;

import lombok.Getter;
import org.bukkit.entity.Player;

import com.ellirion.buildframework.pathbuilder.model.PathBuilder;

import java.util.HashMap;

public class BuilderManager {

    @Getter private static HashMap<Player, PathBuilder> builderSessions = new HashMap<>();
}
