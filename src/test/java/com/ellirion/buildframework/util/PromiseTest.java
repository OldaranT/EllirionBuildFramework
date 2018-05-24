package com.ellirion.buildframework.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.ellirion.buildframework.BuildFramework;

import java.util.ArrayList;
import java.util.List;

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
    public void consumeSync_whenResolved_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.resolve(0));

        p.consumeSync((i) -> results.add(1));

        assertEquals(1, results.size());
    }

    @Test
    public void consumeSync_whenRejected_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.reject(null));

        p.consumeSync((i) -> results.add(1));

        assertEquals(0, results.size());
    }

    @Test
    public void consumeAsync_whenResolved_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.resolve(0));
        p.consumeAsync((i) -> results.add(1));

        assertEquals(1, results.size());
    }

    @Test
    public void consumeAsync_whenRejected_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.reject(null));

        p.consumeAsync((i) -> results.add(1));

        assertEquals(0, results.size());
    }

    @Test
    public void resumeSync_whenResolved_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.resolve(1));

        Promise<Integer> p2 = p1.resumeSync((i) -> {
            results.add(i);
            return i * 2;
        });
        p2.consumeSync(results::add);

        assertEquals(2, results.size());
        assertEquals(1, (int) results.get(0));
        assertEquals(2, (int) results.get(1));
    }

    @Test
    public void resumeSync_whenRejected_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.reject(null));
        Promise<Integer> p2 = p1.resumeSync((i) -> {
            results.add(i);
            return i * 2;
        });
        p2.consumeSync(results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void resumeAsync_whenResolved_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.resolve(1));
        Promise<Integer> p2 = p1.resumeAsync((i) -> {
            results.add(i);
            return i * 2;
        });
        p2.consumeAsync(results::add);

        assertEquals(2, results.size());
        assertEquals(1, (int) results.get(0));
        assertEquals(2, (int) results.get(1));
    }

    @Test
    public void resumeAsync_whenRejected_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.reject(null));
        Promise<Integer> p2 = p1.resumeAsync((i) -> {
            results.add(i);
            return i * 2;
        });
        p2.consumeAsync(results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void consumeFailSync_whenResolved_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.resolve(0));
        p.consumeFailSync((ex) -> results.add(1));

        assertEquals(0, results.size());
    }

    @Test
    public void consumeFailSync_whenRejected_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.reject(null));
        p.consumeFailSync((ex) -> results.add(1));

        assertEquals(1, results.size());
    }

    @Test
    public void consumeFailAsync_whenResolved_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p = new Promise<>((finisher) -> finisher.resolve(0));
        p.consumeFailAsync((ex) -> results.add(1));

        assertEquals(0, results.size());
    }

    @Test
    public void resumeFailSync_whenResolved_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.resolve(1));

        Promise<Integer> p2 = p1.resumeFailSync((ex) -> 0);
        p2.consumeSync(results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void resumeFailSync_whenRejected_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.reject(null));

        Promise<Integer> p2 = p1.resumeFailSync((ex) -> 0);
        p2.consumeSync(results::add);

        assertEquals(1, results.size());
    }

    @Test
    public void resumeFailAsync_whenResolved_shouldNotInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.resolve(1));

        Promise<Integer> p2 = p1.resumeFailSync((ex) -> 0);
        p2.consumeSync(results::add);

        assertEquals(0, results.size());
    }

    @Test
    public void resumeFailAsync_whenRejected_shouldInvokeHandler() {
        List<Integer> results = new ArrayList<>();
        Promise<Integer> p1 = new Promise<>((finisher) -> finisher.reject(null));

        Promise<Integer> p2 = p1.resumeFailAsync((ex) -> 0);
        p2.consumeSync(results::add);

        assertEquals(1, results.size());
    }

    @Test
    public void await_whenResolved_shouldReturnResolved() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.resolve(1));

        Promise.State s = p.await();
        assertEquals(Promise.State.RESOLVED, s);
    }

    @Test
    public void await_whenRejected_shouldReturnRejected() {
        Promise<Integer> p = new Promise<>(finisher -> finisher.reject(null));

        Promise.State s = p.await();
        assertEquals(Promise.State.REJECTED, s);
    }
}
