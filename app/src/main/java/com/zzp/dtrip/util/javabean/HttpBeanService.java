package com.zzp.dtrip.util.javabean;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HttpBeanService {
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/login")
    Call<login> login(@Body RequestBody requestBody);
}
