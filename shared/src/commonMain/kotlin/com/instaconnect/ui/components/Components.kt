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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.instaconnect.data.models.InstagramMedia
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.data.models.TeamAvatars
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
 * Vista modal de pantalla completa para una publicación de Instagram (Feed Post Detail).
 * Idéntica a la vista oficial de Instagram cuando el creador visualiza su publicación.
 */
@Composable
fun PostDetailDialog(
    media: InstagramMedia,
    user: InstagramUser? = null,
    onDismiss: () -> Unit,
    onLikeToggle: ((isLiked: Boolean) -> Unit)? = null
) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(media.likesCount) }
    var isSaved by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(true) }

    val effectiveUrl = media.mediaUrl.ifBlank { media.thumbnailUrl }.ifBlank { TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64 }
    val username = user?.username?.ifBlank { "angll_trujillo" } ?: "angll_trujillo"
    val avatarUrl = user?.profilePictureUrl?.ifBlank { TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64 } ?: TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Barra superior: Botón atrás circular + "Publicaciones" + username
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp, start = 14.dp, end = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262626).copy(alpha = 0.9f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Atrás",
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                                .offset(x = 2.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Publicaciones",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = username,
                            fontSize = 12.sp,
                            color = Color(0xFFA8A8A8)
                        )
                    }

                    // Spacer equilibrador
                    Spacer(modifier = Modifier.size(36.dp))
                }

                // Fila del autor con música y [E]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262626))
                    ) {
                        RemoteImage(
                            url = avatarUrl,
                            contentDescription = username,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = username,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Robot95 • Pluma Roja",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF737373))
                                    .padding(horizontal = 3.dp, vertical = 0.5.dp)
                            ) {
                                Text(
                                    text = "E",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Icon(
                        Icons.Filled.MoreHoriz,
                        contentDescription = "Opciones",
                        tint = Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { }
                    )
                }

                // Foto de la publicación con badge 1/4 y botón mute
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.82f)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    RemoteImage(
                        url = effectiveUrl,
                        contentDescription = media.caption,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Badge 1/4 estilo iOS
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "1/4",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Botón de silencio en la esquina inferior derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable { isMuted = !isMuted },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isMuted) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                            contentDescription = "Audio",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Barra de estadísticas del creador (Insights Bar)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(
                            Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = Color(0xFF0095F6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "2.4 mil • Ver estadísticas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0095F6)
                        )
                    }

                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0095F6),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Promocionar publicación",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Puntos de paginación del carrusel (4 dots)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0095F6))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF385185))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF385185))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF385185))
                    )
                }

                // Barra de iconos de interacción (Like, Comment, Share, Save)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                isLiked = !isLiked
                                if (isLiked) likesCount++ else likesCount--
                                onLikeToggle?.invoke(isLiked)
                            }
                        ) {
                            Icon(
                                if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Me gusta",
                                tint = if (isLiked) Color(0xFFFF3040) else Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { }
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ModeComment,
                                contentDescription = "Comentar",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${media.commentsCount}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Filled.Repeat,
                                contentDescription = "Republicar",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }

                        IconButton(onClick = { }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Send,
                                contentDescription = "Enviar",
                                tint = Color.White,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                    }

                    IconButton(onClick = { isSaved = !isSaved }) {
                        Icon(
                            if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Fila de Me gusta ("Les gusta a francogalvezc y otros")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF555555))
                                .border(1.dp, Color.Black, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = (-5).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF888888))
                                .border(1.dp, Color.Black, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .offset(x = (-10).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFAAAAAA))
                                .border(1.dp, Color.Black, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = buildAnnotatedString {
                            append("Les gusta a ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("francogalvezc")
                            }
                            append(" y ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("otros")
                            }
                        },
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                // Pie de foto (Caption): angll_trujillo Me entiendes?
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(username)
                            }
                            append(" ")
                            append(media.caption.ifBlank { "Me entiendes?" })
                        },
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 17.sp
                    )
                }

                // Comentario destacado: freddy._cord Tirando mas mole que arroz el tio 🔥 ❤️
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("freddy._cord")
                            }
                            append(" ")
                            append("Tirando mas mole que arroz el tio 🔥")
                        },
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = "Me gusta comentario",
                        tint = Color(0xFFFF3040),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
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
