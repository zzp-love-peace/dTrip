package com.zzp.dtrip.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TencentRetrofitManager {

    private const val BASE_URL = "https://apis.map.qq.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}