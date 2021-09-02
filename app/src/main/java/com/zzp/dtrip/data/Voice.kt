package com.zzp.dtrip.data

enum class Voice(val text: String) {

    LOGIN("登录"),
    FACE_RECORD("人脸录入"),
    FACE_LOGIN("人脸登录"),
    REGISTER("注册"),
    REPLACE_PASSWORD("修改密码"),
    OPEN_USER_INFO("打开个人信息"),
    OPEN_TRIP_DATA("打开出行数据"),
    OPEN_SWITCH_MATERIAL("打开居家模式"),
    OPEN_SAYING("打开可视化交流"),
    OPEN_HEARING("打开声音报警"),
    OPEN_NEARBY_SEARCH("打开周边搜索"),
    OPEN_GESTURE("打开手势识别"),
    OPEN_SYNTHESIS("打开语音合成")

}