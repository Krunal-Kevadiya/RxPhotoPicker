package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;

/**
 * The type Threads.
 */
public class Threads {
    private static Threads sSingleton;

    private Threads(Context ctx) {

    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static Threads getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (Threads.class) {
                sSingleton = new Threads(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Retorna el nombre de la clase dentro de la posición del stacktrace del hilo principal
     *
     * @param stackTracePosition the stack trace position
     * @return String caller class name
     */
    public String getCallerClassName( int stackTracePosition ) {
        String fullClassName = Thread.currentThread().getStackTrace()[stackTracePosition].getClassName();
        return fullClassName.substring( fullClassName.lastIndexOf( "." ) + 1 );
    }

    /**
     * Retorna el nombre del método de la clase dentro de la posición del stacktrace del hilo principal
     *
     * @param stackTracePosition the stack trace position
     * @return String caller method name
     */
    public String getCallerMethodName( int stackTracePosition ) {
        return Thread.currentThread().getStackTrace()[stackTracePosition].getMethodName();
    }

    /**
     * Retorna el número de la línea de la clase dentro de la posición del stacktrace del hilo principal
     *
     * @param stackTracePosition the stack trace position
     * @return int caller line number
     */
    public int getCallerLineNumber( int stackTracePosition ) {
        return Thread.currentThread().getStackTrace()[stackTracePosition].getLineNumber();
    }
}