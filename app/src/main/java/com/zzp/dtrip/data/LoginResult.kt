package com.zzp.dtrip.data

data class LoginResult(
    val errorCode: Int,
    val isError: Boolean,
    val errorMessage: String,
    val user: User
)