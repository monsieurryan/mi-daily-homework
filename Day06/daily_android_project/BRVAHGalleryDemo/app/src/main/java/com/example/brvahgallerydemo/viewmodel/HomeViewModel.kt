package com.example.brvahgallerydemo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brvahgallerydemo.api.UnsplashApi
import com.example.brvahgallerydemo.model.ContentItem
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModel : ViewModel() {
    private val _contentItems = MutableLiveData<List<ContentItem>>()
    val contentItems: LiveData<List<ContentItem>> = _contentItems

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val unsplashApi = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UnsplashApi::class.java)

    init {
        loadData()
    }

    fun refreshData() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "开始加载数据")
                val photos = unsplashApi.getRandomPhotos(
                    count = 10,
                    orientation = "portrait",
                    query = "nature" // 获取自然风景图片
                )
                Log.d("HomeViewModel", "获取到 ${photos.size} 张图片")
                
                val items = photos.map { photo ->
                    ContentItem.ImageItem(
                        id = photo.id,
                        imageUrl = photo.urls.regular,
                        title = "Photo by ${photo.user.name}",
                        description = photo.description ?: "A beautiful photo",
                        likes = photo.likes,
                        isFavorite = photo.liked_by_user
                    )
                }

                Log.d("HomeViewModel", "准备更新数据，总数：${items.size}")
                _contentItems.value = items
                Log.d("HomeViewModel", "数据更新完成")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "加载数据失败", e)
                e.printStackTrace()
            }
        }
    }
} 