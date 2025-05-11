package com.example.brvahgallerydemo.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.brvahgallerydemo.model.ContentItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _favoriteItems = MutableStateFlow<Set<String>>(emptySet())
    val favoriteItems: StateFlow<Set<String>> = _favoriteItems

    private val listeners = mutableListOf<FavoriteChangeListener>()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val favorites = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        _favoriteItems.value = favorites
    }

    private fun saveFavorites() {
        prefs.edit().putStringSet(KEY_FAVORITES, _favoriteItems.value).apply()
    }

    fun toggleFavorite(item: ContentItem) {
        val itemId = when (item) {
            is ContentItem.ImageItem -> item.id
            is ContentItem.TextItem -> item.id
        }
        
        Log.d("FavoriteManager", "切换点赞状态：itemId=$itemId, 当前状态=${_favoriteItems.value.contains(itemId)}")
        
        if (_favoriteItems.value.contains(itemId)) {
            val currentFavorites = _favoriteItems.value.toMutableSet()
            currentFavorites.remove(itemId)
            _favoriteItems.value = currentFavorites
            Log.d("FavoriteManager", "取消点赞：itemId=$itemId")
        } else {
            val currentFavorites = _favoriteItems.value.toMutableSet()
            currentFavorites.add(itemId)
            _favoriteItems.value = currentFavorites
            Log.d("FavoriteManager", "添加点赞：itemId=$itemId")
        }
        
        // 保存到 SharedPreferences
        saveFavorites()
        Log.d("FavoriteManager", "保存点赞状态到SharedPreferences，当前点赞总数：${_favoriteItems.value.size}")
        
        // 通知监听器
        notifyListeners(itemId, _favoriteItems.value.contains(itemId))
    }

    fun isFavorite(itemId: String): Boolean {
        val result = _favoriteItems.value.contains(itemId)
        Log.d("FavoriteManager", "检查点赞状态：itemId=$itemId, isFavorite=$result")
        return result
    }

    fun addListener(listener: FavoriteChangeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: FavoriteChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(itemId: String, isFavorite: Boolean) {
        // 使用toList()创建副本以避免并发修改异常
        listeners.toList().forEach { it.onFavoriteChanged(itemId, isFavorite) }
    }

    interface FavoriteChangeListener {
        fun onFavoriteChanged(itemId: String, isFavorite: Boolean)
    }

    companion object {
        private const val PREFS_NAME = "favorite_prefs"
        private const val KEY_FAVORITES = "favorites"

        @Volatile
        private var instance: FavoriteManager? = null

        fun getInstance(context: Context): FavoriteManager {
            return instance ?: synchronized(this) {
                instance ?: FavoriteManager(context.applicationContext).also { instance = it }
            }
        }
    }
} 