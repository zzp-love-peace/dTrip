package com.zzp.dtrip.data

data class LoginResult(
    val errorCode: Int,
    val isError: Boolean,
    val errorMsg: String,
    val user: User
)