package com.kevadiyakrunalk.commonutils.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.kevadiyakrunalk.commonutils.internal.Preconditions;

/**
 * A target view on which an event occurred (e.g., click).
 * <p>
 * <strong>Warning:</strong> Instances keep a strong reference to the view. Operators that cache
 * instances have the potential to leak the associated {@link Context}.
 */
public abstract class ViewEvent<T extends View> {
    private final T view;

    protected ViewEvent(@NonNull T view) {
        this.view = Preconditions.checkNotNull(view, "view == null");
    }

    /**
     * The view from which this event occurred.
     */
    @NonNull
    public T view() {
        return view;
    }
}