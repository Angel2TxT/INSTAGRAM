package com.instaconnect.data

object InstagramConfig {

    var clientId: String = "1769369190781705"

    var clientSecret: String = "12ce14b376d6ecf24e9c6602287533b2"
 
    var redirectUri: String = "https://localhost/callback"

    var useMockData: Boolean = false

    var customAccessToken: String = ""

    const val AUTH_URL = "https://www.instagram.com/oauth/authorize"
  
    const val TOKEN_URL = "https://api.instagram.com/oauth/access_token"

    const val GRAPH_API_URL = "https://graph.instagram.com"
 
    const val USER_FIELDS = "id,username,name,biography,media_count,followers_count,follows_count,profile_picture_url,account_type,website"

    const val MEDIA_FIELDS = "id,caption,media_type,media_url,thumbnail_url,timestamp,permalink"

    fun buildAuthUrl(): String {
        return "$AUTH_URL?" +
                "force_reauth=true&" +
                "client_id=$clientId&" +
                "redirect_uri=$redirectUri&" +
                "response_type=code&" +
                "scope=instagram_business_basic%2Cinstagram_business_manage_messages%2Cinstagram_business_manage_comments%2Cinstagram_business_content_publish%2Cinstagram_business_manage_insights"
    }
}
