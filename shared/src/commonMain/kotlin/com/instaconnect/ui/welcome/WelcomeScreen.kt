package com.instaconnect.ui.welcome

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.theme.DarkColors
import com.instaconnect.ui.components.InstagramAvatar
import com.instaconnect.ui.components.RemoteImage
import com.instaconnect.ui.components.formatCount

/**
 * Pantalla de bienvenida estilo Glossy / Cristal Líquido Brillante.
 * Diseño ultra elegante con reflejos de luz especular y animaciones continuas.
 */
@Composable
fun WelcomeScreen(
    user: InstagramUser,
    isInstagramLogin: Boolean,
    onNavigateToProfile: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onRefresh: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    // Transición infinita para brillo reluciente continuo (Shimmer specular)
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Halo de respiración luminosa
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val specularBrush = remember(shimmerOffset) {
        Brush.linearGradient(
            0.0f to Color.White.copy(alpha = 0.12f),
            shimmerOffset to Color.White.copy(alpha = 0.75f),
            (shimmerOffset + 0.3f) to Color.White.copy(alpha = 0.12f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ==========================================
            // Tarjeta Hero Glossy Principal
            // ==========================================
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { -30 })
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(26.dp),
                            ambientColor = Color.White.copy(alpha = 0.15f),
                            spotColor = Color.White.copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E1E22),
                                    Color(0xFF121214)
                                )
                            )
                        )
                        .border(
                            width = 1.2.dp,
                            brush = specularBrush,
                            shape = RoundedCornerShape(26.dp)
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Badge Glossy de Estado Activo
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(
                                        0.5.dp,
                                        Color.White.copy(alpha = 0.3f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34C759))
                                            .shadow(4.dp, CircleShape, spotColor = Color(0xFF34C759))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isInstagramLogin) "Meta API En Vivo" else "Modo Demo",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Botón de sincronizar con reflejo
                            IconButton(
                                onClick = onRefresh,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                            ) {
                                Icon(
                                    Icons.Outlined.Refresh,
                                    contentDescription = "Sincronizar",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Avatar e información destacada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .shadow(12.dp, CircleShape, spotColor = Color.White)
                                    .border(1.5.dp, specularBrush, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                InstagramAvatar(
                                    name = user.fullName.ifEmpty { user.username },
                                    imageUrl = user.profilePictureUrl,
                                    size = 72.dp,
                                    fontSize = 26,
                                    hasActiveStory = true,
                                    onClick = onNavigateToProfile
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Bienvenido de vuelta!",
                                    fontSize = 13.sp,
                                    color = Color(0xFF8E8E93),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.fullName.ifEmpty { user.username },
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = (-0.4).sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (user.accountType.uppercase() == "BUSINESS" || user.accountType.uppercase() == "CREATOR") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.Filled.Verified,
                                            contentDescription = "Verificado",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "@${user.username}",
                                    fontSize = 13.sp,
                                    color = Color(0xFFA1A1A6),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Botón de cristal brillante para ir al perfil
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White,
                                            Color(0xFFE5E5EA)
                                        )
                                    )
                                )
                                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = Color.White)
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Explorar Mi Perfil Completo",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // Métricas Rápidas en 3 Tarjetas Glossy
            // ==========================================
            Text(
                text = "Resumen de Cuenta",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlossyStatCard(
                    title = "Seguidores",
                    value = formatCount(user.followersCount),
                    icon = Icons.Outlined.People,
                    modifier = Modifier.weight(1f),
                    specularBrush = specularBrush
                )
                GlossyStatCard(
                    title = "Siguiendo",
                    value = formatCount(user.followingCount),
                    icon = Icons.Outlined.PersonAdd,
                    modifier = Modifier.weight(1f),
                    specularBrush = specularBrush
                )
                GlossyStatCard(
                    title = "Posts",
                    value = formatCount(user.mediaCount),
                    icon = Icons.Outlined.PhotoCamera,
                    modifier = Modifier.weight(1f),
                    specularBrush = specularBrush
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // Publicación Destacada / Reciente Glossy
            // ==========================================
            val latestPost = user.recentMedia.firstOrNull()
            if (latestPost != null) {
                Text(
                    text = "Publicación Destacada",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.2).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = Color.White.copy(alpha = 0.1f),
                            spotColor = Color.White.copy(alpha = 0.18f)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1C1C1E), Color(0xFF141416))
                            )
                        )
                        .border(1.dp, specularBrush, RoundedCornerShape(22.dp))
                        .clickable { onNavigateToProfile() }
                ) {
                    Column {
                        val effectiveUrl = latestPost.mediaUrl.ifBlank { latestPost.thumbnailUrl }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .background(Color(0xFF0F0F10)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (effectiveUrl.isNotBlank()) {
                                RemoteImage(
                                    url = effectiveUrl,
                                    contentDescription = latestPost.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            // Reflejo satinado en la esquina
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.TopCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.15f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // Badge de tipo
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = latestPost.mediaType.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${latestPost.likesCount} me gusta",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = "Ver detalles →",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA1A1A6),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (latestPost.caption.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = latestPost.caption,
                                    fontSize = 12.sp,
                                    color = Color(0xFFD1D1D6),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ==========================================
            // Accesos Rápidos Glossy
            // ==========================================
            Text(
                text = "Acciones Rápidas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionGlossyButton(
                    title = "Mi Perfil",
                    subtitle = "Fotos y Posts",
                    icon = Icons.Outlined.Person,
                    onClick = onNavigateToProfile,
                    modifier = Modifier.weight(1f),
                    specularBrush = specularBrush
                )
                QuickActionGlossyButton(
                    title = "Equipo KMP",
                    subtitle = "10° Cuatri",
                    icon = Icons.Outlined.Group,
                    onClick = onNavigateToAbout,
                    modifier = Modifier.weight(1f),
                    specularBrush = specularBrush
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/**
 * Tarjeta de estadística individual con acabado Glossy / Espejo.
 */
@Composable
private fun GlossyStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    specularBrush: Brush
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color.White.copy(alpha = 0.08f),
                spotColor = Color.White.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E1E22),
                        Color(0xFF141416)
                    )
                )
            )
            .border(0.8.dp, specularBrush, RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Botón de acceso rápido tipo cápsula de cristal brillante.
 */
@Composable
private fun QuickActionGlossyButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    specularBrush: Brush
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.White.copy(alpha = 0.08f),
                spotColor = Color.White.copy(alpha = 0.16f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF222226),
                        Color(0xFF161618)
                    )
                )
            )
            .border(1.dp, specularBrush, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}
