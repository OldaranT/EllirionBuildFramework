package com.ellirion.buildframework.util.async;

import org.junit.Test;

import static org.junit.Assert.*;

public class CounterTest {

    @Test
    public void constructor_whenInvoked_shouldInitializeToZero() {
        Counter c = new Counter();

        assertEquals(0, c.get());
    }

    @Test
    public void constructorCount_whenInvoked_shouldInitializeToGivenCount() {
        Counter c = new Counter(2);

        assertEquals(2, c.get());
    }

    @Test
    public void increment_whenInvoked_shouldIncrement() {
        Counter c = new Counter();

        c.increment();
        assertEquals(1, c.get());
    }

    @Test
    public void decrement_whenInvoked_shouldDecrement() {
        Counter c = new Counter();

        c.decrement();
        assertEquals(-1, c.get());
    }

    @Test
    public void get_whenInvoked_shouldReturnCount() {
        Counter c = new Counter();

        for (int i = 0; i < 5; i++) {
            assertEquals(i, c.get());
            c.increment();
        }
    }

    @Test
    public void perform_whenInvoked_shouldExecuteRunnable() {
        Counter c = new Counter();

        assertEquals(0, c.get());
        c.perform(c::increment);
        assertEquals(1, c.get());
    }

    @Test
    public void awaitAndPerform_whenInvoked_shouldWaitAndExecuteRunnable() {
        Counter c = new Counter();

        Thread t1 = new Thread(() -> {
            c.awaitAndPerform(1, c::increment);
        });

        Thread t2 = new Thread(c::increment);

        assertEquals(0, c.get());
        t1.start();
        assertEquals(0, c.get());
        t2.start();

        try {
            t2.join();
            t1.join();
            assertEquals(2, c.get());
        } catch (InterruptedException ex) {
            fail();
        }
    }

    @Test
    public void await_whenAtCorrectCount_shouldReturnImmediately() {
        Counter c = new Counter(2);

        c.await(2);
    }

    @Test
    public void await_whenNotAtCorrectCount_shouldWaitForCountToMatch() {
        Counter c = new Counter(0);

        new Thread(() -> {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(250);
                } catch (Exception ex) {
                    fail();
                }
                c.increment();
            }
        }).start();

        c.await(2);

        assertEquals(2, c.get());
    }

    @Test
    public void await_whenNotAtCorrectCountAndInterrupted_shouldThrowException() {
        Counter c = new Counter(0);

        Thread t1 = new Thread(() -> {
            try {
                c.await(1);
                fail();
            } catch (RuntimeException ex) {
                assertEquals("await() was interrupted", ex.getMessage());
            }
        });
        Thread t2 = new Thread(() -> t1.interrupt());

        t1.start();
        t2.start();

        try {
            t2.join();
            t1.join();
        } catch (InterruptedException ex) {
            fail();
        }
    }
}
