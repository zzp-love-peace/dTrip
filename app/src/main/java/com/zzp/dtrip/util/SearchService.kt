package com.zzp.dtrip.util

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search")
    fun getNearbySearchData(@Query("keyword") keyword: String,
                            @Query("boundary") boundary: String,
                            @Query("key") key: String): Call<SearchAPIHelper.Response<List<SearchAPIHelper.NearbySearchData>>>
}