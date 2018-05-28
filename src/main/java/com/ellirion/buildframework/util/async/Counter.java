package com.ellirion.buildframework.util.async;

public class Counter {

    private final Object latch;
    private int count;

    /**
     * Construct a Counter with an initial count of zero.
     */
    public Counter() {
        this(0);
    }

    /**
     * Construct a Counter with an initial count of {@code count}.
     * @param count The initial count
     */
    public Counter(final int count) {
        this.latch = new Object();
        this.count = count;
    }

    /**
     * Increment the count by one.
     */
    public void increment() {
        synchronized (latch) {
            count++;
            latch.notifyAll();
        }
    }

    /**
     * Decrement the count by one.
     */
    public void decrement() {
        synchronized (latch) {
            count--;
            latch.notifyAll();
        }
    }

    /**
     * Get the current count.
     * @return The current count
     */
    public int get() {
        synchronized (latch) {
            return count;
        }
    }

    /**
     * Wait for this Counter to reach zero.
     * @return Whether the wait completed without interruption
     */
    public boolean await() {
        return await(0);
    }

    /**
     * Wait for this Counter to reach {@code i}.
     * @return Whether the wait completed without interruption
     */
    public boolean await(int i) {
        try {
            synchronized (latch) {
                while (count != i) {
                    latch.wait();
                }
            }
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }
}
