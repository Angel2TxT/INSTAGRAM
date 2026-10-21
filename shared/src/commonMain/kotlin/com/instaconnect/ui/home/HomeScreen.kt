package com.instaconnect.ui.home

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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.models.InstagramMedia
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.theme.DarkColors
import com.instaconnect.theme.InstagramColors
import com.instaconnect.ui.components.*

/**
 * Pantalla principal con vista de perfil completa estilo Instagram.
 */
@Composable
fun HomeScreen(
    user: InstagramUser,
    isInstagramLogin: Boolean,
    onRefresh: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    var selectedContentTab by remember { mutableStateOf(0) } // 0: Posts, 1: Tagged
    var selectedMediaDetail by remember { mutableStateOf<InstagramMedia?>(null) }

    // Bio editable en sesión
    var currentBio by remember { mutableStateOf(user.biography) }
    var showEditBioDialog by remember { mutableStateOf(false) }

    // Diálogo de historia seleccionada
    var selectedStoryTitle by remember { mutableStateOf<String?>(null) }
    var selectedStoryMedia by remember { mutableStateOf<InstagramMedia?>(null) }
    var isSelectedStoryLive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header del perfil auténtico de Instagram
            ProfileHeader(
                user = user,
                currentBio = currentBio,
                isInstagramLogin = isInstagramLogin,
                isVisible = isVisible,
                onRefresh = onRefresh,
                onEditBioClick = { showEditBioDialog = true }
            )

            // Sección de Historias Destacadas auténtica estilo Instagram
            if (user.stories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(user.stories) { story ->
                        InstagramStoryBubble(
                            title = if (story.caption.isNotBlank()) story.caption.take(10) else "Historia",
                            imageUrl = story.thumbnailUrl.ifBlank { story.mediaUrl },
                            hasActiveStory = true,
                            onClick = {
                                selectedStoryMedia = story
                                isSelectedStoryLive = true
                            }
                        )
                    }
                }
            } else {
                // Si el usuario no tiene historias destacadas, botón nativo de Instagram "+ Nueva"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* Crear destacada */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFF38383A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Nueva destacada",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nueva",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Barra de pestañas oficial de Instagram: Cuadrícula de fotos y Fotos etiquetadas
            TabRow(
                selectedTabIndex = selectedContentTab,
                containerColor = DarkColors.Background,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedContentTab]),
                        color = Color.White,
                        height = 1.5.dp
                    )
                },
                divider = {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF262626))
                }
            ) {
                Tab(
                    selected = selectedContentTab == 0,
                    onClick = { selectedContentTab = 0 },
                    icon = {
                        Icon(
                            if (selectedContentTab == 0) Icons.Filled.GridOn else Icons.Outlined.GridOn,
                            contentDescription = "Publicaciones",
                            tint = if (selectedContentTab == 0) Color.White else Color(0xFF8E8E93),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                )
                Tab(
                    selected = selectedContentTab == 1,
                    onClick = { selectedContentTab = 1 },
                    icon = {
                        Icon(
                            if (selectedContentTab == 1) Icons.Filled.AssignmentInd else Icons.Outlined.AssignmentInd,
                            contentDescription = "Etiquetadas",
                            tint = if (selectedContentTab == 1) Color.White else Color(0xFF8E8E93),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                )
            }

            // Contenido de la pestaña
            if (selectedContentTab == 0) {
                if (user.recentMedia.isEmpty()) {
                    // Estado vacío oficial de Instagram
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 70.dp, horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aún no hay publicaciones",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Cuadrícula 3x3 de Instagram directa y limpia
                    InstagramMediaGrid(
                        mediaList = user.recentMedia,
                        onMediaClick = { selectedMediaDetail = it }
                    )
                }
            } else {
                // Pestaña de fotos etiquetadas oficial de Instagram
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp, horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.AssignmentInd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Fotos y videos en los que apareces",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cuando las personas te etiqueten en fotos y videos, aparecerán aquí.",
                            fontSize = 13.sp,
                            color = Color(0xFF8E8E93),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal de detalle de publicación al hacer clic
    selectedMediaDetail?.let { media ->
        PostDetailDialog(
            media = media,
            user = user,
            onDismiss = { selectedMediaDetail = null }
        )
    }

    // Modal visor de Historia real o destacada
    selectedStoryMedia?.let { story ->
        StoryDetailDialog(
            user = user,
            story = story,
            isLiveStory = isSelectedStoryLive,
            onDismiss = { selectedStoryMedia = null }
        )
    }

    // Diálogo informativo de Historia destacada
    selectedStoryTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { selectedStoryTitle = null },
            confirmButton = {
                TextButton(
                    onClick = { selectedStoryTitle = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = InstagramColors.Pink)
                ) {
                    Text("Cerrar")
                }
            },
            containerColor = DarkColors.Surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Historia Destacada: $title", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Esta historia agrupa contenido relacionado con el desarrollo de la aplicación GramPulse en el entorno $title.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }

    // Diálogo para editar biografía
    if (showEditBioDialog) {
        var tempBio by remember { mutableStateOf(currentBio) }
        AlertDialog(
            onDismissRequest = { showEditBioDialog = false },
            title = { Text("Editar Biografía", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tempBio,
                    onValueChange = { tempBio = it },
                    label = { Text("Biografía") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InstagramColors.Pink,
                        focusedLabelColor = InstagramColors.Pink,
                        cursorColor = InstagramColors.Pink,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentBio = tempBio
                        showEditBioDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = InstagramColors.Pink)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBioDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = DarkColors.Surface
        )
    }
}

@Composable
private fun ProfileHeader(
    user: InstagramUser,
    currentBio: String,
    isInstagramLogin: Boolean,
    isVisible: Boolean,
    onRefresh: () -> Unit,
    onEditBioClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Fila superior: Avatar + Métricas (Posts, Seguidores, Siguiendo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InstagramAvatar(
                    name = user.fullName.ifEmpty { user.username },
                    imageUrl = user.profilePictureUrl,
                    size = 78.dp,
                    fontSize = 26,
                    hasActiveStory = user.stories.isNotEmpty()
                )

                Spacer(modifier = Modifier.width(20.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(
                        value = formatCount(user.mediaCount),
                        label = "Posts"
                    )
                    StatItem(
                        value = formatCount(user.followersCount),
                        label = "Seguidores"
                    )
                    StatItem(
                        value = formatCount(user.followingCount),
                        label = "Siguiendo"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre completo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.fullName.ifEmpty { user.username },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (user.accountType.uppercase() == "BUSINESS" || user.accountType.uppercase() == "CREATOR") {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = "Verificado",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF0095F6)
                    )
                }
            }

            // Biografía auténtica
            if (currentBio.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = currentBio,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 17.sp
                )
            }

            // Enlace web estilo Instagram
            if (user.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        try { uriHandler.openUri(user.website) } catch (_: Exception) {}
                    }
                ) {
                    Icon(
                        Icons.Outlined.Link,
                        contentDescription = null,
                        tint = Color(0xFFE0F1FF),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.website.removePrefix("https://").removePrefix("http://"),
                        fontSize = 13.sp,
                        color = Color(0xFFE0F1FF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones oficiales de acción de Instagram: "Editar perfil", "Compartir perfil" y "+👤"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEditBioClick,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF262626),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Editar perfil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = { /* Compartir perfil */ },
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF262626),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Compartir perfil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF262626))
                ) {
                    Icon(
                        Icons.Outlined.PersonAdd,
                        contentDescription = "Descubrir personas",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Cuadrícula de publicaciones de 3 columnas estilo Instagram.
 */
@Composable
private fun InstagramMediaGrid(
    mediaList: List<InstagramMedia>,
    onMediaClick: (InstagramMedia) -> Unit
) {
    val chunked = mediaList.chunked(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                rowItems.forEach { media ->
                    val effectiveUrl = media.mediaUrl.ifBlank { media.thumbnailUrl }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(DarkColors.SurfaceVariant)
                            .clickable { onMediaClick(media) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (effectiveUrl.isNotBlank()) {
                            RemoteImage(
                                url = effectiveUrl,
                                contentDescription = media.caption,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                errorPlaceholder = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF1E1E1E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            when (media.mediaType) {
                                                "VIDEO" -> Icons.Filled.PlayArrow
                                                "CAROUSEL_ALBUM" -> Icons.Filled.Collections
                                                else -> Icons.Outlined.PhotoCamera
                                            },
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.75f),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            )
                        } else {
                            // Fondo de respaldo oscuro neutro
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF1E1E1E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (media.mediaType) {
                                        "VIDEO" -> Icons.Filled.PlayArrow
                                        "CAROUSEL_ALBUM" -> Icons.Filled.Collections
                                        else -> Icons.Outlined.PhotoCamera
                                    },
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        // Badge superior derecho si es video o álbum
                        if (media.mediaType != "IMAGE") {
                            Icon(
                                when (media.mediaType) {
                                    "VIDEO" -> Icons.Filled.PlayArrow
                                    else -> Icons.Filled.Collections
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(16.dp)
                            )
                        }
                    }
                }

                // Si la fila tiene menos de 3 elementos, rellenar espacios vacíos
                val missing = 3 - rowItems.size
                repeat(missing) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}



