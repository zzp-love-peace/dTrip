package com.zzp.dtrip.util.javabean

data class ResultData(
    val code: Int,
    val `data`: Data,
    val errorMessage: String,
    val isError: Boolean
)

class Data(
)