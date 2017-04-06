package com.kevadiyakrunalk.commonutils.internal;

import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Function;

/**
 * The type Functions.
 */
public final class Functions {
    private static final Always<Boolean> ALWAYS_TRUE = new Always<>(true);
    /**
     * The constant FUNC0_ALWAYS_TRUE.
     */
    public static final Func0<Boolean> FUNC0_ALWAYS_TRUE = ALWAYS_TRUE;
    /**
     * The constant FUNC1_ALWAYS_TRUE.
     */
    public static final Func1<Object, Boolean> FUNC1_ALWAYS_TRUE = ALWAYS_TRUE;

    private static final class Always<T> implements Func1<Object, T>, Func0<T> {
        private final T value;

        /**
         * Instantiates a new Always.
         *
         * @param value the value
         */
        Always(T value) {
            this.value = value;
        }

        @Override
        public T call(Object o) {
            return value;
        }

        @Override
        public T call() {
            return value;
        }
    }

    private Functions() {
        throw new AssertionError("No instances.");
    }
}