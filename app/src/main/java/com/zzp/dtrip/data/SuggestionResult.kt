package com.zzp.dtrip.data


data class SuggestionResult(
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val request_id: String,
    val status: Int
)