package com.kevadiyakrunalk.rxphotopicker;

/**
 * The interface Photo interface.
 *
 * @param <T> the type parameter
 */
public interface PhotoInterface<T> {
    /**
     * On photo result.
     *
     * @param t the t
     */
    public void onPhotoResult(T t);
}
