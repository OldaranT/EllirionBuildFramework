package com.ellirion.buildframework.util;

import lombok.Getter;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionManager {

    @Getter private static final Map<Player, BlockingDeque<Transaction>> DONE_TRANSACTIONS = new ConcurrentHashMap<>();
    @Getter private static final Map<Player, BlockingDeque<Transaction>> UNDONE_TRANSACTIONS = new ConcurrentHashMap<>();
    private static final Map<Transaction, Lock> LOCKS = new ConcurrentHashMap<>();

    /**
     * Add a transaction that has already been applied.
     * @param player The player that has performed the transaction.
     * @param transaction The transaction the has been preformed.
     */
    public static void addDoneTransaction(Player player, Transaction transaction) {
        acquireLock(transaction);
        try {
            addToDone(player, transaction);
        } finally {
            releaseLock(transaction);
        }
    }

    /**
     * Performs the {@link Transaction} {@code transaction}.
     * @param player The player that wants to preform the transaction
     * @param transaction The transaction that needs to be performed
     * @return The resulting {@link Promise}
     */
    public static Promise performTransaction(Player player, Transaction transaction) {
        acquireLock(transaction);
        try {
            Promise promise = transaction.apply();
            promise.await();

            addToDone(player, transaction);

            return promise;
        } finally {
            releaseLock(transaction);
        }
    }

    /**
     * Undo's the last {@link Transaction} of the given {@code player}.
     * @param player The player whose transaction needs to be undone
     * @return The resulting promise
     */
    public static Promise undoLastTransaction(Player player) {

        Transaction transaction = DONE_TRANSACTIONS.get(player).pollFirst();
        if (transaction == null) {
            throw new RuntimeException("No transactions to be undone");
        }
        acquireLock(transaction);
        try {
            Promise promise = transaction.revert();
            promise.await();

            addToUndone(player, transaction);
            return promise;
        } finally {
            releaseLock(transaction);
        }
    }

    /**
     * Undo's the last undone {@link Transaction} of the given {@code player}.
     * @param player The player whose transaction needs to be redone
     * @return The resulting promise
     */
    public static Promise redoLastTransaction(Player player) {

        Transaction transaction = UNDONE_TRANSACTIONS.get(player).pollFirst();
        if (transaction == null) {
            throw new RuntimeException("No transactions to be redone");
        }
        acquireLock(transaction);
        try {
            Promise promise = transaction.apply();
            promise.await();

            addToUndone(player, transaction);
            return promise;
        } finally {
            releaseLock(transaction);
        }
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

    private static void acquireLock(Transaction transaction) {

        if (!LOCKS.containsKey(transaction)) {
            LOCKS.put(transaction, new ReentrantLock());
        }
        LOCKS.get(transaction).lock();
        BuildFramework.getInstance().getLogger().info(
                String.format("Acquired lock for transaction %s", transaction.toString()));
    }

    private static void releaseLock(Transaction transaction) {

        if (!LOCKS.containsKey(transaction)) {
            return;
        }
        LOCKS.get(transaction).unlock();
        BuildFramework.getInstance().getLogger().info(
                String.format("Released lock for transaction %s", transaction.toString()));
    }
}
