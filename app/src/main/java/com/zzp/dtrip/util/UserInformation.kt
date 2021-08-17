package com.zzp.dtrip.util

object UserInformation {
    var username = ""
    var password = ""
    var sex = ""
    var ID = -1
    var isLogin = false

    fun setDataNull() {
        username = ""
        password = ""
        sex = ""
        ID = -1
        isLogin = false
    }
}