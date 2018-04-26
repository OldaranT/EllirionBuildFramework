package com.ellirion.buildframework.util;

public interface IPromiseFinisher<T> {

    /**
     * Resolves the Promise.
     * @param t The resulting object
     */
    void resolve(T t);

    /**
     * Reject the Promise.
     * @param ex An optional Exception to throw.
     */
    void reject(Exception ex);
}
