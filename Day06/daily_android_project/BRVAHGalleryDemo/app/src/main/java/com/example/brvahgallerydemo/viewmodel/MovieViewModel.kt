package com.example.brvahgallerydemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brvahgallerydemo.api.DBApi
import com.example.brvahgallerydemo.api.DBMovie
import com.example.brvahgallerydemo.model.MovieItem
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MovieViewModel : ViewModel() {
    private val _movies = MutableLiveData<List<MovieItem>>()
    val movies: LiveData<List<MovieItem>> = _movies

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoading: LiveData<Boolean?> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .dns(okhttp3.Dns.SYSTEM)
        .retryOnConnectionFailure(true)
        .protocols(listOf(okhttp3.Protocol.HTTP_1_1, okhttp3.Protocol.HTTP_2))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d("MovieViewModel", "发送请求:")
            Log.d("MovieViewModel", "- URL: ${request.url}")
            Log.d("MovieViewModel", "- Method: ${request.method}")
            Log.d("MovieViewModel", "- Headers: ${request.headers}")
            Log.d("MovieViewModel", "- Protocol: ${chain.connection()?.protocol()}")
            
            val response = chain.proceed(request)
            val responseBody = response.body?.string()
            Log.d("MovieViewModel", "收到响应:")
            Log.d("MovieViewModel", "- Status Code: ${response.code}")
            Log.d("MovieViewModel", "- Response Headers: ${response.headers}")
            Log.d("MovieViewModel", "- Response Body: $responseBody")
            
            // 重新构建响应体，因为 body 只能被读取一次
            response.newBuilder()
                .body(responseBody?.let { okhttp3.ResponseBody.create(response.body?.contentType(), it) })
                .build()
        }
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://dbapi.ypsou.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DBApi::class.java)

    fun loadMovies() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                Log.d("MovieViewModel", "开始加载电影数据")
                Log.d("MovieViewModel", "网络配置:")
                Log.d("MovieViewModel", "- 连接超时: 60秒")
                Log.d("MovieViewModel", "- 读取超时: 60秒")
                Log.d("MovieViewModel", "- 写入超时: 60秒")
                Log.d("MovieViewModel", "- 支持协议: HTTP/1.1, HTTP/2")
                
                val startTime = System.currentTimeMillis()
                val response = api.getTop250Movies()
                val endTime = System.currentTimeMillis()
                Log.d("MovieViewModel", "请求耗时: ${endTime - startTime}ms")
                
                // 输出响应详情
                Log.d("MovieViewModel", "API响应详情:")
                Log.d("MovieViewModel", "- 状态码: ${response.code}")
                Log.d("MovieViewModel", "- 消息: ${response.msg}")
                
                // 检查响应状态
                if (response.code != 0) {
                    throw Exception("API返回错误: ${response.msg}")
                }
                
                // 检查数据是否为空
                if (response.data.list.isEmpty()) {
                    throw Exception("没有获取到电影数据")
                }
                
                Log.d("MovieViewModel", "- 电影数量: ${response.data.count}")
                Log.d("MovieViewModel", "- 起始位置: ${response.data.start}")
                
                // 输出前3部电影的详细信息
                response.data.list.take(3).forEachIndexed { index, movie ->
                    Log.d("MovieViewModel", "电影 ${index + 1}:")
                    Log.d("MovieViewModel", "- ID: ${movie.id}")
                    Log.d("MovieViewModel", "- 排名: ${movie.no}")
                    Log.d("MovieViewModel", "- 标题: ${movie.title}")
                    Log.d("MovieViewModel", "- 评分: ${movie.average}")
                    Log.d("MovieViewModel", "- 海报: ${movie.poster}")
                }
                
                val movieItems = response.data.list.map { it.toMovieItem() }
                Log.d("MovieViewModel", "成功加载 ${movieItems.size} 部电影")
                _movies.value = movieItems
                _isLoading.value = false
                
            } catch (e: Exception) {
                Log.e("MovieViewModel", "加载电影数据失败", e)
                Log.e("MovieViewModel", "错误类型: ${e.javaClass.simpleName}")
                Log.e("MovieViewModel", "错误信息: ${e.message}")
                _error.value = "加载失败：${e.message}"
                _isLoading.value = false
                _movies.value = emptyList() // 清空电影列表
            }
        }
    }

    private fun DBMovie.toMovieItem() = MovieItem(
        id = id.toInt(),
        title = title,
        overview = "",  // 豆瓣 API 没有提供简介
        posterPath = poster,
        voteAverage = average,
        releaseDate = "",  // 豆瓣 API 没有提供发布日期
        backdropPath = null,  // 豆瓣 API 没有提供背景图
        genreIds = emptyList(),  // 豆瓣 API 没有提供类型
        originalLanguage = "zh",  // 默认中文
        originalTitle = title,  // 使用中文标题作为原始标题
        popularity = 0.0,  // 豆瓣 API 没有提供流行度
        video = false,  // 默认不是视频
        voteCount = 0  // 豆瓣 API 没有提供投票数
    )
}
