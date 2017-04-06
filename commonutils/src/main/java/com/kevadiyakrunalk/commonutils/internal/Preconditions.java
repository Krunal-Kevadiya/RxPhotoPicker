package com.kevadiyakrunalk.commonutils.internal;

/**
 * The type Preconditions.
 */
public final class Preconditions {
    /**
     * Check argument.
     *
     * @param assertion the assertion
     * @param message   the message
     */
    public static void checkArgument(boolean assertion, String message) {
        if (!assertion) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check not null t.
     *
     * @param <T>     the type parameter
     * @param value   the value
     * @param message the message
     * @return the t
     */
    public static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    private Preconditions() {
        throw new AssertionError("No instances.");
    }
}