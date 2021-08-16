package com.zzp.dtrip.util

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface AppService {
    @POST("/user/login")
    fun postLogin(@Query("username") username: String,
                    @Query("password") password: String) : Call<ResponseBody>
}