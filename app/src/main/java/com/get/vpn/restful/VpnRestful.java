package com.get.vpn.restful;

import android.content.Context;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.get.vpn.utils.AppInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author yu.jingye
 * @version created at 2016/10/2.
 */
public class VpnRestful {

//    private static final String API_URL = "https://client.fastvd.com/";

    // 国内用aliyun  ip
    private static final String API_URL = "http://114.215.71.131:80/";

    //private static final String API_URL = "https://proxy.fastvd.com";

    //sandbox
//    private static final String API_URL = "http://192.168.1.160:8080/";

    private static ApiEncrypt mEncrypt = new ApiEncrypt();

    public static Call login(String id, String password, String lastToken, Context context, Callback<ResponseBody> callback) {

        OkHttpClient.Builder clientBuilder = ClientSSL.getClientBuilder(context);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).client(clientBuilder.build()).build();
        VpnApi vpnApi = retrofit.create(VpnApi.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("passwd", password);
        jsonObject.put("lasttoken", lastToken);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);

        _makeCommonParam(jsonObject, context);

        String strJsonBody = jsonObject.toString();
        byte[] srcJson = null;
        try {
            srcJson = strJsonBody.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("VpnRestful", "getBytes('UTF-8') exception!" );
        };
        byte[] byteBody = mEncrypt.Encrypt(srcJson);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), byteBody);

        Call<ResponseBody> call = vpnApi.login(body);
        call.enqueue(callback);
        return call;
    }

    public static Call logout(String id, String token, Context context, Callback<ResponseBody> callback) {
        OkHttpClient.Builder clientBuilder = ClientSSL.getClientBuilder(context);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).client(clientBuilder.build()).build();
        VpnApi vpnApi = retrofit.create(VpnApi.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("token", token);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);

        _makeCommonParam(jsonObject, context);

        String strJsonBody = jsonObject.toString();
        byte[] srcJson = null;
        try {
            srcJson = strJsonBody.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("VpnRestful", "getBytes('UTF-8') exception!" );
        };
        byte[] byteBody = mEncrypt.Encrypt(srcJson);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), byteBody);

        Call<ResponseBody> call = vpnApi.logout(body);
        call.enqueue(callback);
        return call;
    }

    public static Call queryServers(String id, String token, Context context, Callback<ResponseBody> callback) {
        OkHttpClient.Builder clientBuilder = ClientSSL.getClientBuilder(context);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).client(clientBuilder.build()).build();
        VpnApi vpnApi = retrofit.create(VpnApi.class);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("token", token);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);

        _makeCommonParam(jsonObject, context);

        String strJsonBody = jsonObject.toString();
        byte[] srcJson = null;
        try {
            srcJson = strJsonBody.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("VpnRestful", "getBytes('UTF-8') exception!" );
        };
        byte[] byteBody = mEncrypt.Encrypt(srcJson);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), byteBody);

        Call<ResponseBody> call = vpnApi.queryServers(body);
        call.enqueue(callback);
        return call;
    }

    public static Call detectInfo(String lineIp, long port, long ttl, Context context, Callback<ResponseBody> callback) {
        OkHttpClient.Builder clientBuilder = ClientSSL.getClientBuilder(context);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).client(clientBuilder.build()).build();
        VpnApi vpnApi = retrofit.create(VpnApi.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("line-ip", lineIp);
        jsonObject.put("line-port", port);
        jsonObject.put("line-ttl", ttl);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);

        _makeCommonParam(jsonObject, context);

        String strJsonBody = jsonObject.toString();
        byte[] srcJson = null;
        try {
            srcJson = strJsonBody.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("VpnRestful", "getBytes('UTF-8') exception!" );
        }
        byte[] byteBody = mEncrypt.Encrypt(srcJson);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), byteBody);

        Call<ResponseBody> call = vpnApi.detectInfo(body);
        call.enqueue(callback);
        return call;
    }


    public static Call heartBeatReport(String id, String token, String lineIp, Context context, Callback<ResponseBody> callback) {
        OkHttpClient.Builder clientBuilder = ClientSSL.getClientBuilder(context);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL).client(clientBuilder.build()).build();
        VpnApi vpnApi = retrofit.create(VpnApi.class);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("token", token);
        jsonObject.put("time", System.currentTimeMillis() / 1000L);
        jsonObject.put("lineip", lineIp);

        _makeCommonParam(jsonObject, context);

        String strJsonBody = jsonObject.toString();
        byte[] srcJson = null;
        try {
            srcJson = strJsonBody.getBytes("UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("VpnRestful", "getBytes('UTF-8') exception!" );
        }
        byte[] byteBody = mEncrypt.Encrypt(srcJson);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), byteBody);

        Call<ResponseBody> call = vpnApi.heartBeatReport(body);
        call.enqueue(callback);
        return call;
    }

    public static String DecryptResponse(Response<ResponseBody> response) {
        if (null == response || null == mEncrypt) {
            return null;
        }
        byte[] body = null;
        long length = 0;
        try {
            body = response.body().bytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] byteBody = mEncrypt.Decrypt(body);
        String retBody = null;
        try {
            retBody = new String(byteBody, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            retBody = null;
        }

        return retBody;
    }

    private static void _makeCommonParam(JSONObject jsonObj, Context context) {
        String strAppVersion = AppInfo.getAppVersion(context);
        String strOsVersion = AppInfo.getOsVersion();
        String strDeviceModel = AppInfo.getDeviceModel();
        String strUUID = AppInfo.getUUID(context);

    //    Log.i("param:", strAppVersion+" "+strOsVersion+" "+strDeviceModel+" "+strUUID);

        jsonObj.put("app-version", strAppVersion);
        jsonObj.put("os-version", strOsVersion);
        jsonObj.put("uuid", strUUID);
        jsonObj.put("device-model", strDeviceModel);
        jsonObj.put("os", "android");
    }

}
