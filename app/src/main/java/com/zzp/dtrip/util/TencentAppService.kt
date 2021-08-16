package com.zzp.dtrip.util

import com.zzp.dtrip.data.CurrentAddressResult
import com.zzp.dtrip.data.ExploreResult
import com.zzp.dtrip.data.NearbyResult
import com.zzp.dtrip.data.SuggestionResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TencentAppService {
    @GET("/ws/place/v1/suggestion")
    fun getSuggestion(@Query("keyword") keyword: String,
                      @Query("region") region: String,
                      @Query("key") key: String) : Call<SuggestionResult>

    @GET("/ws/place/v1/explore")
    fun getExplore(@Query("boundary") boundary: String,
                   @Query("key") key: String) : Call<ExploreResult>

    @GET("/ws/place/v1/search")
    fun getNearby(@Query("keyword") keyword: String,
                      @Query("boundary") boundary: String,
                      @Query("key") key: String) : Call<NearbyResult>

    @GET("/ws/geocoder/v1/?location=")
    fun getCurrentAddress(@Query("location") location: String,
                          @Query("key") key: String) : Call<CurrentAddressResult>


}