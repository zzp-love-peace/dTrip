package com.zzp.dtrip.data

data class Poi(
    val _dir_desc: String,
    val _distance: Int,
    val ad_info: AdInfo,
    val address: String,
    val category: String,
    val id: String,
    val location: Location,
    val title: String
)