package com.zzp.dtrip.data

data class CurrentAddressResult(
    val message: String,
    val request_id: String,
    val result: Result,
    val status: Int
)