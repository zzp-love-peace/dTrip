package com.zzp.dtrip.data

data class NearbyResult(
    val count: Int,
    val `data`: List<DataXX>,
    val message: String,
    val region: Region,
    val request_id: String,
    val status: Int
)