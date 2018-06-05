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
public class SequenceTransactionTest {

    private Supplier<Promise<Boolean>> countUpSupplier(Counter counter) {
        return () -> new Promise<>(finisher -> {
            counter.increment();
            finisher.resolve(true);
        });
    }

    private Supplier<Promise<Boolean>> countDownSupplier(Counter counter) {
        return () -> new Promise<>(finisher -> {
            counter.decrement();
            finisher.resolve(true);
        });
    }

    private Supplier<Promise<Boolean>> failSupplier() {
        return () -> new Promise<>(finisher -> finisher.resolve(false));
    }

    private Supplier<Promise<Boolean>> successSupplier() {
        return () -> new Promise<>(finisher -> finisher.resolve(true));
    }

    private Transaction counting(Counter counter) {
        return new SimpleTransaction(countUpSupplier(counter), countDownSupplier(counter));
    }

    private Transaction countAndFail(Counter counter) {
        return new SimpleTransaction(countUpSupplier(counter), failSupplier());
    }

    private Transaction failing() {
        return new SimpleTransaction(failSupplier(), failSupplier());
    }

    private Transaction succeeding() {
        return new SimpleTransaction(successSupplier(), successSupplier());
    }

    @Test
    public void add_whenNotFinalized_shouldAdd() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();

        t.add(t1);

        assertTrue(t.contains(t1));
    }

    @Test(expected = RuntimeException.class)
    public void add_whenFinalized_shouldThrowException() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();
        t.apply();

        t.add(t1);
    }

    @Test
    public void remove_whenNotFinalized_shouldRemove() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();

        t.add(t1);
        t.remove(t1);

        assertFalse(t.contains(t1));
    }

    @Test(expected = RuntimeException.class)
    public void remove_whenFinalized_shouldThrowException() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.apply();

        t.remove(t1);
    }

    @Test
    public void contains_whenNotContained_shouldReturnFalse() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();

        assertFalse(t.contains(t1));
    }

    @Test
    public void contains_whenContained_shouldReturnTrue() {
        Transaction t1 = succeeding();
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);

        assertTrue(t.contains(t1));
    }

    @Test
    public void apply_whenChildrenSucceed_shouldResolveTrue() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Counter c2 = new Counter(0);
        Counter c3 = new Counter(0);
        Transaction t1 = counting(c1);
        Transaction t2 = counting(c2);
        Transaction t3 = counting(c3);
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.add(t2);
        t.add(t3);

        Promise<Boolean> p = t.apply();
        p.then(bool -> {
            assertEquals(0, c.get());
            c.increment();
        });
        p.await();

        assertEquals(1, c.get());
        assertEquals(1, c1.get());
        assertEquals(1, c2.get());
        assertEquals(1, c3.get());
    }

    @Test
    public void apply_whenChildFails_shouldRevertAndResolveFalse() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Counter c2 = new Counter(0);
        Transaction t1 = counting(c1);
        Transaction t2 = failing();
        Transaction t3 = counting(c2);
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.add(t2);
        t.add(t3);

        Promise<Boolean> p = t.apply();
        p.then(bool -> {
            assertEquals(0, c.get());
            assertFalse(bool);
            c.increment();
        });
        p.await();

        assertEquals(1, c.get());
        assertEquals(0, c1.get());
        assertEquals(0, c2.get());
    }

    @Test
    public void apply_whenChildFailsAndRevertFails_shouldReject() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Transaction t1 = countAndFail(c1);
        Transaction t2 = failing();
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.add(t2);

        Promise<Boolean> p = t.apply();
        p.except(ex -> {
            assertEquals(0, c.get());
            c.increment();
        });
        p.await();

        assertEquals(1, c.get());
        assertEquals(1, c1.get());
    }

    @Test
    public void apply_whenChildIsPending_shouldWaitForChildToFinish() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Counter c2 = new Counter(0);
        Transaction t1 = new SimpleTransaction(countUpSupplier(c1), () -> new Promise<>(f -> {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                fail();
            }
            c1.decrement();
            f.resolve(true);
        }));
        Transaction t2 = counting(c2);
        SequenceTransaction t = new SequenceTransaction(false, t1, t2);
        t.apply();
        t.revert();

        // This apply() takes 100ms to process, since revert() of t1 is still pending.
        Promise<Boolean> p = t.apply();
        p.then(bool -> {
            assertEquals(0, c.get());
            c.increment();
        });

        p.await();
        assertEquals(1, c.get());
    }

    @Test
    public void apply_whenChildIsPendingApplied_shouldThrowException() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Transaction t1 = new SimpleTransaction(() -> new Promise<>(f -> {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                fail();
            }
            c1.decrement();
            f.resolve(true);
        }), countDownSupplier(c1));
        t1.apply();

        Transaction t = new SequenceTransaction(false, t1);

        // This apply() takes 100ms to process, after which an exception will be thrown
        // due to the child Transaction ending up in an unexpected applied state.
        Promise<Boolean> p = t.apply();
        p.except(ex -> {
            assertEquals(0, c.get());
            c.increment();
        });

        p.await();
        assertEquals(1, c.get());
    }

    @Test
    public void revert_whenChildrenSucceed_shouldResolveTrue() {
        Counter c = new Counter(1);
        Counter c1 = new Counter(0);
        Counter c2 = new Counter(0);
        Transaction t1 = counting(c1);
        Transaction t2 = counting(c2);
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.add(t2);

        t.apply().await();

        assertEquals(1, c.get());
        assertEquals(1, c1.get());
        assertEquals(1, c2.get());
        Promise<Boolean> p = t.revert();
        p.then(bool -> {
            assertEquals(1, c.get());
            c.decrement();
        });
        p.await();
        assertEquals(0, c.get());
        assertEquals(0, c1.get());
        assertEquals(0, c2.get());
    }

    @Test
    public void revert_whenChildFails_shouldReject() {
        Counter c = new Counter(1);
        Counter c1 = new Counter(0);
        Transaction t1 = countAndFail(c1);
        Transaction t2 = failing();
        SequenceTransaction t = new SequenceTransaction();
        t.add(t1);
        t.add(t2);

        t.apply().await();

        assertEquals(1, c.get());
        assertEquals(1, c1.get());
        Promise<Boolean> p2 = t.revert();
        p2.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
        });
        p2.await();
        assertEquals(0, c.get());
        assertEquals(1, c1.get());
    }

    @Test
    public void revert_whenChildIsPending_shouldWaitForChildToFinish() {
        Counter c = new Counter(0);
        Counter c1 = new Counter(0);
        Counter c2 = new Counter(0);
        Transaction t1 = new SimpleTransaction(() -> new Promise<>(f -> {
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                fail();
            }
            c1.increment();
            f.resolve(true);
        }), countDownSupplier(c1));
        Transaction t2 = counting(c2);
        SequenceTransaction t = new SequenceTransaction(false, t1, t2);
        t.apply();

        // This revert() takes 100ms to process, since apply() of t1 is still pending.
        Promise<Boolean> p = t.revert();
        p.then(bool -> {
            assertEquals(0, c.get());
            c.increment();
        });

        p.await();
        assertEquals(1, c.get());
    }
}
