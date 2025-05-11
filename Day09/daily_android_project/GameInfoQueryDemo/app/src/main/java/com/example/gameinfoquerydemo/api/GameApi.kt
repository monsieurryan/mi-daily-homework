package com.example.gameinfoquerydemo.api

import com.example.gameinfoquerydemo.model.GameResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GameApi {
    @GET("quick-game/game/{gameId}")
    suspend fun getGameInfo(@Path("gameId") gameId: String): GameResponse
} 