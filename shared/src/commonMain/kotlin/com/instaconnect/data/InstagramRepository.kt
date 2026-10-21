package com.instaconnect.data

import com.instaconnect.data.models.InstagramMedia
import com.instaconnect.data.models.InstagramMediaResponse
import com.instaconnect.data.models.InstagramTokenResponse
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.data.models.TeamAvatars
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Repositorio que maneja la comunicación con la API de Instagram.
 *
 * Soporta dos modos:
 * - Mock (useMockData = true): Devuelve datos de demostración interactivos
 * - Real (useMockData = false): Usa la API Graph de Instagram de Meta
 */
class InstagramRepository {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    /**
     * Intercambia el authorization code por un access token oficial.
     */
    suspend fun exchangeCodeForToken(code: String): Result<String> {
        val cleanCode = code.substringBefore('#').substringBefore("%23").removeSuffix("#_").trim()

        if (InstagramConfig.useMockData) {
            return Result.success("mock_access_token_12345")
        }

        return try {
            println(">>> [Meta OAuth] Solicitando token a ${InstagramConfig.TOKEN_URL}")
            println(">>> [Meta OAuth] client_id: ${InstagramConfig.clientId}")
            println(">>> [Meta OAuth] redirect_uri: ${InstagramConfig.redirectUri}")
            println(">>> [Meta OAuth] code (limpio): $cleanCode")

            val response = httpClient.submitForm(
                url = InstagramConfig.TOKEN_URL,
                formParameters = parameters {
                    append("client_id", InstagramConfig.clientId)
                    append("client_secret", InstagramConfig.clientSecret)
                    append("grant_type", "authorization_code")
                    append("redirect_uri", InstagramConfig.redirectUri)
                    append("code", cleanCode)
                }
            )

            val responseBody = response.bodyAsText()
            println(">>> [Meta OAuth] Respuesta HTTP ${response.status.value}: $responseBody")

            if (response.status.isSuccess()) {
                val tokenResponse: InstagramTokenResponse = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }.decodeFromString(responseBody)
                Result.success(tokenResponse.accessToken)
            } else {
                Result.failure(Exception("Error de Meta (${response.status.value}): $responseBody"))
            }
        } catch (e: Exception) {
            println(">>> [Meta OAuth] Excepción en exchangeCodeForToken: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos del perfil del usuario autenticado.
     */
    suspend fun getUserProfile(accessToken: String): Result<InstagramUser> {
        val effectiveToken = when {
            InstagramConfig.customAccessToken.isNotBlank() -> InstagramConfig.customAccessToken
            accessToken.isNotBlank() -> accessToken
            else -> "mock_token"
        }

        if (InstagramConfig.useMockData && effectiveToken == "mock_token") {
            return Result.success(getMockUser())
        }

        return try {
            val user: InstagramUser = try {
                httpClient.get(
                    "${InstagramConfig.GRAPH_API_URL}/me"
                ) {
                    parameter("fields", InstagramConfig.USER_FIELDS)
                    parameter("access_token", effectiveToken)
                }.body()
            } catch (_: Exception) {
                httpClient.get(
                    "${InstagramConfig.GRAPH_API_URL}/me"
                ) {
                    parameter("fields", "id,username,account_type,media_count")
                    parameter("access_token", effectiveToken)
                }.body()
            }

            // Obtener también los posts recientes y las historias reales de Meta API
            val mediaResult = getUserMedia(effectiveToken)
            val storiesResult = getUserStories(effectiveToken)

            val userWithMediaAndStories = user.copy(
                recentMedia = mediaResult.getOrDefault(emptyList()),
                stories = storiesResult.getOrDefault(emptyList())
            )

            Result.success(userWithMediaAndStories)
        } catch (e: Exception) {
            // Si la llamada real falla (por ejemplo, sin conexión o token inválido),
            // pero estamos en modo fallback, informamos con mock o devolvemos error
            if (InstagramConfig.useMockData) {
                Result.success(getMockUser())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene los posts recientes del usuario.
     */
    suspend fun getUserMedia(accessToken: String): Result<List<InstagramMedia>> {
        val effectiveToken = when {
            InstagramConfig.customAccessToken.isNotBlank() -> InstagramConfig.customAccessToken
            accessToken.isNotBlank() -> accessToken
            else -> "mock_token"
        }

        if (InstagramConfig.useMockData && effectiveToken == "mock_token") {
            return Result.success(getMockMedia())
        }

        return try {
            val response: InstagramMediaResponse = httpClient.get(
                "${InstagramConfig.GRAPH_API_URL}/me/media"
            ) {
                parameter("fields", InstagramConfig.MEDIA_FIELDS)
                parameter("access_token", effectiveToken)
                parameter("limit", 18)
            }.body()

            Result.success(response.data)
        } catch (e: Exception) {
            if (InstagramConfig.useMockData) {
                Result.success(getMockMedia())
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene las historias activas reales del usuario desde Meta Graph API.
     */
    suspend fun getUserStories(accessToken: String): Result<List<InstagramMedia>> {
        val effectiveToken = when {
            InstagramConfig.customAccessToken.isNotBlank() -> InstagramConfig.customAccessToken
            accessToken.isNotBlank() -> accessToken
            else -> "mock_token"
        }

        if (InstagramConfig.useMockData && effectiveToken == "mock_token") {
            return Result.success(getMockStories())
        }

        return try {
            val response: InstagramMediaResponse = httpClient.get(
                "${InstagramConfig.GRAPH_API_URL}/me/stories"
            ) {
                parameter("fields", "id,caption,media_type,media_url,thumbnail_url,timestamp,permalink")
                parameter("access_token", effectiveToken)
            }.body()

            Result.success(response.data)
        } catch (e: Exception) {
            // Si no tiene historias activas o da error, retornamos lista vacía sin fallar la carga completa
            Result.success(emptyList())
        }
    }

    fun getMockUser(): InstagramUser = InstagramUser(
        id = "17841405793187218",
        username = "angll_trujillo",
        fullName = "Angel Trujillo",
        biography = "\"Without a doubt, you’re everything I need.\"",
        mediaCount = 1,
        followersCount = 246,
        followingCount = 248,
        profilePictureUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
        accountType = "PERSONAL",
        website = "https://instagram.com/angll_trujillo",
        recentMedia = getMockMedia(),
        stories = getMockStories()
    )

    fun getMockStories(): List<InstagramMedia> = emptyList()

    fun getMockMedia(): List<InstagramMedia> = listOf(
        InstagramMedia(
            id = "angel_post_1",
            caption = "Me entiendes?",
            mediaType = "CAROUSEL_ALBUM",
            mediaUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
            thumbnailUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
            timestamp = "2026-10-06T18:30:00+0000",
            permalink = "https://instagram.com/angll_trujillo",
            likesCount = 184,
            commentsCount = 2
        )
    )

    /**
     * Limpia los recursos del cliente HTTP.
     */
    fun close() {
        try {
            httpClient.close()
        } catch (_: Exception) {}
    }
}
