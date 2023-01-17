package com.alibaba.testable.v079.demo.lambda;

/**
 * @author jim
 */
@FunctionalInterface
public interface Function1Throwable<T, R> {
    R apply(T t) throws Throwable;
}
