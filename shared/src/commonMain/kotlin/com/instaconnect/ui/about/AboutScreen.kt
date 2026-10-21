package com.instaconnect.ui.about

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.models.Developer
import com.instaconnect.data.models.TeamAvatars
import com.instaconnect.theme.DarkColors
import com.instaconnect.theme.InstagramColors
import com.instaconnect.ui.components.InstagramAvatar
import com.instaconnect.ui.components.SearchInputField
import com.instaconnect.ui.components.formatCount

/**
 * Pantalla "Acerca de" que presenta a los 4 desarrolladores del proyecto.
 * Incluye buscador, datos académicos, habilidades técnicas y perfil
 * interactivo de Instagram con botón de seguir y proyectos destacados.
 */
@Composable
fun AboutScreen() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDeveloper by remember { mutableStateOf<Developer?>(null) }

    // Estado reactivo de "Seguidos" para cada desarrollador
    val followingState = remember { mutableStateMapOf<String, Boolean>() }
    val followersOffset = remember { mutableStateMapOf<String, Int>() }

    val developers = remember {
        listOf(
            Developer(
                name = "Arbey Esteban Torres Hernández",
                role = "Arquitectura KMP & Estructura",
                instagramUsername = "arbe__yy",
                instagramUrl = "https://www.instagram.com/arbe__yy/",
                avatarUrl = TeamAvatars.ARBE_YY_AVATAR_BASE64,
                subject = "Desarrollo Móvil Multiplataforma",
                semester = "10° Cuatrimestre",
                description = "Arquitectura limpia y modularización del código compartido en shared.",
                avatarColor = 0,
                instagramBio = "Software Engineering • KMP & Clean Architecture",
                instagramFollowers = 67,
                instagramFollowing = 52,
                instagramPosts = 5
            ),
            Developer(
                name = "Angel Emmanuel Trujillo López",
                role = "Lead Developer & Meta Graph API",
                instagramUsername = "angll_trujillo",
                instagramUrl = "https://www.instagram.com/angll_trujillo/",
                avatarUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
                subject = "Desarrollo Móvil Multiplataforma",
                semester = "10° Cuatrimestre",
                description = "Autenticación OAuth 2.0 con Instagram/Meta y networking con Ktor 3.",
                avatarColor = 1,
                instagramBio = "\"Without a doubt, you’re everything I need.\"",
                instagramFollowers = 246,
                instagramFollowing = 248,
                instagramPosts = 1
            ),
            Developer(
                name = "Carlos Jafet Domínguez López",
                role = "UI/UX & Frontend Compose",
                instagramUsername = "jzzstyl",
                instagramUrl = "https://www.instagram.com/jzzstyl/",
                avatarUrl = TeamAvatars.JZZSTYL_AVATAR_BASE64,
                subject = "Desarrollo Móvil Multiplataforma",
                semester = "10° Cuatrimestre",
                description = "Diseño de interfaz de usuario, animaciones y componentes en Compose.",
                avatarColor = 2,
                instagramBio = "UI/UX Designer • Compose Multiplatform • Mobile Engineering",
                instagramFollowers = 219,
                instagramFollowing = 86,
                instagramPosts = 0
            ),
            Developer(
                name = "Roldan Aldemar Arcos Gómez",
                role = "QA & Multiplatform Testing",
                instagramUsername = "rdy__a",
                instagramUrl = "https://www.instagram.com/rdy__a/",
                avatarUrl = TeamAvatars.RDY_A_AVATAR_BASE64,
                subject = "Desarrollo Móvil Multiplataforma",
                semester = "10° Cuatrimestre",
                description = "Control de calidad, pruebas de integración y despliegue del proyecto.",
                avatarColor = 3,
                instagramBio = "QA Engineer • Multiplatform Testing • Code Quality",
                instagramFollowers = 3,
                instagramFollowing = 2,
                instagramPosts = 0
            )
        )
    }

    val filteredDevelopers = remember(developers, searchQuery) {
        if (searchQuery.isBlank()) developers else {
            developers.filter { dev ->
                dev.name.contains(searchQuery, ignoreCase = true) ||
                        dev.role.contains(searchQuery, ignoreCase = true) ||
                        dev.instagramUsername.contains(searchQuery, ignoreCase = true) ||
                        dev.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Header con degradado
            AboutHeader(isVisible)

            Spacer(modifier = Modifier.height(16.dp))

            // Card informativa de la materia
            SubjectInfoCard(isVisible)

            Spacer(modifier = Modifier.height(20.dp))

            // Título de la sección y buscador
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Group,
                        contentDescription = "Equipo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Equipo de Desarrollo (4 Desarrolladores)",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                SearchInputField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar desarrollador por nombre o rol..."
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lista de desarrolladores filtrada
            if (filteredDevelopers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron integrantes que coincidan con la búsqueda.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                filteredDevelopers.forEachIndexed { index, dev ->
                    val isFollowing = followingState[dev.instagramUsername] ?: false
                    val extraFollowers = followersOffset[dev.instagramUsername] ?: 0

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 150 + index * 80)) +
                                slideInVertically(
                                    initialOffsetY = { 30 },
                                    animationSpec = tween(400, delayMillis = 150 + index * 80)
                                )
                    ) {
                        DeveloperCard(
                            developer = dev,
                            isFollowing = isFollowing,
                            extraFollowers = extraFollowers,
                            onClick = { selectedDeveloper = dev },
                            onToggleFollow = {
                                val current = followingState[dev.instagramUsername] ?: false
                                followingState[dev.instagramUsername] = !current
                                followersOffset[dev.instagramUsername] = if (!current) 1 else 0
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }

    // Modal de perfil de Instagram detallado
    selectedDeveloper?.let { dev ->
        val isFollowing = followingState[dev.instagramUsername] ?: false
        val extraFollowers = followersOffset[dev.instagramUsername] ?: 0

        DeveloperInstagramModal(
            developer = dev,
            isFollowing = isFollowing,
            extraFollowers = extraFollowers,
            onToggleFollow = {
                val current = followingState[dev.instagramUsername] ?: false
                followingState[dev.instagramUsername] = !current
                followersOffset[dev.instagramUsername] = if (!current) 1 else 0
            },
            onDismiss = { selectedDeveloper = null }
        )
    }
}

@Composable
private fun AboutHeader(isVisible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkColors.Background)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DarkColors.SurfaceVariant)
                    .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Code,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Acerca de GramPulse",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Kotlin Multiplatform (KMP) • Meta Graph API",
                fontSize = 13.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun SubjectInfoCard(isVisible: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkColors.Surface),
        border = BorderStroke(0.5.dp, DarkColors.Outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C2E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.School,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Desarrollo Móvil Multiplataforma",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "10° Cuatrimestre • Kotlin Multiplatform & Meta API",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}

@Composable
private fun DeveloperCard(
    developer: Developer,
    isFollowing: Boolean,
    extraFollowers: Int,
    onClick: () -> Unit,
    onToggleFollow: () -> Unit
) {
    val gradientList = InstagramColors.AvatarColors.getOrElse(developer.avatarColor) {
        InstagramColors.AvatarColors[0]
    }
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkColors.Surface),
        border = BorderStroke(0.5.dp, DarkColors.Outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InstagramAvatar(
                    name = developer.name,
                    size = 56.dp,
                    imageUrl = developer.avatarUrl,
                    gradientColors = listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)),
                    fontSize = 20,
                    borderWidth = 1.5.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = developer.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = "Verificado",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = developer.role,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF8E8E93)
                    )

                    Text(
                        text = "@${developer.instagramUsername} • ${developer.semester}",
                        fontSize = 11.sp,
                        color = Color(0xFFA1A1A6)
                    )
                }

                // Botones rápidos estilo Apple
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (developer.instagramUrl.isNotBlank()) {
                                uriHandler.openUri(developer.instagramUrl)
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.OpenInBrowser,
                            contentDescription = "Abrir perfil de Instagram",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = onToggleFollow,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) DarkColors.SurfaceVariant else Color.White,
                            contentColor = if (isFollowing) Color(0xFF8E8E93) else Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isFollowing) "Siguiendo" else "Seguir",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isFollowing) Color(0xFF8E8E93) else Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = developer.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mini barra con métricas de Instagram del dev y link interactivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkColors.SurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${formatCount(developer.instagramFollowers + extraFollowers)} seguidores",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (developer.instagramUrl.isNotBlank()) {
                            uriHandler.openUri(developer.instagramUrl)
                        }
                    }
                ) {
                    Text(
                        text = "@${developer.instagramUsername} ›",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8E93),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Modal detallado y limpio del perfil de Instagram del desarrollador.
 */
@Composable
private fun DeveloperInstagramModal(
    developer: Developer,
    isFollowing: Boolean,
    extraFollowers: Int,
    onToggleFollow: () -> Unit,
    onDismiss: () -> Unit
) {
    val gradientList = InstagramColors.AvatarColors.getOrElse(developer.avatarColor) {
        InstagramColors.AvatarColors[0]
    }
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        containerColor = DarkColors.Surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (developer.instagramUrl.isNotBlank()) {
                            uriHandler.openUri(developer.instagramUrl)
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = null,
                        tint = InstagramColors.Pink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "@${developer.instagramUsername}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = null,
                        tint = Color(0xFF0095F6),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                // Header: Avatar grande + Métricas oficiales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InstagramAvatar(
                        name = developer.name,
                        size = 72.dp,
                        imageUrl = developer.avatarUrl,
                        gradientColors = gradientList,
                        fontSize = 24,
                        borderWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricColumn(developer.instagramPosts.toString(), "Posts")
                        MetricColumn(formatCount(developer.instagramFollowers + extraFollowers), "Seguidores")
                        MetricColumn(formatCount(developer.instagramFollowing), "Siguiendo")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nombre completo
                Text(
                    text = developer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )

                // Rol
                Text(
                    text = developer.role,
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bio limpia auténtica
                if (developer.instagramBio.isNotBlank()) {
                    Text(
                        text = developer.instagramBio,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Botones principales estilo Instagram: "Seguir" y "Abrir en Instagram"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleFollow,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing) DarkColors.SurfaceVariant else Color.White,
                            contentColor = if (isFollowing) Color(0xFF8E8E93) else Color.Black
                        )
                    ) {
                        Text(
                            text = if (isFollowing) "Siguiendo" else "Seguir",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFollowing) Color(0xFF8E8E93) else Color.Black
                        )
                    }

                    Button(
                        onClick = {
                            if (developer.instagramUrl.isNotBlank()) {
                                uriHandler.openUri(developer.instagramUrl)
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkColors.SurfaceVariant
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.OpenInBrowser,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Instagram",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tarjeta minimalista de contribución directa al proyecto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C1C1E))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Code,
                                contentDescription = null,
                                tint = InstagramColors.Pink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Contribución en GramPulse",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = developer.description,
                            fontSize = 12.sp,
                            color = Color(0xFFA1A1A6),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun MetricColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
