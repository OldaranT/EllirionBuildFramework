package com.ellirion.buildframework.pathfinder;

import org.bukkit.entity.Player;
import com.ellirion.buildframework.pathfinder.model.PathingSession;

import java.util.HashMap;

public class PathingManager {

    private static HashMap<Player, PathingSession> sessions;

    /**
     * Gets the path for the given player.
     * @param p The player
     * @return The path
     */
    public static PathingSession getSession(Player p) {
        if (!sessions.containsKey(p)) {
            sessions.put(p, new PathingSession(p));
        }
        return sessions.get(p);
    }

    static {
        sessions = new HashMap<>();
    }
}
