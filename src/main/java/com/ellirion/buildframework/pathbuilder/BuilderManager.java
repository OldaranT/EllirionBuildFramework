package com.ellirion.buildframework.pathbuilder;

import org.bukkit.entity.Player;
import com.ellirion.buildframework.pathbuilder.model.BlockChange;
import com.ellirion.buildframework.pathbuilder.model.PathBuilder;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.transact.SequenceTransaction;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BuilderManager {

    private static final HashMap<Player, PathBuilder> BUILDER_SESSIONS = new HashMap<>();

    public static HashMap<Player, PathBuilder> getBuilderSessions() {
        return BUILDER_SESSIONS;
    }

    /**
     * Place a path and record the block changes.
     * @param player the player for whom to place the path
     * @param blockChanges the blockchanges of the path
     */
    public static void placePath(Player player, List<BlockChange> blockChanges) {
        List<Transaction> changes = new LinkedList<>();

        // Make transaction of all BlockChange
        for (BlockChange change : blockChanges) {
            changes.add(WorldHelper.setBlock(change.getLocation(), change.getMatAfter(), change.getMetadataAfter()));
        }

        TransactionManager.addDoneTransaction(player,
                                              new SequenceTransaction(true, changes.toArray(new Transaction[0])));
    }
}
