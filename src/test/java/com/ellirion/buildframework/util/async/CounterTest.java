package com.ellirion.buildframework.util.async;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

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
    public void await_whenAtZero_shouldImmediatelyReturnTrue() {
        Counter c = new Counter(0);

        assertTrue(c.await());
    }

    @Test
    public void await_whenNotAtZero_shouldWaitForZeroAndReturnTrue() {
        Counter c = new Counter(2);

        new Thread(() -> {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                    fail();
                }
                c.decrement();
            }
        }).start();

        assertTrue(c.await());
        assertEquals(0, c.get());
    }

    @Test
    public void await_whenNotAtZeroAndInterrupted_shouldReturnFalse() {
        Counter c = new Counter(2);
        CountDownLatch l = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            assertFalse(c.await());
            assertEquals(2, c.get());
            l.countDown();
        });
        Thread t2 = new Thread(() -> t1.interrupt());

        t1.start();
        t2.start();

        try {
            l.await();
        } catch (InterruptedException ex) {
            fail();
        }
        assertEquals(0, l.getCount());
    }

    @Test
    public void awaitCount_whenAtCorrectCount_shouldReturnImmediately() {
        Counter c = new Counter(2);

        assertTrue(c.await(2));
    }

    @Test
    public void awaitCount_whenNotAtCorrectCount_shouldWaitForCountAndReturnTrue() {
        Counter c = new Counter(0);

        new Thread(() -> {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                    fail();
                }
                c.increment();
            }
        }).start();

        assertTrue(c.await(2));
        assertEquals(2, c.get());
    }

    @Test
    public void awaitCount_whenNotAtCorrectCountAndInterrupted_shouldReturnFalse() {
        Counter c = new Counter(0);
        CountDownLatch l = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            assertFalse(c.await(2));
            assertEquals(0, c.get());
            l.countDown();
        });
        Thread t2 = new Thread(() -> t1.interrupt());

        t1.start();
        t2.start();

        try {
            l.await();
        } catch (InterruptedException ex) {
            fail();
        }
        assertEquals(0, l.getCount());
    }
}
