package com.ellirion.buildframework.util.transaction;

import com.ellirion.buildframework.model.BlockChange;
import com.ellirion.buildframework.util.worldhelper.WorldHelper;
import com.ellirion.buildframework.util.async.Promise;

public class BlockChangeTransaction extends Transaction {

    private BlockChange before;
    private BlockChange after;

    /**
     * Creates a transaction in which a singular blockChange will be executed.
     * @param before the state of te block before the Transaction was applied
     * @param after the state of the block after the Transaction was applied
     */
    public BlockChangeTransaction(final BlockChange before, final BlockChange after) {
        this.before = before;
        this.after = after;
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
