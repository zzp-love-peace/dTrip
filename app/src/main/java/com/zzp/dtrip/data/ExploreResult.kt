package com.zzp.dtrip.data

data class ExploreResult(
    val count: Int,
    val `data`: List<DataX>,
    val message: String,
    val request_id: String,
    val status: Int
)