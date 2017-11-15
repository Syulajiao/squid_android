package com.get.vpn.restful;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author yu.jingye
 * @version created at 2016/10/2.
 */
public interface VpnApi {

    @POST("api/cv2/login")
    Call<ResponseBody> login(@Body RequestBody requestBody);

    @POST("api/cv2/logout")
    Call<ResponseBody> logout(@Body RequestBody requestBody);

    @POST("api/cv2/servers")
    Call<ResponseBody> queryServers(@Body RequestBody requestBody);

    @POST("api/cv2/report")
    Call<ResponseBody> heartBeatReport(@Body RequestBody requestBody);

    @POST("api/cv2/detectinfo")
    Call<ResponseBody> detectInfo(@Body RequestBody requestBody);
}
