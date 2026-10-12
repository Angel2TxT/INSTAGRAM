package com.instaconnect.data

/**
 * Configuración dinámica de la API de Instagram.
 *
 * Permite alternar en tiempo de ejecución entre:
 * 1. Modo demostración (Mock Data completo)
 * 2. Token de acceso directo de Meta Graph API
 * 3. Flujo OAuth completo con App ID y App Secret
 */
object InstagramConfig {

    // ============================================================
    // Configuración de Meta / Instagram API
    // ============================================================

    /** ID de la aplicación en Meta Developers (Instagram App ID) */
    var clientId: String = "1769369190781705"

    /** Secret de la aplicación en Meta Developers */
    var clientSecret: String = "TU_APP_SECRET_AQUI"

    /** URI de redirección configurada en Meta Developers */
    var redirectUri: String = "https://localhost/callback"

    /**
     * Si es true, usa datos de demostración en lugar de la API real.
     * Puede alternarse dinámicamente desde la interfaz de usuario.
     */
    var useMockData: Boolean = false

    /**
     * Token de acceso personalizado (por ejemplo, generado desde Graph API Explorer).
     */
    var customAccessToken: String = ""

    // ============================================================
    // Endpoints de la API de Instagram
    // ============================================================

    /** URL de autorización OAuth de Instagram para apps de Negocios */
    const val AUTH_URL = "https://www.instagram.com/oauth/authorize"

    /** URL para intercambiar el code por access token */
    const val TOKEN_URL = "https://api.instagram.com/oauth/access_token"

    /** URL base de la Graph API de Instagram */
    const val GRAPH_API_URL = "https://graph.instagram.com"

    /** Scopes de permisos requeridos para app de tipo Negocios */
    const val SCOPES = "instagram_business_basic,instagram_business_manage_messages,instagram_business_manage_comments,instagram_business_content_publish,instagram_business_manage_insights"

    /**
     * Campos a solicitar del perfil del usuario.
     */
    const val USER_FIELDS = "id,username,name,biography,media_count,followers_count,follows_count,profile_picture_url,account_type,website"

    /**
     * Campos a solicitar de los posts del usuario.
     */
    const val MEDIA_FIELDS = "id,caption,media_type,media_url,thumbnail_url,timestamp,permalink"

    /**
     * Retorna si las credenciales de Meta están configuradas adecuadamente.
     */
    fun isRealApiConfigured(): Boolean {
        return clientId.isNotBlank() &&
                clientId != "TU_APP_ID_AQUI" &&
                clientSecret.isNotBlank() &&
                clientSecret != "TU_APP_SECRET_AQUI"
    }

    /**
     * Construye la URL de autorización completa para iniciar el flujo OAuth oficial generado por Meta.
     */
    fun buildAuthUrl(): String {
        return "$AUTH_URL?" +
                "force_reauth=true&" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "response_type=code&" +
                "scope=instagram_business_basic%2Cinstagram_business_manage_messages%2Cinstagram_business_manage_comments%2Cinstagram_business_content_publish%2Cinstagram_business_manage_insights"
    }

    /**
     * Restablece la configuración al modo demostración predeterminado.
     */
    fun resetToDemoMode() {
        useMockData = true
        customAccessToken = ""
    }
}
