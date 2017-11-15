package com.get.vpn.restful;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by istmedia-m1 on 6/20/17.
 */

public interface IstEventApi {
    @GET("api/v2/track?event=install&tokenid=7e79dd80ac65124386d32936c383a0dd")
    Call<ResponseBody> logInstall(@Query("imei") String id);

    @GET("api/v2/track?event=start&tokenid=7e79dd80ac65124386d32936c383a0dd")
    Call<ResponseBody> logStart(@Query("imei") String id);

    @FormUrlEncoded
    @POST("s2.php")
    Call<ResponseBody> logTtl(@Field("d") String data);

}
