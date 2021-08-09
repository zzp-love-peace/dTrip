package com.zzp.dtrip.data

data class Data(
    val adcode: Int,
    val address: String,
    val category: String,
    val city: String,
    val district: String,
    val id: String,
    val location: Location,
    val province: String,
    val title: String,
    val type: Int
)