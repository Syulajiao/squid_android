package com.get.vpn.service;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.get.vpn.model.UserModel;
import com.get.vpn.model.VpnModel;
import com.get.vpn.utils.SharePrefHelper;

/**
 * @author yu.jingye
 * @version created at 2016/10/2.
 */
public class VpnService {

    private static final String LOGIN_USER = "login_user";
    private static final String LOGIN_PWD = "login_pwd";
    private static final String LOGIN_TIME = "login_time";
    private static final String LAST_VPN = "last_vpn";

    public static String getLastToken(Context context) {
        UserModel userModel = getLoginUserModel(context);
        if (userModel == null) {
            return "";
        } else {
            return userModel.getToken();
        }
    }

    public static UserModel getLoginUserModel(Context context) {
        String json = SharePrefHelper.getInstance(context).getPref(LOGIN_USER, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            return UserModel.parseUserModel(json);
        }
    }

    public static UserModel getFreeAccount() {
        UserModel userModel = new UserModel();
/*
        userModel.setEmail("android@istmedia.com");
        userModel.setPassword("MlvMDxr5");
*/
        /*
        // sandbox
       userModel.setEmail("zhengyu@istmedia.com");
        userModel.setPassword("zhengyu1");
        */

        userModel.setEmail("wanyuan@istmedia.com");
        userModel.setPassword("Iloveyou&520");

        return userModel;
    }

    public static void setLoginUserInfo(Context context, String json) {
        SharePrefHelper.getInstance(context).setPref(LOGIN_USER, json);
    }

    public static void setLoginUserPwd(Context context, String pwd) {
        SharePrefHelper.getInstance(context).setPref(LOGIN_PWD, pwd);
    }

    public static String getLoginUserPwd(Context context) {
        return SharePrefHelper.getInstance(context).getPref(LOGIN_PWD, "");
    }

    public static void setLoginTime(Context context, long time) {
        SharePrefHelper.getInstance(context).setPref(LOGIN_TIME, time);
    }

    public static long getLastLoginTime(Context context) {
        return SharePrefHelper.getInstance(context).getPref(LOGIN_TIME, 0L);
    }

    public static void setLastVpnModel(Context context, VpnModel vpnModel) {
        SharePrefHelper.getInstance(context).setPref(LAST_VPN, vpnModel.toString());
    }

    public static VpnModel getLastVpnModel(Context context) {
        String vpnJson = SharePrefHelper.getInstance(context).getPref(LAST_VPN, "");
        if (TextUtils.isEmpty(vpnJson))
            return null;
        else
            return VpnModel.parseVpnModel(JSONObject.parseObject(vpnJson));
    }

}
