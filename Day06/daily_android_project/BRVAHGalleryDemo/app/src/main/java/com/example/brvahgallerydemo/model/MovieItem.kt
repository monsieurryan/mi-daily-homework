package com.example.brvahgallerydemo.model

import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val voteAverage: Double,
    val releaseDate: String,
    val backdropPath: String?,
    val genreIds: List<Int>,
    val originalLanguage: String,
    val originalTitle: String,
    val popularity: Double,
    val video: Boolean,
    val voteCount: Int,
    var isFavorite: Boolean = false
) : Parcelable, MultiItemEntity {
    override val itemType: Int
        get() = this::class.java.hashCode()

    val fullPosterPath: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val fullBackdropPath: String?
        get() = backdropPath?.let { "https://image.tmdb.org/t/p/original$it" }
} 