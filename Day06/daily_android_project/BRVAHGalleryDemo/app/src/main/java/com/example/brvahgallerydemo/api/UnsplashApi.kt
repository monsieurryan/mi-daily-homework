package com.example.brvahgallerydemo.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashApi {
    @Headers("Authorization: Client-ID 1mIaVDJiVagt649UjKtdV9h4CJeLV7Q7g16yDDprA3o")
    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("count") count: Int = 10,
        @Query("orientation") orientation: String = "portrait",
        @Query("query") query: String? = null
    ): List<UnsplashPhoto>
}

data class UnsplashPhoto(
    val id: String,
    val created_at: String,
    val width: Int,
    val height: Int,
    val color: String,
    val description: String?,
    val urls: UnsplashUrls,
    val user: UnsplashUser,
    val likes: Int,
    val liked_by_user: Boolean
)

data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String,
    val portfolio_url: String?,
    val bio: String?,
    val location: String?,
    val total_likes: Int,
    val total_photos: Int,
    val total_collections: Int,
    val instagram_username: String?,
    val twitter_username: String?
) 