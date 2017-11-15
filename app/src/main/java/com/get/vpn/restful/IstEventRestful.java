package com.get.vpn.restful;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.get.vpn.utils.AppInfo;

import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by istmedia-m1 on 6/20/17.
 */

public class IstEventRestful {

    private static final String API_URL = "http://app.imobiletracking.net";
    private static final String API_URL_TTL = "http://log.fastvd.com";


    public static Call LogInstall(String uuid, Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).build();
        IstEventApi istApi = retrofit.create(IstEventApi.class);

        Call<ResponseBody> call = istApi.logInstall(uuid);
        call.enqueue(callback);
        return  call;
    }

    public static Call LogStart(String uuid, Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).build();
        IstEventApi istApi = retrofit.create(IstEventApi.class);

        Call<ResponseBody> call = istApi.logStart(uuid);
        call.enqueue(callback);
        return call;
    }

    public static Call logTtl(String strServer, String strIp, String strTtl, Context context, Callback<ResponseBody> callback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL_TTL).build();
        IstEventApi istApi = retrofit.create(IstEventApi.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", AppInfo.getUUID(context));
        jsonObject.put("geo", AppInfo.getGeo());
        jsonObject.put("server", strServer);
        jsonObject.put("ip", strIp);
        jsonObject.put("ttl", strTtl);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);

        String strJsonBody = jsonObject.toString();

        Call<ResponseBody> call =  istApi.logTtl(strJsonBody);
        call.enqueue(callback);
        return call;
    }
}