package com.kevadiyakrunalk.commonutils.common;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * The type ConnectionUtil.
 */
public class ConnectionUtil {
    private static ConnectionUtil sSingleton;

    private Context context;

    /**
     * Instantiates a new Connection util.
     *
     * @param ctx the ctx
     */
    private ConnectionUtil(Context ctx) {
        context = ctx;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static ConnectionUtil getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (ConnectionUtil.class) {
                sSingleton = new ConnectionUtil(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public Boolean isConnected() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
            if ( conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected() ) {
                return true;
            } else {
                return false;
            }
        } catch ( Exception e ) {
            return false;
        }
    }
}