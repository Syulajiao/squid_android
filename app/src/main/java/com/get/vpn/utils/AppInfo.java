package com.get.vpn.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.get.vpn.R;
import com.get.vpn.service.VpnService;

import java.util.Locale;

/**
 * Created by istmedia-m1 on 6/7/17.
 */

public class AppInfo {

    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info =  manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "1.0.0";
        }
    }
    public static String getUUID(Context context) {
        return Installation.id(context);
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getLanguage() {
        return Locale.getDefault().toString();
    }

    public static String getGeo() {
        return Locale.getDefault().getCountry();
    }

    public static boolean isArabic() {
        String strLan = Locale.getDefault().getLanguage();
        return strLan.equals("ar")||strLan.equals("fa");
    }

    public static String get0x06Prefix(Context context) {
        // 0x06 length(2Byte)email||uuid||os||os-version||device-model||app-version 0x03 playload(shadowsocks)
       /* String strTmp = "android_detector_email||";
        strTmp += "android_detector_uuid||";
        strTmp += "android||";
        strTmp += "7.1.1||";
        strTmp += "google||";
        strTmp += "1.0.0";
        */

        String strPrefix;
        strPrefix = VpnService.getFreeAccount().getEmail();
        strPrefix += "||";
        strPrefix += getUUID(context);
        strPrefix += "||";
        strPrefix += "android||";
        strPrefix += getOsVersion();
        strPrefix += "||";
        strPrefix += getDeviceModel();
        strPrefix += "||";
        strPrefix += getAppVersion(context);

        return strPrefix;
    }
}
