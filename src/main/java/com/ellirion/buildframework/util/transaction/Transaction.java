package com.ellirion.buildframework.util.transaction;

import lombok.Getter;
import com.ellirion.buildframework.util.async.Counter;
import com.ellirion.buildframework.util.async.Promise;

import java.util.function.Supplier;

public abstract class Transaction {

    private Counter latch;
    @Getter private boolean applied;

    /**
     * Construct a new Transaction.
     */
    protected Transaction() {
        this.latch = new Counter(0);
        this.applied = false;
    }

    /**
     * Applies this Transaction and returns a Promise thereof.
     * @return The Promise for the result of this operation.
     */
    protected abstract Promise<Boolean> applier();

    /**
     * Applies this Transaction and returns a Promise thereof.
     * @return The Promise for the result of this operation.
     */
    protected abstract Promise<Boolean> reverter();

    /**
     * Apply this transaction.
     * @return Whether the operation succeeded or not.
     */
    public final Promise<Boolean> apply() {
        return perform(this::applier, true);
    }

    /**
     * Revert this transaction.
     * @return Whether the operation succeeded or not.
     */
    public final Promise<Boolean> revert() {
        return perform(this::reverter, false);
    }

    private Promise<Boolean> perform(Supplier<Promise<Boolean>> supplier, boolean becomesApplied) {
        // If someone forgot to set the applier or reverter functinos, throw an exception.
        if (supplier == null) {
            throw new IllegalStateException("Cannot perform transaction operation without supplier");
        }

        // Make sure we don't run twice simultaneously. It might seem that the fact
        // that the apply() and revert() methods are synchronized prevents this, but that's
        // not necessarily the case since the Promise may go unresolved for an extended period of time.
        latch.awaitAndPerform(0, () -> {
            // After we've waited for the latch to reach zero, we can now
            // safely increment it *without yielding the synchronized block*.
            latch.increment();
        });

        // Make sure we don't try to apply when we're already applied, or
        // try to revert when we're not applied.
        if (applied == becomesApplied) {
            throw new RuntimeException("Cannot apply when applied, or revert when not applied");
        }

        // Get a Promise produced by our apply function.
        Promise<Boolean> p = supplier.get();

        // Once the Promise finishes in any way, we set pending to false and
        // applied to whatever boolean value was passed as becomesApplied.
        p.always(() -> {
            // Acquire the lock, and then decrement it. This prevents another thread
            // from changing the value between our check and assignment.
            latch.perform(() -> {
                applied = becomesApplied;
                latch.decrement();
            });
        });

        // And we return the Promise to the callee.
        return p;
    }

    public boolean isPending() {
        return latch.get() > 0;
    }
}
