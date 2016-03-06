package com.bookstore.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/3/6.
 */
public class NetWorkUtil {

    public NetWorkUtil(Context context) {
    }

    public static boolean IsWifiConnection(Context context) {
        ConnectivityManager conectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean IsDataConnection() {

        return true;
    }
}
