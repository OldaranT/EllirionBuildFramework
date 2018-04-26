package com.ellirion.buildframework.util;

import lombok.Getter;
import org.apache.commons.lang.UnhandledException;
import org.bukkit.Bukkit;
import com.ellirion.buildframework.BuildFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.ellirion.buildframework.util.Promise.State.*;

public class Promise<TResult> {

    private State state;
    @Getter private TResult result;
    @Getter private Exception exception;

    private List<Consumer<TResult>> onResolve;
    private List<Consumer<Exception>> onReject;

    private IPromiseBody<TResult> runner;
    private IPromiseFinisher<TResult> finisher;

    private boolean async;

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

    private Promise(final IPromiseBody<TResult> runner, final boolean async, final boolean run) {
        this.state = PENDING;
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

        this.async = async;

        // Schedule (sync or async) the invocation of our runner if requested.
        if (run) {
            schedule(this::runBody);
        }
    }

    /**
     * Upon the resolving of this Promise, the {@code consumer} will be synchronously invoked with the
     * result that caused this Promise to resolve.
     * @param consumer The consumer function to run upon resolving
     * @return This Promise
     */
    public Promise<TResult> consumeSync(Consumer<TResult> consumer) {
        resume((result) -> {
            consumer.accept(result);
            return null;
        }, false);
        return this;
    }

    /**
     * Upon the resolving of this Promise, the {@code consumer} will be synchronously invoked with the
     * result that caused this Promise to resolve.
     * @param consumer The consumer function to run upon resolving
     * @return This Promise
     */
    public Promise<TResult> consumeAsync(Consumer<TResult> consumer) {
        resume((result) -> {
            consumer.accept(result);
            return null;
        }, true);
        return this;
    }

    /**
     * Upon the resolving of this Promise, the {@code continuer} will be synchronously invoked with the
     * result that caused this Promise to resolve.
     * @param continuer The continuer function to run upon resolving
     * @param <TNext> The return type of the {@code continuer}
     * @return A new Promise that resolves after the {@code continuer} executes, or fails when this Promise
     *         fails or if the {@code continuer} throws an exception.
     */
    public <TNext> Promise<TNext> resumeSync(IPromiseContinuer<TResult, TNext> continuer) {
        return resume(continuer, false);
    }

    /**
     * Upon the resolving of this Promise, the {@code continuer} will be synchronously invoked with the
     * result that caused this Promise to resolve.
     * @param continuer The continuer function to run upon resolving
     * @param <TNext> The return type of the {@code continuer}
     * @return A new Promise that resolves after the {@code continuer} executes, or fails when this Promise
     *         fails or if the {@code continuer} throws an exception.
     */
    public <TNext> Promise<TNext> resumeAsync(IPromiseContinuer<TResult, TNext> continuer) {
        return resume(continuer, true);
    }

    private <TNext> Promise<TNext> resume(IPromiseContinuer<TResult, TNext> continuer, boolean async) {

        // Create the promise we will execute upon succesful finishing of this promise.
        Promise<TNext> next = new Promise<>((finisher) -> {
            TNext nextResult = null;
            try {
                // Invoke the continuer with the result of the outer promise.
                nextResult = continuer.run(result);
            } catch (Exception ex) {
                // If it fails, this promise has to reject.
                finisher.reject(ex);
            }

            // Otherwise we resolve with the continuation result.
            finisher.resolve(nextResult);
        }, async, false);

        // If we have already resolved, we need to queue the follow-up immediately!
        if (state == RESOLVED) {
            next.schedule(next::runBody);
            return next;
        }

        // If we have been rejected, it's a tad bit different: We do not invoke the regular
        // body, but we directly invoke the failure handling function of the Promise.
        if (state == REJECTED) {
            next.schedule(() -> next.runFailure(exception));
            return next;
        }

        // If this Promise resolves normally, the next Promise also needs to run normally.
        // However, if this Promise fails, then the inner Promise also needs to fail.
        onResolve.add((result) -> next.schedule(next::runBody));
        onReject.add(next::handleReject);

        return next;
    }

    /**
     * Upon rejection of this Promise, the {@code consumer} will be synchronously invoked
     * with the Exception that caused this Promise to fail.
     * @param consumer The consumer function to run upon failing
     * @return This Promise
     */
    public Promise<TResult> consumeFailSync(Consumer<Exception> consumer) {
        resumeFail((ex) -> {
            consumer.accept(ex);
            return null;
        }, false);
        return this;
    }

    /**
     * Upon rejection of this Promise, the {@code consumer} will be asynchronously invoked
     * with the Exception that caused this Promise to fail.
     * @param consumer The consumer function to run upon failing
     * @return This Promise
     */
    public Promise<TResult> consumeFailAsync(Consumer<Exception> consumer) {
        resumeFail((ex) -> {
            consumer.accept(ex);
            return null;
        }, true);
        return this;
    }

    /**
     * Upon rejection of this Promise, the {@code continuer} will be synchronously invoked
     * with the Exception that caused this Promise to fail.
     * @param continuer The continuer function to run upon failing
     * @param <TNext> The type returned by the continuer function
     * @return A Promise that may be resolved with the result of the continuer function
     */
    public <TNext> Promise<TNext> resumeFailSync(IPromiseContinuer<Exception, TNext> continuer) {
        return resumeFail(continuer, false);
    }

    /**
     * Upon rejection of this Promise, the {@code continuer} will be asynchronously invoked
     * with the Exception that caused this Promise to fail.
     * @param continuer The continuer function to run upon failing
     * @param <TNext> The type returned by the continuer function
     * @return A Promise that may be resolved with the result of the continuer function
     */
    public <TNext> Promise<TNext> resumeFailAsync(IPromiseContinuer<Exception, TNext> continuer) {
        return resumeFail(continuer, true);
    }

    private <TNext> Promise<TNext> resumeFail(IPromiseContinuer<Exception, TNext> continuer, boolean async) {

        // Create the promise we will execute upon the succesful finishing of this promise.
        Promise<TNext> next = new Promise<>((finisher) -> {
            TNext result = null;
            try {
                // Invoke the continuer with the result of the outer promise.
                result = continuer.run(exception);
            } catch (Exception ex) {
                // If it fails, this promise has to reject.
                finisher.reject(ex);
            }

            // Otherwise we resolve with the continuation result.
            finisher.resolve(result);
        }, async, false);

        // If we have already resolved, we can just stop right here. The 'next' promise
        // will never be resolved.
        if (state == RESOLVED) {
            return next;
        }

        // If we have been rejected, schedule the execution of the next body.
        if (state == REJECTED) {
            next.schedule(next::runBody);
            return next;
        }

        // If the outer (this) Promise resolves normally, the 'next' Promise is NEVER RAN.
        // However, if the outer Promise fails, then the next Promise's body is invoked.
        onReject.add((ex) -> next.schedule(next::runBody));

        return next;
    }

    private void runBody() {
        schedule(() -> runner.run(finisher));
    }

    private void runFailure(Exception ex) {
        schedule(() -> handleReject(ex));
    }

    private void schedule(Runnable r) {
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(BuildFramework.getInstance(), r);
        } else {
            Bukkit.getScheduler().runTask(BuildFramework.getInstance(), r);
        }
    }

    private void handleResolve(TResult t) {
        if (state != PENDING) {
            return;
        }

        state = RESOLVED;
        result = t;

        for (Consumer<TResult> next : onResolve) {
            next.accept(t);
        }
    }

    private void handleReject(Exception ex) {
        if (state != PENDING) {
            return;
        }

        state = REJECTED;
        exception = ex;

        if (onReject.size() == 0 && ex != null) {
            throw new UnhandledException("Promise failed with unhandled exception", ex);
        }

        for (Consumer<Exception> next : onReject) {
            next.accept(ex);
        }
    }

    private enum State {
        PENDING,
        RESOLVED,
        REJECTED
    }
}
