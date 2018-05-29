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
    private static final HashMap<Player, Stack<List<BlockChange>>> PATH_UNDO_STACK = new HashMap<>();
    private static final HashMap<Player, Stack<List<BlockChange>>> PATH_REDO_STACK = new HashMap<>();

    public static HashMap<Player, PathBuilder> getBuilderSessions() {
        return BUILDER_SESSIONS;
    }

    /**
     * Pop the last path from the undo stack and return this.
     * @param player the player whose path to remove
     * @return the last path on the undo stack
     */
    public static List<BlockChange> popUndo(Player player) {
        List<BlockChange> path = PATH_UNDO_STACK.get(player).peek();
        undo(player);
        return path;
    }

    /**
     * Remove all pathbuilder sessions from the given player.
     * @param player the player whose pathbuilder sessions to remove
     */
    public static void removeAll(Player player) {
        BUILDER_SESSIONS.remove(player);
    }

    /**
     * Create the undo and redo stacks for a player.
     * @param player the player for whom to create the undo and redo stacks
     */
    public static void createStacks(Player player) {
        PATH_UNDO_STACK.put(player, new Stack<>());
        PATH_REDO_STACK.put(player, new Stack<>());
    }

    /**
     * Place a path and record the block changes.
     * @param player the player for whom to place the path
     * @param blockChanges the blockchanges of the path
     */
    public static void placePath(Player player, List<BlockChange> blockChanges) {
        PATH_REDO_STACK.get(player).push(blockChanges);

        redo(player);
    }

    /**
     * Undo a path placement.
     * @param player the player whose last path to undo
     */
    public static void undo(Player player) {
        List<BlockChange> changes = PATH_UNDO_STACK.get(player).pop();
        PATH_REDO_STACK.get(player).push(changes);

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
     * @param player the player whose previous path to be redone
     */
    public static void redo(Player player) {
        List<BlockChange> changes = PATH_REDO_STACK.get(player).pop();
        PATH_UNDO_STACK.get(player).push(changes);

        for (BlockChange change : changes) {
            Location loc = change.getLocation();
            Material mat = change.getMatAfter();
            byte data = change.getMetadataAfter();
            loc.getWorld().getBlockAt(loc).setType(mat);
            loc.getWorld().getBlockAt(loc).setData(data);
        }
    }
}
