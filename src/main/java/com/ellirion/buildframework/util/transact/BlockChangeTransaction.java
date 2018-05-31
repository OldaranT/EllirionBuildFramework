package com.ellirion.buildframework.util.transact;

import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.async.Promise;

import java.util.List;

public class BlockChangeTransaction extends Transaction {

    private List<BlockChange> before;
    private List<BlockChange> after;

    /**
     * Creates a transaction in which multiple {@link BlockChange} will be executed.
     * This transaction can also be reverted to undo the changes done
     * @param before the state of te block before the transaction was applied
     * @param after the state of the block after the transaction was applied
     */
    public BlockChangeTransaction(final BlockChange before, final BlockChange after) {
        this.before.add(before);
        this.after.add(after);
    }

    /**
     * Creates a transaction in which a multiple {@link BlockChange} will be executed.
     * This transaction can also be reverted to undo the changes done
     * @param block The original block that needs to be changed
     * @param change the the change that needs to be applied to the block
     */
    public BlockChangeTransaction(final Block block, final BlockChange change) {
        this(new BlockChange(block.getType(), block.getData(), block.getLocation()), change);
    }

    @Override
    protected Promise<Boolean> applier() {
        return Promise.all(WorldHelper.queueBlockChanges(after).toArray(new Promise[0])).then(t -> true);
    }

    @Override
    protected Promise<Boolean> reverter() {
        return Promise.all(WorldHelper.queueBlockChanges(before).toArray(new Promise[0])).then(t -> true);
    }
}
