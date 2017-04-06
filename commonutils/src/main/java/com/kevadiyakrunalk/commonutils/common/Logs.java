package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.util.Log;

/**
 * The type Logs.
 */
public class Logs {
    private static Logs sSingleton;
    private Context context;

    private Logs(Context ctx) {
        context = ctx;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static Logs getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (Logs.class) {
                sSingleton = new Logs(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Log verbose extendido
     *
     * @param tag  the tag
     * @param text the text
     */
    public void verbose(String tag, String text) {
        try {
            Log.v(tag, getFormattedLogLine() + text);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Log debug extendido
     *
     * @param tag  the tag
     * @param text the text
     */
    public void debug(String tag, String text) {
        try {
            Log.d(tag, getFormattedLogLine() + text);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Log info extendido
     *
     * @param tag  the tag
     * @param text the text
     */
    public void info(String tag, String text) {
        try {
            Log.i(tag, getFormattedLogLine() + text);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Log warn extendido
     *
     * @param tag  the tag
     * @param text the text
     */
    public void warn(String tag, String text) {
        try {
            Log.w(tag, getFormattedLogLine() + text);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Log error extendido
     *
     * @param tag  the tag
     * @param text the text
     */
    public void error(String tag, String text) {
        try {
            Log.e(tag, getFormattedLogLine() + text);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Debug.
     *
     * @param tag       the tag
     * @param what      the what
     * @param bytes     the bytes
     * @param bytetohex the bytetohex
     */
    public void debug(String tag, String what, byte[] bytes, String bytetohex) {
        try {
            Log.d(tag, what + "[" + bytes.length + "] [" + bytetohex + "]");
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Debug.
     *
     * @param tag   the tag
     * @param what  the what
     * @param value the value
     */
    public void debug(String tag, String what, String value) {
        try {
            Log.d(tag, what + "[" + value.length() + "] [" + value + "]");
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String getFormattedLogLine() {
        String className = Threads.getInstance(context).getCallerClassName(5);
        String methodName = Threads.getInstance(context).getCallerMethodName(5);
        int lineNumber = Threads.getInstance(context).getCallerLineNumber(5);
        return className + "." + methodName + "():" + lineNumber + ": ";
    }
}
