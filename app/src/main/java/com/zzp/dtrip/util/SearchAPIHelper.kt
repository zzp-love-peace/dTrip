package com.zzp.dtrip.util

object SearchAPIHelper {

    private const val BASE_URL = "https://apis.map.qq.com/ws/place/v1/"

    data class Response<T>(val status: Int, val message: String, val count: Int, val request_id: String, val data: T)

    data class NearbySearchData(val id: String, val title: String, val address: String, val tel: String, val category: String, val type: Int, val location: Location, val _distance: Int, val ad_info: AdInfo)

    data class Location(val lat: Int, val lng: Int)

    data class AdInfo(val adcode: Int, val province: String, val city: String, val district: String)
}