package com.ellirion.buildframework.util.transact;

import org.bukkit.Bukkit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.util.async.Counter;
import com.ellirion.buildframework.util.async.Promise;

import java.util.function.Supplier;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class})
public class TransactionTest {

    private Supplier<Promise<Boolean>> countUp(Counter counter) {
        return () -> new Promise<>(finisher -> {
            counter.increment();
            finisher.resolve(true);
        });
    }

    private Supplier<Promise<Boolean>> countDown(Counter counter) {
        return () -> new Promise<>(finisher -> {
            counter.decrement();
            finisher.resolve(true);
        });
    }

    private Supplier<Promise<Boolean>> delay(long time, Supplier<Promise<Boolean>> supplier) {
        return () -> new Promise<>(finisher -> {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
            }
            Promise<Boolean> child = supplier.get();
            child.schedule();
            if (child.await()) {
                finisher.resolve(child.getResult());
            } else {
                finisher.reject(child.getException());
            }
        });
    }

    private Transaction countTransaction(Counter counter) {
        return new SimpleTransaction(countUp(counter), countDown(counter));
    }

    @Test
    public void apply_whenNotApplied_shouldApply() {
        Counter c = new Counter(0);
        Transaction t = countTransaction(c);

        assertEquals(0, c.get());
        t.apply().await();
        assertEquals(1, c.get());
    }

    @Test(expected = RuntimeException.class)
    public void apply_whenApplied_shouldThrowException() {
        Counter c = new Counter(0);
        Transaction t = countTransaction(c);

        assertEquals(0, c.get());
        t.apply();
        assertEquals(1, c.get());
        t.apply();
    }

    @Test(expected = RuntimeException.class)
    public void revert_whenNotApplied_shouldThrowException() {
        Counter c = new Counter(0);
        Transaction t = countTransaction(c);

        t.revert();
    }

    @Test
    public void revert_whenApplied_shouldRevert() {
        Counter c = new Counter(0);
        Transaction t = countTransaction(c);

        assertEquals(0, c.get());
        t.apply();
        assertEquals(1, c.get());
        t.revert();
        assertEquals(0, c.get());
    }

    @Test
    public void revert_whenStillApplying_shouldWait() {
        Counter c = new Counter(0);
        Transaction t = new SimpleTransaction(delay(500, countUp(c)), countDown(c));

        // An error would be thrown if the waiting did not work.
        t.apply();
        t.revert();
    }
}
