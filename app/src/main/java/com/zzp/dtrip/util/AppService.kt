package com.zzp.dtrip.util

import com.zzp.dtrip.body.*
import com.zzp.dtrip.data.LoginResult
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.javabean.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*

interface AppService {
    @POST("/user/login")
    fun postLogin(@Body loginBody: LoginBody) : Call<LoginResult>

    @POST("/user/register")
    fun postRegister(@Body registerBody: RegisterBody) : Call<NormalResult>

    @POST("/user/updateUsn")
    fun postUsername(@Body usernameBody: UsernameBody) : Call<NormalResult>

    @POST("/user/updateSex")
    fun postSex(@Body sexBody: SexBody) : Call<NormalResult>

    @POST("/user/updatePsw")
    fun postPassword(@Body passwordBody: PasswordBody) : Call<NormalResult>


    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/face/compareFace")
    fun compareFace(@Body ask: compareFaceAsk): Call<compareFaceResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/face/addFace")
    fun addFace(@Body ask: compareFaceAsk): Call<compareFaceResponse>

}