package com.get.vpn.utils;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by istmedia-m1 on 6/8/17.
 */

public class FirebaseHelper {

    private static String TAG="FirebaseHelper";

    private static String EVENT_SERVER = "server_info";
    private static String PARAM_SERVER_SELECTFROM = "server_select_from";
    private static String PARAM_SERVER_SELECT_AUTO = "server_select_auto";
    private static String PARAM_SERVER_SELECT_USER = "server_select_user";
    private static String PARAM_SERVER_CONNECT = "server_connect";
    private static String PARAM_SERVER_CONNECTED = "server_connected";
    private static String PARAM_SERVER_DISCONNECT = "server_disconnect";
    private static String EVENT_USER = "user_info";
    private static String PARAM_USER_LANGUAGE = "user_language";

    private static String mDefaultCountry = Locale.getDefault().getCountry();

    private static Map<String, String> mArControy= new HashMap<String, String>() {{
        put("EG", "EG_server_info");
        put("SD", "SD_server_info");
        put("DZ", "DZ_server_info");
        put("IQ", "IQ_server_info");
        put("MA", "MA_server_info");
        put("SA", "SA_server_info");
        put("YE", "YE_server_info");
        put("SY", "SY_server_info");
        put("TN", "TN_server_info");
        put("AE", "AE_server_info");
        put("LB", "LB_server_info");
        put("JO", "JO_server_info");
        put("LY", "LY_server_info");
        put("OM", "OM_server_info");
        put("KW", "KW_server_info");
        put("QA", "QA_server_info");
        put("BH", "BH_server_info");
        put("PH", "PH_server_info");
    }};

    public static void logServerSelectFrom(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_SELECTFROM, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);
    }

    public static void logServerSelectAuto(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_SELECT_AUTO, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);

        Log.i(TAG, "SelectAuto");

    }
    public static void logServerSelectUser(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_SELECT_USER, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);
    }

    public static void logServerConnect(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_CONNECT, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);
    }

    public static void logServerDisconnect(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_DISCONNECT, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);
    }

    public static void logServerConnected(String strServerName, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_SERVER_CONNECTED, strServerName);
        mFirebaseAnalytics.logEvent(EVENT_SERVER, bundle);
        _logServerFromArCountry(bundle, mFirebaseAnalytics);
    }

    private static void _logServerFromArCountry(Bundle bundle, FirebaseAnalytics mFirebaseAnalytics) {
    /*    if (mArControy.containsKey(mDefaultCountry)) {
            mFirebaseAnalytics.logEvent(mArControy.get(mDefaultCountry), bundle);
        }
    */
    }

    public static void logUserLanguage(String strLanguage, FirebaseAnalytics mFirebaseAnalytics) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_USER_LANGUAGE, strLanguage);
        mFirebaseAnalytics.logEvent(EVENT_USER, bundle);

        Log.i(TAG, PARAM_USER_LANGUAGE);
    }
}
