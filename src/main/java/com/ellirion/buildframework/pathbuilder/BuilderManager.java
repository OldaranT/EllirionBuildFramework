package com.ellirion.buildframework.pathbuilder;

import net.minecraft.server.v1_12_R1.Tuple;
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
    private static final Stack<Tuple<List<BlockChange>, PathBuilder>> PATH_UNDO_STACK = new Stack<>();
    private static final Stack<Tuple<List<BlockChange>, PathBuilder>> PATH_REDO_STACK = new Stack<>();

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
     * @param blockChanges the blockchanges of the path
     * @param pathbuilder the pathbuilder that made this path
     */
    public static void placePath(List<BlockChange> blockChanges, PathBuilder pathbuilder) {
        //Sort the list and put the supportType blockChanges first so that these will be overridden by the path

        PATH_REDO_STACK.push(new Tuple<>(blockChanges, pathbuilder));

        redo();
        //        for (BlockChange change : blockChanges) {
        //            Location loc = change.getLocation();
        //            Material mat = change.getMatAfter();
        //            byte data = change.getMetadataAfter();
        //            loc.getWorld().getBlockAt(loc).setType(mat);
        //            loc.getWorld().getBlockAt(loc).setData(data);
        //        }
    }

    /**
     * Undo a path placement.
     */
    public static void undo() {
        Tuple<List<BlockChange>, PathBuilder> tuple = PATH_UNDO_STACK.pop();
        List<BlockChange> changes = tuple.a();
        PATH_REDO_STACK.push(tuple);

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
        Tuple<List<BlockChange>, PathBuilder> tuple = PATH_REDO_STACK.pop();
        List<BlockChange> changes = tuple.a();
        PATH_UNDO_STACK.push(tuple);

        for (BlockChange change : changes) {
            Location loc = change.getLocation();
            Material mat = change.getMatAfter();
            byte data = change.getMetadataAfter();
            loc.getWorld().getBlockAt(loc).setType(mat);
            loc.getWorld().getBlockAt(loc).setData(data);
        }
    }
}
