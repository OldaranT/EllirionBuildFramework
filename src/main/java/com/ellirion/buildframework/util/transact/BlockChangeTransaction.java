package com.ellirion.buildframework.util.transact;

import org.bukkit.block.Block;
import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.worldhelper.WorldHelper;

public class BlockChangeTransaction extends Transaction {

    private BlockChange before;
    private BlockChange after;

    /**
     * Creates a transaction in which a singular {@link BlockChange} will be executed.
     * This transaction can also be reverted to undo the changes done
     * @param before the state of te block before the transaction was applied
     * @param after the state of the block after the transaction was applied
     */
    public BlockChangeTransaction(final BlockChange before, final BlockChange after) {
        this.before = before;
        this.after = after;
    }

    /**
     * Creates a transaction in which a singular {@link BlockChange} will be executed.
     * This transaction can also be reverted to undo the changes done
     * @param block The original block that needs to be changed
     * @param change the the change that needs to be applied to the block
     */
    public BlockChangeTransaction(final Block block, final BlockChange change) {
        this(new BlockChange(block.getType(), block.getData(), block.getLocation()), change);
    }

    @Override
    protected Promise<Boolean> applier() {
        return WorldHelper.queueBlockChange(after);
    }

    @Override
    protected Promise<Boolean> reverter() {
        return WorldHelper.queueBlockChange(before);
    }
}
