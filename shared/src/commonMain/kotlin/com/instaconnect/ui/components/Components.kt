package com.instaconnect.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.models.InstagramMedia
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.theme.DarkColors
import com.instaconnect.theme.InstagramColors

/**
 * Avatar circular con iniciales del usuario y borde gradiente estilo Instagram.
 */
@Composable
fun InstagramAvatar(
    name: String,
    size: Dp = 80.dp,
    imageUrl: String = "",
    gradientColors: List<Color> = InstagramColors.GradientColors,
    borderWidth: Dp = 3.dp,
    fontSize: Int = 28,
    hasActiveStory: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() }
    } else Modifier

    Box(
        modifier = Modifier
            .size(size)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.sweepGradient(gradientColors))
                .padding(borderWidth)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isNotBlank()) {
                RemoteImage(
                    url = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    errorPlaceholder = {
                        val initials = name
                            .trim()
                            .split(" ")
                            .filter { it.isNotBlank() }
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.uppercase() }
                            .joinToString("")
                            .ifEmpty { name.take(1).uppercase().ifEmpty { "?" } }

                        Text(
                            text = initials,
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = InstagramColors.Pink
                        )
                    }
                )
            } else {
                val initials = name
                    .trim()
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .ifEmpty { name.take(1).uppercase().ifEmpty { "?" } }

                Text(
                    text = initials,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = InstagramColors.Pink
                )
            }
        }

        if (hasActiveStory) {
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF25D366))
                    .padding(2.dp)
            )
        }
    }
}

/**
 * Burbuja interactiva de Historia / Highlight estilo Apple y Meta.
 */
@Composable
fun InstagramStoryBubble(
    title: String,
    imageUrl: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    hasActiveStory: Boolean = false,
    gradientColors: List<Color> = listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)),
    size: Dp = 68.dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        val ringBrush = if (hasActiveStory) {
            Brush.sweepGradient(
                listOf(
                    Color.White,
                    Color(0xFFE5E5EA),
                    Color(0xFF8E8E93),
                    Color(0xFFD1D1D6),
                    Color.White
                )
            )
        } else {
            Brush.linearGradient(listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)))
        }

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(ringBrush)
                .padding(if (hasActiveStory) 2.5.dp else 1.dp)
                .clip(CircleShape)
                .background(DarkColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                RemoteImage(
                    url = imageUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (icon != null) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.42f)
                )
            } else {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.42f)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (hasActiveStory) FontWeight.SemiBold else FontWeight.Medium,
            color = if (hasActiveStory) Color.White else Color(0xFFA1A1A6),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Campo de búsqueda estilizado tipo Apple Spotlight.
 */
@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF8E8E93),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Buscar",
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(19.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Limpiar",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DarkColors.SurfaceVariant,
            unfocusedContainerColor = DarkColors.SurfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(12.dp))
    )
}

/**
 * Chip para filtrar categorías o tipos de publicación estilo Apple.
 */
@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Color.White else DarkColors.SurfaceVariant
            )
            .border(
                width = 0.5.dp,
                color = if (isSelected) Color.White else DarkColors.Outline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.Black else Color(0xFF8E8E93),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.Black else Color(0xFF8E8E93)
            )
        }
    }
}

/**
 * Diálogo modal detallado al abrir un post de Instagram.
 */
@Composable
fun PostDetailDialog(
    media: InstagramMedia,
    onDismiss: () -> Unit,
    onLikeToggle: ((isLiked: Boolean) -> Unit)? = null
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(media.likesCount) }
    var isSaved by remember { mutableStateOf(false) }

    val heartScale = remember { Animatable(1f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text("Cerrar", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = DarkColors.Surface,
        shape = RoundedCornerShape(22.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkColors.SurfaceVariant)
                            .border(0.5.dp, DarkColors.Outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (media.mediaType) {
                                "VIDEO" -> Icons.Filled.PlayCircle
                                "CAROUSEL_ALBUM" -> Icons.Filled.Collections
                                else -> Icons.Filled.PhotoCamera
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = when (media.mediaType) {
                            "VIDEO" -> "Video / Reel"
                            "CAROUSEL_ALBUM" -> "Álbum Carrusel"
                            else -> "Publicación"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // Badge de tipo estilo iOS
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkColors.SurfaceVariant)
                        .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = media.mediaType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA1A1A6)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Vista ampliada con la foto real o gradiente dinámico
                val effectiveUrl = media.mediaUrl.ifBlank { media.thumbnailUrl }
                if (effectiveUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkColors.SurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        RemoteImage(
                            url = effectiveUrl,
                            contentDescription = media.caption,
                            modifier = Modifier.fillMaxSize(),
                            errorPlaceholder = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (media.mediaType) {
                                            "VIDEO" -> Icons.Filled.PlayArrow
                                            "CAROUSEL_ALBUM" -> Icons.Filled.CollectionsBookmark
                                            else -> Icons.Outlined.Image
                                        },
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                }
                            }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        InstagramColors.Purple.copy(alpha = 0.4f),
                                        InstagramColors.DeepPurple.copy(alpha = 0.6f),
                                        InstagramColors.Pink.copy(alpha = 0.4f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                when (media.mediaType) {
                                    "VIDEO" -> Icons.Filled.PlayArrow
                                    "CAROUSEL_ALBUM" -> Icons.Filled.CollectionsBookmark
                                    else -> Icons.Outlined.Image
                                },
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Contenido en alta resolución",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fila de acciones interactivas (Like, Comment, Share, Save)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Like interactivo
                        IconButton(
                            onClick = {
                                isLiked = !isLiked
                                if (isLiked) likesCount++ else likesCount--
                                onLikeToggle?.invoke(isLiked)
                            },
                            modifier = Modifier.scale(heartScale.value)
                        ) {
                            Icon(
                                if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Me gusta",
                                tint = if (isLiked) InstagramColors.Red else Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Comentario
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Outlined.ModeComment,
                                contentDescription = "Comentar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Compartir
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Send,
                                contentDescription = "Enviar",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Guardar
                    IconButton(onClick = { isSaved = !isSaved }) {
                        Icon(
                            if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (isSaved) InstagramColors.Yellow else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Likes count
                Text(
                    text = "${formatCount(likesCount)} Me gusta",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Caption
                Text(
                    text = media.caption,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Comentarios
                Text(
                    text = "Ver los ${media.commentsCount} comentarios",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

/**
 * Botón de acción destacado estilo Apple (Blanco con texto negro).
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    gradientColors: List<Color> = listOf(Color.White, Color.White)
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) Color.White else Color(0xFF2C2C2E))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = if (enabled) Color.Black else Color(0xFF8E8E93),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color.Black else Color(0xFF8E8E93),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Tarjeta limpia con borde fino estilo iOS.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkColors.Surface)
            .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

/**
 * Widget de estadística (posts, seguidores, siguiendo).
 */
@Composable
fun StatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Column(
        modifier = modifier.then(clickableModifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Divisor vertical con gradiente.
 */
@Composable
fun GradientDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(1.dp)
            .height(36.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        InstagramColors.Pink.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Formatea cantidades numéricas con abreviaturas (K, M).
 */
fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> {
            val m = count / 1_000_000
            val rem = (count % 1_000_000) / 100_000
            if (rem > 0) "$m.${rem}M" else "${m}M"
        }
        count >= 10_000 -> {
            val k = count / 1_000
            val rem = (count % 1_000) / 100
            if (rem > 0) "$k.${rem}K" else "${k}K"
        }
        count >= 1_000 -> {
            val k = count / 1_000
            val rem = (count % 1_000) / 100
            if (rem > 0) "$k.${rem}K" else "${k}K"
        }
        else -> count.toString()
    }
}

/**
 * Diálogo modal para ver una Historia o Historia Destacada estilo Instagram Stories.
 */
@Composable
fun StoryDetailDialog(
    user: InstagramUser,
    story: InstagramMedia,
    isLiveStory: Boolean = false,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (story.permalink.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            try {
                                uriHandler.openUri(story.permalink)
                            } catch (e: Exception) {
                                println("Error al abrir enlace: ${e.message}")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Outlined.OpenInBrowser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver en Instagram", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        containerColor = DarkColors.Surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InstagramAvatar(
                        imageUrl = user.profilePictureUrl,
                        name = user.fullName.ifBlank { user.username },
                        size = 38.dp,
                        hasActiveStory = isLiveStory
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "@${user.username}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isLiveStory) "Historia en Vivo (Meta API)" else "Historia Destacada",
                            fontSize = 11.sp,
                            color = Color(0xFFA1A1A6)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLiveStory) Color(0xFF34C759).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isLiveStory) "EN VIVO" else "DESTACADA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLiveStory) Color(0xFF34C759) else Color.White
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val mediaUrl = story.thumbnailUrl.ifBlank { story.mediaUrl }
                if (mediaUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        RemoteImage(
                            url = mediaUrl,
                            contentDescription = "Historia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (story.mediaType == "VIDEO") {
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("VIDEO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (story.caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = story.caption,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (story.timestamp.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Publicado: ${story.timestamp.take(10)}",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8E93),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
