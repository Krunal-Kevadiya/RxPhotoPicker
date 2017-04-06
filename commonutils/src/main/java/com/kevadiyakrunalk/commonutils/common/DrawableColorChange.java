package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

/**
 * The type Drawable color change.
 */
public class DrawableColorChange {
    private static DrawableColorChange sSingleton;

    /**
     * The Context.
     */
    private Context context;

    /**
     * Instantiates a new Drawable color change.
     *
     * @param context the context
     */
    private DrawableColorChange(Context context) {
        this.context = context;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static DrawableColorChange getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (DrawableColorChange.class) {
                sSingleton = new DrawableColorChange(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Change color drawable.
     *
     * @return the drawable
     */
    public Drawable changeColor(Drawable drawable, Integer color) {
        if (drawable == null) {
            throw new NullPointerException("Drawable is null. Please set drawable by setDrawable() method");
        } else if (color == null) {
            throw new NullPointerException("Color is null. Please set color by setColor() or setColorResID() method");
        }
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        return drawable;
    }

    /**
     * Change color by id drawable.
     *
     * @param drawableResId the drawable res id
     * @param colorResId    the color res id
     * @return the drawable
     */
    public Drawable changeColorById(@DrawableRes Integer drawableResId, @ColorRes Integer colorResId) {
        return changeColor(ContextCompat.getDrawable(context, drawableResId), ContextCompat.getColor(context, colorResId));
    }

    /**
     * Change color by id drawable.
     *
     * @param drawable   the drawable
     * @param colorResId the color res id
     * @return the drawable
     */
    public Drawable changeColorById(Drawable drawable, @ColorRes Integer colorResId) {
        return changeColor(drawable, ContextCompat.getColor(context, colorResId));
    }

    /**
     * Change color by color drawable.
     *
     * @param drawableResId the drawable res id
     * @param color         the color
     * @return the drawable
     */
    public Drawable changeColorByColor(@DrawableRes Integer drawableResId, @ColorInt Integer color) {
        return changeColor(ContextCompat.getDrawable(context, drawableResId), color);
    }

    /**
     * Change color by color drawable.
     *
     * @param drawable the drawable
     * @param color    the color
     * @return the drawable
     */
    public Drawable changeColorByColor(Drawable drawable, @ColorInt Integer color) {
        return changeColor(drawable, color);
    }
}