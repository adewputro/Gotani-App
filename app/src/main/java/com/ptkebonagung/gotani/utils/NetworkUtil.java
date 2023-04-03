package com.ptkebonagung.gotani.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean getConnectivityStatus(Context context){
        boolean status = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo                 = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null){
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                status = true;
                return status;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                status = true;
                return status;
            }
        } else {
            status = false;
            return status;
        }

        return status;
    }
}
