package com.ptkebonagung.gotani.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {

    /*deklarasi key data untuk penyimpanan data session*/
    private static final String KEY_EMAIL_REGISTERED        = "email";
    private static final String KEY_USER_ID                 = "user_id";
    private static final String KEY_STATUS_IS_LOGGED_IN     = "user_is_logged_in";
    private static final String KEY_API                     = "api_user";
    private static final String FCM_TOKEN                   = "fcm_token";
    private static final String FCM_IS_REGISTERED           = "fcm_is_registered";
    private static final String STORAGE_PERMISSION          = "storage_permission";

    /* deklarasi sharedpreference berdasarkan key yang ada pada context */
    public static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*fungsi untuk setter dan getter KEY_EMAIL_REGISTERED di shared preferences */
    public static void setKeyEmailRegistered(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_EMAIL_REGISTERED, username);
        editor.apply();
    }

    public static String getKeyEmailRegistered(Context context){
        return getSharedPreferences(context).getString(KEY_EMAIL_REGISTERED, "");
    }

    /*fungsi untuk setter dan getter KEY_USER_ID*/
    public static void setKeyUserId(Context context, String userID){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_ID, userID);
        editor.apply();
    }

    public static String getKeyUserId(Context context){
        return getSharedPreferences(context).getString(KEY_USER_ID,"");
    }

    /*fungsi untuk setter dan getter KEY_STATUS_IS_LOGGED_IN*/
    public static void setKeyStatusIsLoggedIn(Context context, boolean isLogin){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_STATUS_IS_LOGGED_IN, isLogin);
        editor.apply();
    }

    public static boolean getKeyStatusIsLoggedIn(Context context){
        return getSharedPreferences(context).getBoolean(KEY_STATUS_IS_LOGGED_IN,false);
    }

    /*fungsi untuk setter dan getter KEY_API*/
    public static void setKeyAPI(Context context, String keyAPI){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_API, keyAPI);
        editor.apply();
    }

    public static String getKeyApi(Context context) {
        return getSharedPreferences(context).getString(KEY_API,"");
    }

    /*fungsi untuk setter dan getter FCM_TOKEN*/
    public static void setFcmToken(Context context, String token){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(FCM_TOKEN, token);
        editor.apply();
    }

    public static String getFcmToken(Context context) {
        return getSharedPreferences(context).getString(FCM_TOKEN,"");
    }

    /*fungsi untuk setter dan getter KEY_STATUS_IS_LOGGED_IN*/
    public static void setFcmIsRegistered(Context context, boolean isNotification){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(FCM_IS_REGISTERED, isNotification);
        editor.apply();
    }

    public static boolean getFcmIsRegistered(Context context){
        return getSharedPreferences(context).getBoolean(FCM_IS_REGISTERED,false);
    }


    public static void clearFCMToken(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(FCM_TOKEN);
        editor.apply();
    }

    public static boolean getStoragePermission(Context context) {
        return getSharedPreferences(context).getBoolean(STORAGE_PERMISSION, false);
    }

    /*fungsi untuk hapus data dengan KEY STATUS IS LOGGED IN */
    public static void clearLoggedInDataUser (Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_EMAIL_REGISTERED);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_STATUS_IS_LOGGED_IN);
        editor.remove(KEY_API);
        editor.apply();
    }

}
