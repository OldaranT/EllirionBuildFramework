package com.ellirion.buildframework.util;

public interface IPromiseFinisher<T> {

    /**
     * Resolve the Promise.
     * @param t The resulting object
     */
    void resolve(T t);

    /**
     * Reject the Promise.
     * @param ex An optional Exception to error handlers
     */
    void reject(Exception ex);
}
