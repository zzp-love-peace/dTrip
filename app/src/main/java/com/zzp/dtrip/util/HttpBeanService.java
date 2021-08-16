package com.zzp.dtrip.util;

import com.zzp.dtrip.javabean.LoginAsk;
import com.zzp.dtrip.javabean.LoginResponse;
import com.zzp.dtrip.javabean.RegisterAsk;
import com.zzp.dtrip.javabean.RegisterResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface HttpBeanService {
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/login")
    Call<LoginResponse> login(@Body LoginAsk loginAsk);
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/register")
    Call<RegisterResponse> register(@Body RegisterAsk registerAsk );
}
