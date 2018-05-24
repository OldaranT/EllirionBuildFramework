package com.ellirion.buildframework.pathbuilder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.pathbuilder.model.PathBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class BuilderManager {

    private static final HashMap<Player, PathBuilder> BUILDER_SESSIONS = new HashMap<>();
    private static final Stack<List<BlockChange>> PATH_UNDO_STACK = new Stack<>();
    private static final Stack<List<BlockChange>> PATH_REDO_STACK = new Stack<>();

    public static HashMap<Player, PathBuilder> getBuilderSessions() {
        return BUILDER_SESSIONS;
    }

    /**
     * Remove all pathbuilder sessions from the given player.
     * @param player the player whose pathbuilder sessions to remove
     */
    public static void removeAll(Player player) {
        BUILDER_SESSIONS.remove(player);
    }

    /**
     * Place a path and record the block changes.
     * @param blockChanges the blockchanges og the path
     */
    public static void placePath(List<BlockChange> blockChanges) {
        PATH_UNDO_STACK.push(blockChanges);

        for (BlockChange change : blockChanges) {
            Location loc = change.getLocation();
            Material mat = change.getMatAfter();
            byte data = change.getMetadataAfter();
            loc.getWorld().getBlockAt(loc).setType(mat);
            loc.getWorld().getBlockAt(loc).setData(data);
        }
    }

    /**
     * Undo a path placement.
     */
    public static void undo() {
        List<BlockChange> changes = PATH_UNDO_STACK.pop();
        PATH_REDO_STACK.push(changes);

        for (BlockChange change : changes) {
            Location loc = change.getLocation();
            Material mat = change.getMatBefore();
            byte data = change.getMetadataBefore();
            loc.getWorld().getBlockAt(loc).setType(mat);
            loc.getWorld().getBlockAt(loc).setData(data);
        }
    }

    /**
     * Redo an undone path placement.
     */
    public static void redo() {
        List<BlockChange> changes = PATH_REDO_STACK.pop();
        PATH_UNDO_STACK.push(changes);

        for (BlockChange change : changes) {
            Location loc = change.getLocation();
            Material mat = change.getMatAfter();
            byte data = change.getMetadataAfter();
            loc.getWorld().getBlockAt(loc).setType(mat);
            loc.getWorld().getBlockAt(loc).setData(data);
        }
    }
}
