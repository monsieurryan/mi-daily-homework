package com.example.gameinfoquerydemo.model

data class GameResponse(
    val code: Int,
    val msg: String,
    val data: GameInfo
)

data class GameInfo(
    val id: Int,
    val gameName: String,
    val packageName: String,
    val appId: String,
    val icon: String,
    val introduction: String,
    val brief: String,
    val versionName: String,
    val apkUrl: String,
    val tags: String,
    val score: Double,
    val playNumFormat: String,
    val createTime: String
) 