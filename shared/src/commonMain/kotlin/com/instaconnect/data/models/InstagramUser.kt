package com.instaconnect.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa un usuario de Instagram con sus datos de perfil.
 */
@Serializable
data class InstagramUser(
    val id: String = "",
    val username: String = "",
    @SerialName("name")
    val fullName: String = "",
    val biography: String = "",
    @SerialName("media_count")
    val mediaCount: Int = 0,
    @SerialName("followers_count")
    val followersCount: Int = 0,
    @SerialName("follows_count")
    val followingCount: Int = 0,
    @SerialName("profile_picture_url")
    val profilePictureUrl: String = "",
    @SerialName("account_type")
    val accountType: String = "PERSONAL",
    val website: String = "",
    val recentMedia: List<InstagramMedia> = emptyList(),
    val stories: List<InstagramMedia> = emptyList()
)

/**
 * Representa un post/media de Instagram.
 */
@Serializable
data class InstagramMedia(
    val id: String = "",
    val caption: String = "",
    @SerialName("media_type")
    val mediaType: String = "IMAGE", // IMAGE, VIDEO, CAROUSEL_ALBUM
    @SerialName("media_url")
    val mediaUrl: String = "",
    @SerialName("thumbnail_url")
    val thumbnailUrl: String = "",
    val timestamp: String = "",
    val permalink: String = "",
    @SerialName("like_count")
    val likesCount: Int = 42,
    @SerialName("comments_count")
    val commentsCount: Int = 8
)

/**
 * Respuesta de la API de Instagram para la lista de media.
 */
@Serializable
data class InstagramMediaResponse(
    val data: List<InstagramMedia> = emptyList()
)

/**
 * Respuesta del token de Instagram.
 */
@Serializable
data class InstagramTokenResponse(
    @SerialName("access_token")
    val accessToken: String = "",
    @SerialName("user_id")
    val userId: Long = 0
)
