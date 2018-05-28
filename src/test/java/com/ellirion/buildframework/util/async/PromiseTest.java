package com.ellirion.buildframework.util.async;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class})
public class PromiseTest {

    @Before
    public void setup() {
        BukkitScheduler sched = mock(BukkitScheduler.class);

        mockStatic(BuildFramework.class);
        mockStatic(Bukkit.class);

        when(BuildFramework.getInstance()).thenReturn(null);
        when(Bukkit.getScheduler()).thenReturn(sched);
        when(sched.runTask(anyObject(), Mockito.any(Runnable.class))).thenAnswer((inv) -> {
            Runnable r = (Runnable) inv.getArguments()[1];
            r.run();
            return null;
        });
        when(sched.runTaskAsynchronously(anyObject(), Mockito.any(Runnable.class))).thenAnswer((inv) -> {
            Runnable r = (Runnable) inv.getArguments()[1];
            r.run();
            return null;
        });
    }

    @Test
    public void thenConsume_whenResolvingBefore_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false, true);

        p.then(i -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void thenConsume_whenResolvingAfter_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false, false);

        p.then(i -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(1, c.get());
        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void thenConsume_whenRejectingBefore_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, true);

        p.then(i -> {
            fail();
        });
    }

    @Test
    public void thenConsume_whenRejectingAfter_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);

        p.then(i -> {
            fail();
        });

        p.schedule();
    }

    @Test
    public void thenResume_whenResolvingBefore_shouldInvokeHandler() {
        Counter c = new Counter(2);
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, true);

        Promise<Integer> p2 = p1.then(i -> {
            assertEquals(1, (int) i);
            assertEquals(2, c.get());
            c.decrement();
            return i * 2;
        });
        p2.then(i -> {
            assertEquals(2, (int) i);
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void thenResume_whenResolvingAfter_shouldInvokeHandler() {
        Counter c = new Counter(2);
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);

        Promise<Integer> p2 = p1.then(i -> {
            assertEquals(1, (int) i);
            assertEquals(2, c.get());
            c.decrement();
            return i * 2;
        });
        p2.then(i -> {
            assertEquals(2, (int) i);
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(2, c.get());
        p1.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void thenResume_whenRejectingBefore_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, true);

        p.then(i -> {
            fail();
            return 0;
        });
    }

    @Test
    public void thenResume_whenRejectingAfter_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);

        p.then(i -> {
            fail();
            return 0;
        });

        p.schedule();
    }

    @Test
    public void exceptConsume_whenResolvingBefore_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false, true);

        p.except(ex -> {
            fail();
        });
    }

    @Test
    public void exceptConsume_whenResolvingAfter_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false, false);

        p.except(ex -> {
            fail();
        });

        p.schedule();
    }

    @Test
    public void exceptConsume_whenRejectingBefore_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, true);

        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void exceptConsume_whenRejectingAfter_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);

        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(1, c.get());
        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void exceptResume_whenResolvingBefore_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), false, true);

        p.except(ex -> {
            fail();
            return 0;
        });
    }

    @Test
    public void exceptResume_whenResolvingAfter_shouldDoNothing() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), false, false);

        p.except(ex -> {
            fail();
            return 0;
        });

        p.schedule();
    }

    @Test
    public void exceptResume_whenRejectingBefore_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, true);

        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
            return 0;
        });

        assertEquals(0, c.get());
    }

    @Test
    public void exceptResume_whenRejectingAfter_shouldInvokeHandler() {
        Counter c = new Counter(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);

        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
            return 0;
        });

        assertEquals(1, c.get());
        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void always_whenResolvingBefore_shouldInvokeHandler() {
        Promise<Boolean> p = new Promise<>(finisher -> finisher.resolve(true), false, true);
        Counter c = new Counter(1);

        p.always(() -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void always_whenResolvingAfter_shouldInvokeHandler() {
        Promise<Boolean> p = new Promise<>(finisher -> finisher.resolve(true), false, false);
        Counter c = new Counter(1);

        p.always(() -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(1, c.get());
        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void always_whenRejectingBefore_shouldInvokeHandler() {
        Promise<Boolean> p = new Promise<>(finisher -> finisher.reject(null), false, true);
        Counter c = new Counter(1);

        p.always(() -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void always_whenRejectingAfter_shouldInvokeHandler() {
        Promise<Boolean> p = new Promise<>(finisher -> finisher.reject(null), false, false);
        Counter c = new Counter(1);

        p.always(() -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(1, c.get());
        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void finisherResolve_whenInvokedAndPending_shouldInvokeOnlyResolveHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Counter c = new Counter(1);

        p.then(i -> {
            assertEquals(1, c.get());
            c.decrement();
        });
        p.except(ex -> {
            fail();
        });

        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void finisherResolve_whenInvokedAndFinished_shouldInvokeOnlyResolveHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1));
        Counter c = new Counter(1);

        p.then(i -> {
            assertEquals(1, c.get());
            c.decrement();
        });
        p.except(ex -> {
            fail();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void finisherReject_whenInvokedAndPending_shouldInvokeOnlyRejectionHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);
        Counter c = new Counter(1);

        p.then(i -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        p.schedule();
        assertEquals(0, c.get());
    }

    @Test
    public void finisherReject_whenInvokedAndFinished_shouldInvokeOnlyRejectionHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null));
        Counter c = new Counter(1);

        p.then(i -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void await_whenResolved_shouldReturnTrue() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1));

        assertTrue(p.await());
    }

    @Test
    public void await_whenRejected_shouldReturnFalse() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null));

        assertFalse(p.await());
    }

    @Test
    public void sequence_whenInvoked_shouldRunSequentially() {
        Counter c = new Counter(3);
        Promise<Integer> p1 = new Promise<>(finisher -> {
            assertEquals(3, c.get());
            finisher.resolve(1);
            c.decrement();
        }, false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> {
            assertEquals(2, c.get());
            finisher.resolve(2);
            c.decrement();
        }, false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> {
            assertEquals(1, c.get());
            finisher.resolve(3);
            c.decrement();
        }, false, false);

        Promise.sequence(p1, p2, p3);

        assertEquals(0, c.get());
    }

    @Test
    public void sequence_whenAllPromisesResolve_shouldResolvePromise() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.resolve(3), false, false);
        Counter c = new Counter(1);

        Promise<Map<Promise, Object>> p = Promise.sequence(p1, p2, p3);
        p.then(results -> {
            assertEquals(1, results.get(p1));
            assertEquals(2, results.get(p2));
            assertEquals(3, results.get(p3));
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void sequence_whenAnyPromiseRejects_shouldRejectPromise() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> {
            throw new RuntimeException();
        }, false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.resolve(3), false, false);
        Counter c = new Counter(1);

        Promise<Map<Promise, Object>> p = Promise.sequence(false, p1, p2, p3);
        p.except(ex -> {
            c.decrement();
        });
        p.schedule();

        assertEquals(0, c.get());
    }

    @Test
    public void any_whenAnyPromiseResolves_shouldResolve() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Counter c = new Counter(1);

        Promise<Object> p = Promise.any(p1, p2).schedule();
        p.then(obj -> {
            assertEquals(1, c.get());
            assertEquals(1, obj);
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void any_whenAnyPromiseRejects_shouldReject() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.reject(null), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Counter c = new Counter(1);

        Promise<Object> p = Promise.any(p1, p2).schedule();
        p.then(result -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, c.get());
            assertNull(ex);
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void all_whenSomePromisesResolve_shouldNeverFinish() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> { }, false, false);

        Promise<Map<Promise, Object>> p = Promise.all(p1, p2, p3);
        p.then(result -> {
            fail();
        });
        p.except(ex -> {
            fail();
        });
    }

    @Test
    public void all_whenAllPromisesResolve_shouldResolve() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.resolve(3), false, false);
        Counter c = new Counter(1);

        Promise<Map<Promise, Object>> p = Promise.all(p1, p2, p3);
        p.then(result -> {
            assertEquals(1, c.get());
            assertEquals(1, result.get(p1));
            assertEquals(2, result.get(p2));
            assertEquals(3, result.get(p3));
            c.decrement();
        });
        p.except(ex -> {
            fail();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void all_whenSomePromisesReject_shouldReject() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.reject(null), false, false);
        Counter c = new Counter(1);

        Promise<Map<Promise, Object>> p = Promise.all(p1, p2, p3);
        p.then(result -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, c.get());
            assertNull(ex);
            c.decrement();
        });

        assertEquals(0, c.get());
    }

    @Test
    public void resolve_whenGivenResult_shouldReturnPromiseResolvedToResult() {
        Promise<Boolean> p = Promise.resolve(true);
        Counter c = new Counter(2);

        p.then(b -> {
            assertEquals(2, c.get());
            c.decrement();
            assertTrue(b);
        });
        p.then(b -> {
            assertEquals(1, c.get());
            c.decrement();
            assertTrue(b);
            return 0;
        });
        p.except(ex -> {
            fail();
        });
        p.except(ex -> {
            fail();
            return 0;
        });
        p.schedule();

        assertEquals(0, c.get());
    }

    @Test
    public void reject_whenGivenException_shouldReturnPromiseRejectedToException() {
        Exception e = new Exception();
        Promise<Boolean> p = Promise.reject(e);
        Counter c = new Counter(2);

        p.then(b -> {
            fail();
        });
        p.then(b -> {
            fail();
            return 0;
        });
        p.except(ex -> {
            assertEquals(2, c.get());
            c.decrement();
            assertEquals(e, ex);
        });
        p.except(ex -> {
            assertEquals(1, c.get());
            c.decrement();
            assertEquals(e, ex);
            return 0;
        });
        p.schedule();

        assertEquals(0, c.get());
    }
}
