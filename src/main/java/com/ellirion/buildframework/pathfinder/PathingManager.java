package com.ellirion.buildframework.pathfinder;

import org.bukkit.entity.Player;
import com.ellirion.buildframework.pathfinder.model.PathingSession;

import java.util.HashMap;

public class PathingManager {

    private static final HashMap<Player, PathingSession> SESSIONS;

    /**
     * Gets the path for the given player.
     * @param p The player
     * @return The path
     */
    public static PathingSession getSession(Player p) {
        if (!SESSIONS.containsKey(p)) {
            SESSIONS.put(p, new PathingSession(p));
        }
        return SESSIONS.get(p);
    }

    static {
        SESSIONS = new HashMap<>();
    }
}
