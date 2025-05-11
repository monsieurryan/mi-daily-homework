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
                
                // 创建图片项
                val imageItems = photos.map { photo ->
                    ContentItem.ImageItem(
                        id = photo.id,
                        imageUrl = photo.urls.regular,
                        title = "Photo by ${photo.user.name}",
                        description = photo.description ?: "A beautiful photo",
                        likes = photo.likes,
                        isFavorite = photo.liked_by_user
                    )
                }

                // 创建文字项
                val textItems = listOf(
                    ContentItem.TextItem(
                        id = "text_1",
                        title = "大自然的奥秘",
                        preview = "在这个快节奏的世界里，我们常常忘记了停下来欣赏大自然的美丽。每一片叶子，每一朵花，都在诉说着生命的奇迹...",
                        content = "在这个快节奏的世界里，我们常常忘记了停下来欣赏大自然的美丽。每一片叶子，每一朵花，都在诉说着生命的奇迹。\n\n" +
                                "春天，万物复苏，新生的嫩芽破土而出；夏天，繁花似锦，绿树成荫；秋天，层林尽染，硕果累累；冬天，银装素裹，静谧安详。\n\n" +
                                "大自然教会我们耐心，教会我们敬畏，教会我们感恩。让我们放慢脚步，用心感受这美好的世界。",
                        likes = 128,
                        isFavorite = false
                    ),
                    ContentItem.TextItem(
                        id = "text_2",
                        title = "摄影的艺术",
                        preview = "摄影不仅仅是记录，更是一种表达。通过镜头，我们可以捕捉瞬间的永恒，让时间在那一刻静止...",
                        content = "摄影不仅仅是记录，更是一种表达。通过镜头，我们可以捕捉瞬间的永恒，让时间在那一刻静止。\n\n" +
                                "好的摄影作品能够讲述故事，传递情感，引发共鸣。它让我们看到世界的不同角度，发现生活中被忽视的细节。\n\n" +
                                "无论是自然风光，还是人文纪实，每一张照片都承载着摄影师独特的视角和思考。",
                        likes = 256,
                        isFavorite = false
                    ),
                    ContentItem.TextItem(
                        id = "text_3",
                        title = "旅行的意义",
                        preview = "旅行，是探索未知的勇气，是拥抱变化的决心。每一次出发，都是一次自我发现的过程...",
                        content = "旅行，是探索未知的勇气，是拥抱变化的决心。每一次出发，都是一次自我发现的过程。\n\n" +
                                "在陌生的土地上，我们学会适应，学会包容，学会欣赏不同的文化。旅行让我们变得更加开放，更加包容，更加理解这个世界。\n\n" +
                                "无论是远方的山川，还是近处的风景，每一次旅行都是一次心灵的洗礼。",
                        likes = 192,
                        isFavorite = false
                    )
                )

                // 混合图片和文字项
                val allItems = (imageItems + textItems).shuffled()

                Log.d("HomeViewModel", "准备更新数据，总数：${allItems.size}")
                _contentItems.value = allItems
                Log.d("HomeViewModel", "数据更新完成")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "加载数据失败", e)
                e.printStackTrace()
            }
        }
    }
} 