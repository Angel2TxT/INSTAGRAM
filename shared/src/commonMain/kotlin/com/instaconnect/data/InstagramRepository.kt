package com.instaconnect.data

import com.instaconnect.data.models.InstagramMedia
import com.instaconnect.data.models.InstagramMediaResponse
import com.instaconnect.data.models.InstagramTokenResponse
import com.instaconnect.data.models.InstagramUser
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
        val cleanCode = code.removeSuffix("#_").removeSuffix("%23_").trim()

        if (InstagramConfig.useMockData) {
            return Result.success("mock_access_token_12345")
        }

        return try {
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

            if (response.status.isSuccess()) {
                val tokenResponse: InstagramTokenResponse = response.body()
                Result.success(tokenResponse.accessToken)
            } else {
                val errorMsg = response.bodyAsText()
                Result.failure(Exception("Error de Meta (${response.status.value}): $errorMsg"))
            }
        } catch (e: Exception) {
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
            val user: InstagramUser = httpClient.get(
                "${InstagramConfig.GRAPH_API_URL}/me"
            ) {
                parameter("fields", InstagramConfig.USER_FIELDS)
                parameter("access_token", effectiveToken)
            }.body()

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

    // ================================================================
    // Datos de demostración (Mock Data)
    // ================================================================

    fun getMockUser(): InstagramUser = InstagramUser(
        id = "17841405793187218",
        username = "grampulse_dev",
        fullName = "GramPulse Developer",
        biography = "🎓 Estudiantes de Ingeniería en Software\n📱 Desarrollando en Kotlin Multiplatform (KMP)\n✨ 10° Cuatrimestre • Desarrollo Móvil\n📍 México • Meta Graph API Ready",
        mediaCount = 47,
        followersCount = 1_542,
        followingCount = 623,
        profilePictureUrl = "",
        accountType = "BUSINESS",
        website = "https://github.com/grampulse",
        recentMedia = getMockMedia(),
        stories = getMockStories()
    )

    fun getMockStories(): List<InstagramMedia> = listOf(
        InstagramMedia(
            id = "mock_story_1",
            caption = "🚀 Nueva versión de GramPulse con soporte completo de Stories",
            mediaType = "IMAGE",
            mediaUrl = "https://picsum.photos/seed/grampulse_story1/600/1000",
            thumbnailUrl = "https://picsum.photos/seed/grampulse_story1/300/300",
            timestamp = "2026-10-09T03:00:00+0000",
            permalink = "https://instagram.com"
        ),
        InstagramMedia(
            id = "mock_story_2",
            caption = "✨ Probando la animación de pulso continua en Compose",
            mediaType = "IMAGE",
            mediaUrl = "https://picsum.photos/seed/grampulse_story2/600/1000",
            thumbnailUrl = "https://picsum.photos/seed/grampulse_story2/300/300",
            timestamp = "2026-10-08T22:30:00+0000",
            permalink = "https://instagram.com"
        )
    )

    fun getMockMedia(): List<InstagramMedia> = listOf(
        InstagramMedia(
            id = "post_1",
            caption = "🚀 Lanzamiento oficial de GramPulse! Una sola base de código en Kotlin para Android y Desktop. #KMP #Kotlin #MobileDev #ComposeMultiplatform",
            mediaType = "IMAGE",
            mediaUrl = "",
            timestamp = "2026-10-06T18:30:00+0000",
            permalink = "https://instagram.com/p/demo1",
            likesCount = 184,
            commentsCount = 24
        ),
        InstagramMedia(
            id = "post_2",
            caption = "💻 Sesión intensiva de código con el equipo universitario. Pulimos arquitectura y consumo de endpoints OAuth. #DevLife #CodingSession #10moCuatrimestre",
            mediaType = "CAROUSEL_ALBUM",
            mediaUrl = "",
            timestamp = "2026-10-03T14:15:00+0000",
            permalink = "https://instagram.com/p/demo2",
            likesCount = 210,
            commentsCount = 31
        ),
        InstagramMedia(
            id = "post_3",
            caption = "🌟 Jetpack Compose & Compose Multiplatform hacen que el diseño declarativo sea una maravilla visual. #Compose #UIUX #ModernAndroid",
            mediaType = "IMAGE",
            mediaUrl = "",
            timestamp = "2026-09-29T11:00:00+0000",
            permalink = "https://instagram.com/p/demo3",
            likesCount = 95,
            commentsCount = 12
        ),
        InstagramMedia(
            id = "post_4",
            caption = "🎬 Demo en video: Flujo completo de autenticación y sincronización de datos con Meta Graph API. #InstagramAPI #OAuth #Ktor",
            mediaType = "VIDEO",
            mediaUrl = "",
            timestamp = "2026-09-25T16:45:00+0000",
            permalink = "https://instagram.com/p/demo4",
            likesCount = 342,
            commentsCount = 58
        ),
        InstagramMedia(
            id = "post_5",
            caption = "🎓 Proyecto integrador para la materia Desarrollo Móvil Multiplataforma. El futuro es multiplataforma! #Universidad #SoftwareEngineering",
            mediaType = "IMAGE",
            mediaUrl = "",
            timestamp = "2026-09-20T10:20:00+0000",
            permalink = "https://instagram.com/p/demo5",
            likesCount = 156,
            commentsCount = 19
        ),
        InstagramMedia(
            id = "post_6",
            caption = "☕ Café y Kotlin = la combinación perfecta para compilar sin errores en viernes. #CodingLife #DeveloperHumor",
            mediaType = "IMAGE",
            mediaUrl = "",
            timestamp = "2026-09-15T08:30:00+0000",
            permalink = "https://instagram.com/p/demo6",
            likesCount = 128,
            commentsCount = 15
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
