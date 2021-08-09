package com.zzp.dtrip.util

import com.zzp.dtrip.data.SuggestionResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TencentAppService {
    @GET("/ws/place/v1/suggestion")
    fun getSuggestion(@Query("keyword") keyword: String,
                      @Query("region") region: String,
                      @Query("key") key: String) : Call<SuggestionResult>
}