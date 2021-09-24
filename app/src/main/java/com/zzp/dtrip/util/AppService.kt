package com.zzp.dtrip.util

import com.zzp.dtrip.body.*
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.data.LoginResult
import com.zzp.dtrip.data.NormalResult
import retrofit2.Call
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

    @POST("/data/addData")
    fun postData(@Body addDataBody: AddDataBody) : Call<NormalResult>

    @POST("/face/addFace")
    fun postFaceData(@Body faceBody: FaceBody) : Call<FaceResult>

    @POST("/face/compareFace")
    fun compareFace(@Body cmpFaceBody: CmpFaceBody) : Call<LoginResult>

    @POST("/face/deleteFace")
    fun deleteFace(@Body deleteFaceBody: DeleteFaceBody) : Call<NormalResult>
}