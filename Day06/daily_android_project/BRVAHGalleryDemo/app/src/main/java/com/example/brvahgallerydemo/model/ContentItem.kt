package com.example.brvahgallerydemo.model

import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.parcelize.Parcelize

sealed class ContentItem : MultiItemEntity, Parcelable {
    @Parcelize
    data class ImageItem(
        val id: String,
        val imageUrl: String,
        val title: String,
        val description: String,
        val likes: Int,
        var isFavorite: Boolean = false
    ) : ContentItem() {
        override val itemType: Int = ImageItem::class.java.hashCode()
    }
} 