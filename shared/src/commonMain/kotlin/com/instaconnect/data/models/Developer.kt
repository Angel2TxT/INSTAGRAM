package com.instaconnect.data.models

/**
 * Representa a un desarrollador del equipo para la pantalla "Acerca de".
 *
 * @param name Nombre completo del desarrollador
 * @param role Rol en el proyecto (ej: "Frontend Developer")
 * @param instagramUsername Username de Instagram (sin @)
 * @param subject Materia o clase relacionada
 * @param semester Cuatrimestre actual
 * @param description Descripción breve del desarrollador
 * @param avatarColor Color de fondo para el avatar (índice 0-3)
 * @param instagramBio Bio de Instagram del desarrollador
 * @param instagramFollowers Número de seguidores
 * @param instagramFollowing Número de seguidos
 * @param instagramPosts Número de publicaciones
 */
data class Developer(
    val name: String,
    val role: String,
    val instagramUsername: String,
    val subject: String,
    val semester: String,
    val description: String,
    val avatarColor: Int = 0,
    val instagramBio: String = "",
    val instagramFollowers: Int = 0,
    val instagramFollowing: Int = 0,
    val instagramPosts: Int = 0,
    val instagramUrl: String = "",
    val avatarUrl: String = ""
)
