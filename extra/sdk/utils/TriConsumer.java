package org.hobbit.sdk.utils;

@FunctionalInterface
public interface TriConsumer<T,U,S> {
    void handleCmd(T t, U u, S s) throws Exception;

}
