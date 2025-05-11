package com.example.gameinfoquerydemo.repository

import com.example.gameinfoquerydemo.api.GameApi
import com.example.gameinfoquerydemo.model.GameInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class GameRepository {
    private val baseUrl = "https://hotfix-service-prod.g.mi.com/"
    
    private val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
            
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val gameApi: GameApi by lazy {
        retrofit.create(GameApi::class.java)
    }
    
    suspend fun getGameInfo(gameId: String): GameInfo {
        try {
            val response = gameApi.getGameInfo(gameId)
            if (response.code == 200) {
                return response.data
            } else {
                throw GameException(response.msg ?: "获取游戏信息失败")
            }
        } catch (e: IOException) {
            when (e) {
                is UnknownHostException -> throw GameException("网络连接失败，请检查网络设置")
                is SocketTimeoutException -> throw GameException("请求超时，请稍后重试")
                else -> throw GameException("网络错误，请稍后重试")
            }
        } catch (e: Exception) {
            throw GameException(e.message ?: "未知错误")
        }
    }
}

class GameException(message: String) : Exception(message) 