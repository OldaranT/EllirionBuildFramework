package com.ellirion.buildframework.util;

@FunctionalInterface
public interface IPromiseBody<TResult> {

    /**
     * @param finisher The finisher that is used to resolve or reject the Promise
     */
    void run(IPromiseFinisher<TResult> finisher);
}
