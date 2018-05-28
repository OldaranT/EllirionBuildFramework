package com.ellirion.buildframework.util.async;

import lombok.Getter;
import org.apache.commons.lang.UnhandledException;
import org.bukkit.Bukkit;
import com.ellirion.buildframework.BuildFramework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.ellirion.buildframework.util.async.PromiseState.*;

public class Promise<TResult> {

    private PromiseState state;
    private boolean scheduled;
    @Getter private TResult result;
    @Getter private Exception exception;

    private List<Consumer<TResult>> onResolve;
    private List<Consumer<Exception>> onReject;

    private final IPromiseBody<TResult> runner;
    private final IPromiseFinisher<TResult> finisher;

    private final Counter latch;
    private final boolean async;

    /**
     * Construct a Promise with the given runner as function body.
     * @param runner The runner to run
     */
    public Promise(final IPromiseBody<TResult> runner) {
        this(runner, false);
    }

    /**
     * Construct a Promise with the given runner as function body.
     * @param runner The runner to run
     * @param async Whether to run asynchronously or not
     */
    public Promise(final IPromiseBody<TResult> runner, final boolean async) {
        this(runner, async, true);
    }

    /**
     * Construct a Promise with the given runner as function body.
     * @param runner The runner to run
     * @param async Whether to run asynchronously or not
     * @param immediate Whether to immediately schedule this Promise or not
     */
    public Promise(final IPromiseBody<TResult> runner, final boolean async, final boolean immediate) {
        this.state = PENDING;
        this.scheduled = false;
        this.result = null;
        this.exception = null;

        this.onResolve = new ArrayList<>();
        this.onReject = new ArrayList<>();

        this.runner = runner;
        this.finisher = new IPromiseFinisher<TResult>() {
            @Override
            public void resolve(TResult t) {
                handleResolve(t);
            }

            @Override
            public void reject(Exception ex) {
                handleReject(ex);
            }
        };

        this.latch = new Counter(1);
        this.async = async;

        // Schedule (sync or async) the invocation of our runner if requested.
        if (immediate) {
            this.schedule();
        }
    }

    /**
     * When this Promise is resolved, the {@code continuer} is invoked with the result.
     * @param continuer The function body that is invoked upon resolving
     * @param async Whether to run the {@code continuer} asynchronously or not
     * @param <TNext> The return type the {@code continuer}
     * @return The resulting Promise with type {@code TNext}.
     */
    public synchronized <TNext> Promise<TNext> then(
            IPromiseContinuer<TResult, TNext> continuer, boolean async) {
        // Create a follow-up promise that does not schedule itself immediately.
        Promise<TNext> next = new Promise<>(finisher -> {
            try {
                finisher.resolve(continuer.run(result));
            } catch (Exception ex) {
                finisher.reject(ex);
            }
        }, async, false);

        // If we have already been resolved, schedule the next Promise for execution.
        if (state == RESOLVED) {
            next.runBody();
            return next;
        }

        // If we have been rejected, we actually DO NOT schedule the next Promise
        // for failure. Since the next Promise is new and still pending, doing this
        // would cause the next Promise to immediately throw an "unhandled exception"
        // exception. What we WILL do is straight away set the next Promise to a failed
        // state without running the handlers.
        if (state == REJECTED) {
            next.state = REJECTED;
            next.exception = exception;
            return next;
        }

        // If we are still pending, don't schedule it yet!
        // We put the handlers in the follow-up queue(s).
        onResolve.add(result -> next.runBody());
        onReject.add(next::runFailure);
        return next;
    }

    /**
     * When this Promise is resolved, the {@code continuer} is invoked with the result.
     * The {@code continuer} is ran with the same synchronicity as this Promise.
     * @param continuer The function body that is invoked upon resolving
     * @param <TNext> The return type the {@code continuer}
     * @return The resulting Promise with type {@code TNext}.
     */
    public <TNext> Promise<TNext> then(IPromiseContinuer<TResult, TNext> continuer) {
        return then(continuer, async);
    }

    /**
     * When this Promise is resolved, the {@code consumer} is invoked with the result.
     * @param consumer The function body that is invoked upon resolving
     * @param async Whether to run the {@code consumer} asynchronously or not
     */
    public void then(Consumer<TResult> consumer, boolean async) {
        then(result -> {
            consumer.accept(result);
            return null;
        }, async);
    }

    /**
     * When this Promise is resolved, the {@code consumer} is invoked with the result.
     * The {@code consumer} is ran with the same synchronicity as this Promise.
     * @param consumer The function body that is invoked upon resolving
     */
    public void then(Consumer<TResult> consumer) {
        then(result -> {
            consumer.accept(result);
            return null;
        }, async);
    }

    /**
     * When this Promise is rejected, the {@code continuer} is invoked with the exception.
     * @param continuer The function body that is invoked upon rejection
     * @param async Whether to run the {@code continuer} asynchronously or not
     * @param <TNext> The return type the {@code continuer}
     * @return The resulting Promise with type {@code TNext}.
     */
    public synchronized <TNext> Promise<TNext> except(
            IPromiseContinuer<Exception, TNext> continuer, boolean async) {
        // Create a follow-up promise that does not schedule itself immediately.
        Promise<TNext> next = new Promise<>(finisher -> {
            try {
                finisher.resolve(continuer.run(exception));
            } catch (Exception ex) {
                finisher.reject(ex);
            }
        }, async, false);

        // If we have already been resolved, just return the
        // next Promise. It will never be executed.
        if (state == RESOLVED) {
            return next;
        }

        // If we have been rejected, schedule the next Promise for execution.
        if (state == REJECTED) {
            next.runBody();
            return next;
        }

        // If we are still pending, don't schedule it yet!
        // We put the handlers in the follow-up queue(s).
        onReject.add(result -> next.runBody());
        return next;
    }

    /**
     * When this Promise is rejected, the {@code continuer} is invoked with the exception.
     * The {@code continuer} is ran with the same synchronicity as this Promise.
     * @param continuer The function body that is invoked upon rejection
     * @param <TNext> The return type the {@code continuer}
     * @return The resulting Promise with type {@code TNext}.
     */
    public <TNext> Promise<TNext> except(IPromiseContinuer<Exception, TNext> continuer) {
        return except(continuer, async);
    }

    /**
     * When this Promise is rejected, the {@code consumer} is invoked with the exception.
     * @param consumer The function body that is invoked upon rejection
     * @param async Whether to run the {@code consumer} asynchronously or not
     */
    public void except(Consumer<Exception> consumer, boolean async) {
        except(ex -> {
            consumer.accept(ex);
            return null;
        }, async);
    }

    /**
     * When this Promise is rejected, the {@code consumer} is invoked with the exception.
     * The {@code consumer} is ran with the same synchronicity as this Promise.
     * @param consumer The function body that is invoked upon rejection
     */
    public void except(Consumer<Exception> consumer) {
        except(result -> {
            consumer.accept(result);
            return null;
        }, async);
    }

    /**
     * When this Promise is finished in any way, the {@code runnable} is invoked.
     * The {@code runnable} is ran with with the given synchronicity.
     * @param runnable The function body that is invoked upon finishing
     * @param async Whether to run the {@code runnable} asynchronously or not
     */
    public synchronized void always(Runnable runnable, boolean async) {
        // If we have already finished, just run the consumer-
        // but be careful to run it with the correct synchronicity!
        if (state != PENDING) {
            schedule(runnable, async);
        }

        // Otherwise, we add it to the queues.
        onResolve.add(result -> schedule(runnable, async));
        onReject.add(ex -> schedule(runnable, async));
    }

    /**
     * When this Promise is finished in any way, the {@code runnable} is invoked.
     * The {@code runnable} is ran with the same synchronicity as this Promise.
     * @param runnable The function body that is invoked upon finishing
     */
    public void always(Runnable runnable) {
        always(runnable, async);
    }

    /**
     * Waits for this Promise to resolve or reject.
     * @return The PromiseState of this Promise after awaiting
     */
    public boolean await() {
        // Make sure execution is scheduled if it isn't already.
        this.schedule();

        // Wait for the countdown to reach zero.
        try {
            latch.await();
        } catch (Exception ex) {
            throw new RuntimeException("Promise.await() was interrupted", ex);
        }
        return state == RESOLVED;
    }

    /**
     * Schedule this Promise for execution manually.
     * Does nothing if this Promise has already been scheduled.
     * @return This Promise
     */
    public synchronized Promise<TResult> schedule() {
        runBody();
        return this;
    }

    private synchronized void schedule(Runnable r) {
        // Only schedule if we haven't been scheduled yet!
        if (scheduled || state != PENDING) {
            return;
        }
        scheduled = true;

        // Actually schedule.
        schedule(r, async);
    }

    private void schedule(Runnable r, boolean async) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(BuildFramework.getInstance(), r);
        } else {
            Bukkit.getScheduler().runTask(BuildFramework.getInstance(), r);
        }
    }

    private synchronized void runBody() {
        schedule(() -> {
            // If the runner throws an exception, catch it and reject this Promise.
            try {
                runner.run(finisher);
            } catch (Exception ex) {
                finisher.reject(ex);
            }
        });
    }

    private synchronized void runFailure(Exception ex) {
        schedule(() -> handleReject(ex));
    }

    private synchronized void handleResolve(TResult t) {
        if (state != PENDING) {
            return;
        }

        // Store the result and mark ourselves as resolved.
        // We also notify any threads waiting for this Promise to finish.
        state = RESOLVED;
        result = t;
        latch.decrement();

        // Invoke all functions waiting on this Promise being resolved.
        for (Consumer<TResult> next : onResolve) {
            next.accept(t);
        }
    }

    private synchronized void handleReject(Exception ex) {
        if (state != PENDING) {
            return;
        }

        // Store the exception and mark ourselves as rejected.
        // We also notify any threads waiting for this Promise to finish.
        state = REJECTED;
        exception = ex;
        latch.decrement();

        // If there are no exception handlers registered on this Promise, throw the exception.
        if (onReject.size() == 0 && ex != null) {
            throw new UnhandledException("Promise failed with unhandled exception", ex);
        }

        // Invoke all functions waiting on this Promise being rejected.
        for (Consumer<Exception> next : onReject) {
            next.accept(ex);
        }
    }

    /**
     * Schedule the given Promises in sequence. This means that any given Promise
     * is only scheduled when all Promises before it have been resolved.
     * If any Promise is rejected, the result Promise is also rejected and any
     * following Promises are not executed.
     * @param promises The Promises to schedule in sequence
     * @return A map from input Promise to result
     */
    public static Promise<Map<Promise, Object>> sequence(Promise<?>... promises) {
        return sequence(true, promises);
    }

    /**
     * Schedule the given Promises in sequence. This means that any given Promise
     * is only scheduled when all Promises before it have been resolved.
     * If any Promise is rejected, the result Promise is also rejected and any
     * following Promises are not executed.
     * @param immediate Whether this Promise should run immediately
     * @param promises The Promises to schedule in sequence
     * @return A map from input Promise to result
     */
    public static Promise<Map<Promise, Object>> sequence(boolean immediate, Promise<?>... promises) {
        return new Promise<>(finisher -> {
            Map<Promise, Object> results = new HashMap<>();
            for (Promise<?> p : promises) {
                p.schedule();
                if (!p.await()) {
                    finisher.reject(p.exception);
                }
                results.put(p, p.result);
            }
            finisher.resolve(results);
        }, true, immediate);
    }

    /**
     * Schedule all given Promises, returning a Promise which is resolved as soon as
     * any one of the given Promises has been resolved. If any Promise is rejected,
     * the result Promise is also rejected.
     * @param promises The Promises to schedule
     * @return The first returned result by any of the given Promises
     */
    public static Promise<Object> any(Promise<?>... promises) {
        return any(true, promises);
    }

    /**
     * Schedule all given Promises, returning a Promise which is resolved as soon as
     * any one of the given Promises has been resolved. If any Promise is rejected,
     * the result Promise is also rejected.
     * @param immediate Whether this Promise should run immediately
     * @param promises The Promises to schedule
     * @return The first returned result by any of the given Promises
     */
    public static Promise<Object> any(boolean immediate, Promise<?>... promises) {
        return new Promise<>(finisher -> {
            for (Promise<?> p : promises) {
                p.then(finisher::resolve);
                p.except(finisher::reject);
                p.schedule();
            }
        }, true, immediate);
    }

    /**
     * Schedule all given Promises, returning a Promise which is resolved as soon as
     * all of the given Promises have been resolved. If any Promise is rejected,
     * the resulting Promise is also rejected.
     * @param promises The Promises to schedule
     * @return A map from input Promise to result
     */
    public static Promise<Map<Promise, Object>> all(Promise<?>... promises) {
        return all(true, promises);
    }

    /**
     * Schedule all given Promises, returning a Promise which is resolved as soon as
     * all of the given Promises have been resolved. If any Promise is rejected,
     * the resulting Promise is also rejected.
     * @param immediate Whether this Promise should run immediately
     * @param promises The Promises to schedule
     * @return A map from input Promise to result
     */
    public static Promise<Map<Promise, Object>> all(boolean immediate, Promise<?>... promises) {
        return new Promise<>(finisher -> {
            Map<Promise, Object> results = new HashMap<>();
            CountDownLatch latch = new CountDownLatch(promises.length);
            for (Promise<?> p : promises) {

                // Register success and failure handlers on the promise
                p.then(result -> {
                    results.put(p, result);
                    synchronized (latch) {
                        latch.countDown();
                        if (latch.getCount() == 0) {
                            finisher.resolve(results);
                        }
                    }
                });
                p.except(ex -> {
                    finisher.reject(ex);
                });

                // Schedule it for execution
                p.schedule();
            }
        }, true, immediate);
    }

    /**
     * Create a resolved Promise with {@code t} as the result value.
     * @param t The result value to use
     * @param <TResult> The type of the result value
     * @return The resolved Promise
     */
    public static <TResult> Promise<TResult> resolve(TResult t) {
        Promise<TResult> p = new Promise<>(null, false, false);
        p.state = RESOLVED;
        p.result = t;
        return p;
    }

    /**
     * Create a rejected Promise with {@code ex} as the exception.
     * @param ex The exception to use
     * @param <TResult> The type of the result value
     * @return The resolved Promise
     */
    public static <TResult> Promise<TResult> reject(Exception ex) {
        Promise<TResult> p = new Promise<>(null, false, false);
        p.state = REJECTED;
        p.exception = ex;
        return p;
    }
}
