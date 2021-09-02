package com.zzp.dtrip.data

data class FaceResult(
    val code: Int,
    val `data`: String,
    val errorMessage: String,
    val isError: Boolean
)