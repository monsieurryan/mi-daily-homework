package com.example.brvahgallerydemo.api

import retrofit2.http.GET

interface DBApi {
    @GET("api/douban/list?listname=top250")
    suspend fun getTop250Movies(): DBResponse
}

data class DBResponse(
    val code: Int,
    val data: DBData,
    val msg: String
)

data class DBData(
    val count: Int,
    val list: List<DBMovie>,
    val listname: String,
    val start: Int
)

data class DBMovie(
    val average: Double,
    val id: String,
    val no: Int,
    val poster: String,
    val stars: String,
    val title: String
)
