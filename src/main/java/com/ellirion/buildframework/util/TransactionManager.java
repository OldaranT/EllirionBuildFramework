package com.ellirion.buildframework.util;

import lombok.Getter;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class TransactionManager {

    @Getter private static final Map<Player, BlockingDeque<Transaction>> DONE_TRANSACTIONS = new ConcurrentHashMap<>();
    @Getter private static final Map<Player, BlockingDeque<Transaction>> UNDONE_TRANSACTIONS = new ConcurrentHashMap<>();

    /**
     * Add a transaction that has already been applied.
     * @param player The player that has performed the transaction.
     * @param transaction The transaction the has been preformed.
     */
    public static void addDoneTransaction(Player player, Transaction transaction) {
        addToDone(player, transaction);
    }

    /**
     * Performs the {@link Transaction} {@code transaction}.
     * @param player The player that wants to preform the transaction
     * @param transaction The transaction that needs to be performed
     * @return The resulting {@link Promise}
     */
    public static Promise<Boolean> performTransaction(Player player, Transaction transaction) {

        Promise<Boolean> promise = transaction.apply();
        promise.await();

        addToDone(player, transaction);

        return promise;
    }

    /**
     * Undoes the last {@link Transaction} of the given {@code player}.
     * @param player The player whose transaction needs to be undone
     * @return The resulting promise
     */
    public static Promise<Boolean> undoLastTransaction(Player player) {

        Transaction transaction = DONE_TRANSACTIONS.get(player).pollFirst();
        if (transaction == null) {
            return Promise.reject(new RuntimeException("No transactions to be undone"));
        }

        Promise<Boolean> promise = transaction.revert();
        promise.await();

        addToUndone(player, transaction);
        return promise;
    }

    /**
     * Redoes the last undone {@link Transaction} of the given {@code player}.
     * @param player The player whose transaction needs to be redone
     * @return The resulting promise
     */
    public static Promise<Boolean> redoLastTransaction(Player player) {

        Transaction transaction = UNDONE_TRANSACTIONS.get(player).pollFirst();
        if (transaction == null) {
            return Promise.reject(new RuntimeException("No transactions to be redone"));
        }
        Promise<Boolean> promise = transaction.apply();
        promise.await();

        addToUndone(player, transaction);
        return promise;
    }

    private static void addToDone(Player player, Transaction transaction) {
        if (!DONE_TRANSACTIONS.containsKey(player)) {
            DONE_TRANSACTIONS.put(player, new LinkedBlockingDeque<>());
        }
        DONE_TRANSACTIONS.get(player).addFirst(transaction);
    }

    private static void addToUndone(Player player, Transaction transaction) {
        if (!DONE_TRANSACTIONS.containsKey(player)) {
            DONE_TRANSACTIONS.put(player, new LinkedBlockingDeque<>());
        }
        DONE_TRANSACTIONS.get(player).addFirst(transaction);
    }
}
