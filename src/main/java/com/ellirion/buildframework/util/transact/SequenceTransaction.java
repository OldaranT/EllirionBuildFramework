package com.ellirion.buildframework.util.transact;

import lombok.Getter;
import com.ellirion.buildframework.util.async.IPromiseFinisher;
import com.ellirion.buildframework.util.async.Promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceTransaction extends Transaction {

    private List<Transaction> children;
    @Getter private boolean finalized;

    /**
     * Construct a new SequenceTransaction.
     */
    public SequenceTransaction() {
        this.children = new ArrayList<>();
        this.finalized = false;
    }

    /**
     * Construct a new SequenceTransaction using {@code transactions}
     * as the initial children.
     * @param transactions The initial children
     */
    public SequenceTransaction(final Transaction... transactions) {
        this.children = Arrays.asList(transactions);
        this.finalized = false;
    }

    /**
     * Add a child Transaction to this SequenceTransaction.
     * @param child The child Transaction to add.
     * @throws RuntimeException when this SequenceTransaction has already been applied at least once.
     */
    public synchronized void add(Transaction child) {
        assertNotFinalized();
        children.add(child);
    }

    /**
     * Removes a child Transaction from this SequenceTransaction.
     * @param child The child Transaction to remove.
     * @throws RuntimeException when this SequenceTransaction has already been applied at least once.
     */
    public synchronized void remove(Transaction child) {
        assertNotFinalized();
        children.remove(child);
    }

    /**
     * Checks if the given Transaction is a child of this SequenceTransaction.
     * @param child The child Transaction to check for.
     * @return Whether the given Transaction is a child of this SequenceTransaction.
     */
    public synchronized boolean contains(Transaction child) {
        return children.contains(child);
    }

    private synchronized void assertNotFinalized() {
        if (finalized) {
            throw new RuntimeException("Attempt to modify children of a finalized SequenceTransaction");
        }
    }

    @Override
    protected Promise<Boolean> applier() {
        // Prevent our children from being altered.
        synchronized (this) {
            finalized = true;
        }

        return new Promise<>(finisher -> {
            int index;
            boolean failed = false;

            // From the front, start going through our child transactions.
            for (index = 0; index < children.size(); index++) {
                Promise<Boolean> child = children.get(index).apply();

                // If the child threw an exception, propagate it upwards.
                if (!child.await()) {
                    finisher.reject(child.getException());
                    return;
                }

                // Otherwise, the child ran without throwing an exception.
                // However, this does not mean it was SUCCESSFUL.
                // We need to check the child's result for that.

                // If the child failed to apply, prepare for rollback.
                if (!child.getResult()) {
                    failed = true;
                    index--;
                    break;
                }

                // If it succeeded, we proceed to the next child.
            }

            // Return success if we didn't fail.
            if (!failed) {
                finisher.resolve(true);
                return;
            }

            // Roll back any changes upon failure.
            revertFrom(finisher, index);

            // Our result is a complete and utter failure.
            finisher.resolve(false);
        }, true);
    }

    @Override
    protected Promise<Boolean> reverter() {
        return new Promise<>(finisher -> {

            // From the front, start going through our child transactions.
            revertFrom(finisher, children.size() - 1);

            // We succeeded in reverting all our actions.
            finisher.resolve(true);
        }, true);
    }

    private void revertFrom(IPromiseFinisher<Boolean> finisher, int index) {
        for (; index >= 0; index--) {
            Promise<Boolean> child = children.get(index).revert();

            // If the child threw an exception, propagate it upwards.
            if (!child.await()) {
                finisher.reject(child.getException());
                return;
            }

            // Otherwise, the child ran without throwing an exception.
            // However, this does not mean it was SUCCESSFUL.
            // We need to check the child's result for that.

            // If the child failed to revert, also propagate it upwards.
            if (!child.getResult()) {
                finisher.reject(new IllegalStateException("Revert failed", child.getException()));
                return;
            }

            // If it succeeded, we proceed to the next child.
        }
    }
}
