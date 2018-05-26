package com.ellirion.buildframework.util.async;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.util.async.Promise;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildFramework.class, Bukkit.class})
public class PromiseTest {

    public PromiseTest() {
        mockScheduler();
    }

    private void mockScheduler() {
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
    public void thenConsumeSync_whenResolved_shouldInvokeResolveHandler() {
        CountDownLatch latch = new CountDownLatch(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false);

        p.then(i -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void thenConsumeSync_whenRejected_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false);

        p.then(i -> {
            fail();
        });
    }

    @Test
    public void thenConsumeAsync_whenResolved_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), true);

        p.then(i -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void thenConsumeAsync_whenRejected_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), true);

        p.then(i -> {
            fail();
        });
    }

    @Test
    public void thenResumeSync_whenResolved_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(2);
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false);

        Promise<Integer> p2 = p1.then(i -> {
            assertEquals(1, (int) i);
            assertEquals(2, latch.getCount());
            latch.countDown();
            return i * 2;
        });
        p2.then(i -> {
            assertEquals(2, (int) i);
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void thenResumeSync_whenRejected_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false);

        p.then(i -> {
            fail();
            return 0;
        });
    }

    @Test
    public void thenResumeAsync_whenResolved_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(2);
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), true);

        Promise<Integer> p2 = p1.then(i -> {
            assertEquals(1, (int) i);
            assertEquals(2, latch.getCount());
            latch.countDown();
            return i * 2;
        });
        p2.then(i -> {
            assertEquals(2, (int) i);
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void thenResumeAsync_whenRejected_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), true);

        p.then(i -> {
            fail();
            return 0;
        });
    }

    @Test
    public void exceptConsumeSync_whenResolved_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), false);

        p.except(ex -> {
            fail();
        });
    }

    @Test
    public void exceptConsumeSync_whenRejected_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false);

        p.except(ex -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void exceptConsumeAsync_whenResolved_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(0), true);

        p.except(ex -> {
            fail();
        });
    }

    @Test
    public void exceptResumeSync_whenResolved_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), false);

        p.except(ex -> {
            fail();
            return 0;
        });
    }

    @Test
    public void exceptResumeSync_whenRejected_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false);

        p.except(ex -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
            return 0;
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void exceptResumeAsync_whenResolved_shouldNotInvokeHandler() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), true);

        p.except(ex -> {
            fail();
            return 0;
        });
    }

    @Test
    public void exceptResumeAsync_whenRejected_shouldInvokeHandler() {
        CountDownLatch latch = new CountDownLatch(1);
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), true);

        p.except(i -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void resolve_whenInvokedAndPending_shouldInvokeOnlyResolveHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        p.then(i -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });
        p.except(ex -> {
            fail();
        });

        p.schedule();
        assertEquals(0, latch.getCount());
    }

    @Test
    public void resolve_whenInvokedAndFinished_shouldInvokeOnlyResolveHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1));
        CountDownLatch latch = new CountDownLatch(1);

        p.then(i -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });
        p.except(ex -> {
            fail();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void reject_whenInvokedAndPending_shouldInvokeOnlyRejectionHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        p.then(i -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        p.schedule();
        assertEquals(0, latch.getCount());
    }

    @Test
    public void reject_whenInvokedAndFinished_shouldInvokeOnlyRejectionHandlers() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null));
        CountDownLatch latch = new CountDownLatch(1);

        p.then(i -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, latch.getCount());
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void await_whenResolved_shouldReturnResolved() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1));

        Promise.PromiseState s = p.await();
        assertEquals(Promise.PromiseState.RESOLVED, s);
    }

    @Test
    public void await_whenRejected_shouldReturnRejected() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null));

        Promise.PromiseState s = p.await();
        assertEquals(Promise.PromiseState.REJECTED, s);
    }

    @Test
    public void sequence_whenInvoked_shouldRunSequentially() {
        CountDownLatch latch = new CountDownLatch(3);
        Promise<Integer> p1 = new Promise<>(finisher -> {
            assertEquals(3, latch.getCount());
            finisher.resolve(1);
            latch.countDown();
        }, false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> {
            assertEquals(2, latch.getCount());
            finisher.resolve(2);
            latch.countDown();
        }, false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> {
            assertEquals(1, latch.getCount());
            finisher.resolve(3);
            latch.countDown();
        }, false, false);

        Promise.sequence(p1, p2, p3);

        assertEquals(0, latch.getCount());
    }

    @Test
    public void sequence_whenAllPromisesResolve_shouldResolvePromise() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.resolve(3), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Map<Promise, Object>> p = Promise.sequence(p1, p2, p3);
        p.then(results -> {
            assertEquals(1, results.get(p1));
            assertEquals(2, results.get(p2));
            assertEquals(3, results.get(p3));
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void sequence_whenAnyPromiseRejects_shouldRejectPromise() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> {
            throw new RuntimeException();
        }, false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.resolve(3), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Map<Promise, Object>> p = Promise.sequence(false, p1, p2, p3);
        p.except(ex -> {
            latch.countDown();
        });
        p.schedule();

        assertEquals(0, latch.getCount());
    }

    @Test
    public void any_whenAnyPromiseResolves_shouldResolve() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Object> p = Promise.any(p1, p2).schedule();
        p.then(obj -> {
            assertEquals(1, latch.getCount());
            assertEquals(1, obj);
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void any_whenAnyPromiseRejects_shouldReject() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.reject(null), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Object> p = Promise.any(p1, p2).schedule();
        p.then(result -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, latch.getCount());
            assertNull(ex);
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void all_whenSomePromisesResolve_shouldNeverFinish() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> {}, false, false);

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
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Map<Promise, Object>> p = Promise.all(p1, p2, p3);
        p.then(result -> {
            assertEquals(1, latch.getCount());
            assertEquals(1, result.get(p1));
            assertEquals(2, result.get(p2));
            assertEquals(3, result.get(p3));
            latch.countDown();
        });
        p.except(ex -> {
            fail();
        });

        assertEquals(0, latch.getCount());
    }

    @Test
    public void all_whenSomePromisesReject_shouldReject() {
        Promise<Integer> p1 = new Promise<>(finisher -> finisher.resolve(1), false, false);
        Promise<Integer> p2 = new Promise<>(finisher -> finisher.resolve(2), false, false);
        Promise<Integer> p3 = new Promise<>(finisher -> finisher.reject(null), false, false);
        CountDownLatch latch = new CountDownLatch(1);

        Promise<Map<Promise, Object>> p = Promise.all(p1, p2, p3);
        p.then(result -> {
            fail();
        });
        p.except(ex -> {
            assertEquals(1, latch.getCount());
            assertNull(ex);
            latch.countDown();
        });

        assertEquals(0, latch.getCount());
    }
}
