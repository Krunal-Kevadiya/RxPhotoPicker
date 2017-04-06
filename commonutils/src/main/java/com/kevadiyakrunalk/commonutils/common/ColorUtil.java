package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.graphics.Color;

/**
 * The type ColorUtil.
 */
public class ColorUtil {

    private static ColorUtil sSingleton;

    /**
     * Instantiates a new Color util.
     *
     * @param ctx the ctx
     */
    private ColorUtil(Context ctx) {

    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static ColorUtil getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (ColorUtil.class) {
                sSingleton = new ColorUtil(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Int to argb string.
     *
     * @param i the
     * @return string string
     */
    public String intToARGB( int i ) {
        return Integer.toHexString( ( ( i >> 16 ) & 0xFF ) ) +
                Integer.toHexString( ( ( i >> 8 ) & 0xFF ) ) +
                Integer.toHexString( ( i & 0xFF ) );
    }

    /**
     * Gets darker color.
     *
     * @param colorCode the color code
     * @return the darker color
     */
    public int getDarkerColor(String colorCode) {
        float[] hsv = new float[3];
        int color = Color.parseColor(colorCode);
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        color = Color.HSVToColor(hsv);
        return color;
    }

    /**
     * Gets darker color.
     *
     * @param colorCode the color code
     * @return the darker color
     */
    public int getDarkerColor(int colorCode) {
        float[] hsv = new float[3];
        Color.colorToHSV(colorCode, hsv);
        hsv[2] *= 0.8f; // value component
        colorCode = Color.HSVToColor(hsv);
        return colorCode;
    }

    /**
     * Gets tab indicator darker color.
     *
     * @param colorCode the color code
     * @param factor    the factor
     * @return the tab indicator darker color
     */
    public int getTabIndicatorDarkerColor(int colorCode, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(colorCode, hsv);
        hsv[2] *= factor; // value component
        colorCode = Color.HSVToColor(hsv);
        return colorCode;
    }

    /**
     * Lighter int.
     *
     * @param color  the color
     * @param factor the factor
     * @return the int
     */
    public int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }
}