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

    var selectedContentTab by remember { mutableStateOf(0) } // 0: Posts, 1: Reels/Videos, 2: Info API
    var isGridView by remember { mutableStateOf(true) } // Grid vs Feed list
    var searchQuery by remember { mutableStateOf("") }
    var selectedMediaDetail by remember { mutableStateOf<InstagramMedia?>(null) }

    // Bio editable en sesión
    var currentBio by remember { mutableStateOf(user.biography) }
    var showEditBioDialog by remember { mutableStateOf(false) }

    // Diálogo de historia seleccionada
    var selectedStoryTitle by remember { mutableStateOf<String?>(null) }
    var selectedStoryMedia by remember { mutableStateOf<InstagramMedia?>(null) }
    var isSelectedStoryLive by remember { mutableStateOf(false) }

    // Generar la lista dinámica de Historias y Destacadas reales
    val highlightItems = remember(user.stories, user.recentMedia) {
        val items = mutableListOf<StoryHighlightItem>()

        // 1. Historias activas obtenidas de Meta Graph API (/me/stories)
        user.stories.forEachIndexed { index, story ->
            val label = if (story.caption.isNotBlank()) {
                val clean = story.caption.lines().firstOrNull()?.trim() ?: story.caption
                clean.take(12)
            } else if (user.stories.size == 1) {
                "Tu Historia"
            } else {
                "Historia ${index + 1}"
            }

            items.add(
                StoryHighlightItem(
                    id = story.id.ifBlank { "live_story_$index" },
                    title = label,
                    imageUrl = story.thumbnailUrl.ifBlank { story.mediaUrl },
                    hasActiveStory = true,
                    isLiveStory = true,
                    story = story
                )
            )
        }

        // 2. Si el usuario tiene publicaciones en el feed, mostrarlas como Historias Destacadas con sus fotos de portada reales
        if (user.recentMedia.isNotEmpty()) {
            user.recentMedia.forEachIndexed { index, media ->
                val label = if (media.caption.isNotBlank()) {
                    val clean = media.caption.lines().firstOrNull()?.trim() ?: media.caption
                    clean.take(12)
                } else {
                    "Destacado ${index + 1}"
                }

                items.add(
                    StoryHighlightItem(
                        id = media.id.ifBlank { "highlight_$index" },
                        title = label,
                        imageUrl = media.thumbnailUrl.ifBlank { media.mediaUrl },
                        hasActiveStory = false,
                        isLiveStory = false,
                        story = media
                    )
                )
            }
        }

        // 3. Si no hay ni historias ni publicaciones (por ejemplo, cuenta vacía), items informativos
        if (items.isEmpty()) {
            items.addAll(
                listOf(
                    StoryHighlightItem("def1", "KMP 🚀", null, false, false, null, Icons.Outlined.Devices),
                    StoryHighlightItem("def2", "Compose 🎨", null, false, false, null, Icons.Outlined.Palette),
                    StoryHighlightItem("def3", "API Ktor ⚡", null, false, false, null, Icons.Outlined.CloudSync),
                    StoryHighlightItem("def4", "Uni 10° 🎓", null, false, false, null, Icons.Outlined.School),
                    StoryHighlightItem("def5", "Docs 📄", null, false, false, null, Icons.Outlined.Description)
                )
            )
        }

        items
    }

    // Filtrar publicaciones según pestaña y búsqueda
    val filteredMedia = remember(user.recentMedia, selectedContentTab, searchQuery) {
        user.recentMedia.filter { media ->
            val matchesTab = when (selectedContentTab) {
                1 -> media.mediaType == "VIDEO"
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                media.caption.contains(searchQuery, ignoreCase = true) ||
                        media.mediaType.contains(searchQuery, ignoreCase = true)
            }
            matchesTab && matchesSearch
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
        ) {
            // Header del perfil
            ProfileHeader(
                user = user,
                currentBio = currentBio,
                isInstagramLogin = isInstagramLogin,
                isVisible = isVisible,
                onRefresh = onRefresh,
                onEditBioClick = { showEditBioDialog = true }
            )

            // Historias destacadas e historias en vivo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (user.stories.isNotEmpty()) "Historias y Destacadas" else "Historias Destacadas",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA1A1A6)
                    )
                    if (user.stories.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF34C759).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "EN VIVO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34C759)
                            )
                        }
                    }
                }

                Text(
                    text = "${highlightItems.size} elementos",
                    fontSize = 11.sp,
                    color = Color(0xFF8E8E93)
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(highlightItems) { item ->
                    InstagramStoryBubble(
                        title = item.title,
                        imageUrl = item.imageUrl,
                        icon = item.icon,
                        hasActiveStory = item.hasActiveStory,
                        onClick = {
                            if (item.story != null) {
                                selectedStoryMedia = item.story
                                isSelectedStoryLive = item.isLiveStory
                            } else {
                                selectedStoryTitle = item.title
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Barra de pestañas de contenido estilo Apple (Blanco y Negro)
            TabRow(
                selectedTabIndex = selectedContentTab,
                containerColor = DarkColors.Background,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedContentTab]),
                        color = Color.White,
                        height = 2.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedContentTab == 0,
                    onClick = { selectedContentTab = 0 },
                    icon = {
                        Icon(
                            if (selectedContentTab == 0) Icons.Filled.GridOn else Icons.Outlined.GridOn,
                            contentDescription = "Publicaciones",
                            tint = if (selectedContentTab == 0) Color.White else Color(0xFF8E8E93)
                        )
                    }
                )
                Tab(
                    selected = selectedContentTab == 1,
                    onClick = { selectedContentTab = 1 },
                    icon = {
                        Icon(
                            if (selectedContentTab == 1) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "Videos",
                            tint = if (selectedContentTab == 1) Color.White else Color(0xFF8E8E93)
                        )
                    }
                )
                Tab(
                    selected = selectedContentTab == 2,
                    onClick = { selectedContentTab = 2 },
                    icon = {
                        Icon(
                            if (selectedContentTab == 2) Icons.Filled.Code else Icons.Outlined.Code,
                            contentDescription = "Detalles API",
                            tint = if (selectedContentTab == 2) Color.White else Color(0xFF8E8E93)
                        )
                    }
                )
            }

            // Vista según pestaña seleccionada
            if (selectedContentTab == 2) {
                // Información técnica de Meta Graph API
                Spacer(modifier = Modifier.height(16.dp))
                AccountInfoSection(user, isInstagramLogin)
            } else {
                // Buscador y selector de Grid/Feed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchInputField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = if (selectedContentTab == 1) "Buscar reels o videos..." else "Buscar publicaciones...",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Alternar vista de cuadrícula vs lista
                    IconButton(
                        onClick = { isGridView = !isGridView },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkColors.SurfaceVariant)
                            .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            if (isGridView) Icons.Filled.ViewAgenda else Icons.Filled.GridView,
                            contentDescription = "Cambiar vista",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Lista / Cuadrícula de publicaciones
                if (filteredMedia.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron publicaciones",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else if (isGridView) {
                    // Vista Cuadrícula estilo 3 columnas de Instagram
                    InstagramMediaGrid(
                        mediaList = filteredMedia,
                        onMediaClick = { selectedMediaDetail = it }
                    )
                } else {
                    // Vista Feed en tarjetas
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        filteredMedia.forEachIndexed { index, media ->
                            PostCard(
                                media = media,
                                index = index,
                                onClick = { selectedMediaDetail = media }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
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
        // Halo de luz ambiental superior estilo glossy
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.White.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Fila superior: Avatar + Estadísticas lado a lado (estilo Apple limpio y reluciente)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InstagramAvatar(
                    name = user.fullName.ifEmpty { user.username },
                    imageUrl = user.profilePictureUrl,
                    size = 82.dp,
                    fontSize = 28,
                    hasActiveStory = true
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

            Spacer(modifier = Modifier.height(14.dp))

            // Nombre y badge verificado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = user.fullName.ifEmpty { user.username },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (user.accountType.uppercase() == "BUSINESS" || user.accountType.uppercase() == "CREATOR") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = "Verificado",
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                }
            }

            // Username y origen
            Text(
                text = "@${user.username} • ${if (isInstagramLogin) "Meta Graph API" else "Cuenta Local"}",
                fontSize = 13.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Biografía
            if (currentBio.isNotBlank()) {
                Text(
                    text = currentBio,
                    fontSize = 13.sp,
                    color = Color(0xFFD1D1D6),
                    lineHeight = 18.sp
                )
            }

            if (user.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Link,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = user.website,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botones de acción directa estilo Apple con bordes glossy de cristal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditBioClick,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkColors.SurfaceVariant.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Editar Perfil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onRefresh,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                        )
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = DarkColors.SurfaceVariant.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = "Refrescar",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sincronizar",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
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
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(
                                                        InstagramColors.DeepPurple.copy(alpha = 0.5f),
                                                        InstagramColors.Pink.copy(alpha = 0.3f)
                                                    )
                                                )
                                            ),
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
                            // Fondo gradiente de respaldo
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                InstagramColors.DeepPurple.copy(alpha = 0.5f),
                                                InstagramColors.Pink.copy(alpha = 0.3f)
                                            )
                                        )
                                    ),
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

@Composable
private fun PostCard(
    media: InstagramMedia,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkColors.Surface
        )
    ) {
        Column {
            val effectiveUrl = media.mediaUrl.ifBlank { media.thumbnailUrl }
            if (effectiveUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(DarkColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    RemoteImage(
                        url = effectiveUrl,
                        contentDescription = media.caption,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        errorPlaceholder = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (media.mediaType) {
                                        "VIDEO" -> Icons.Filled.PlayCircle
                                        "CAROUSEL_ALBUM" -> Icons.Filled.Collections
                                        else -> Icons.Outlined.Image
                                    },
                                    contentDescription = media.mediaType,
                                    modifier = Modifier.size(44.dp),
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    InstagramColors.DeepPurple.copy(alpha = 0.4f),
                                    InstagramColors.Pink.copy(alpha = 0.3f),
                                    InstagramColors.Orange.copy(alpha = 0.3f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            when (media.mediaType) {
                                "VIDEO" -> Icons.Filled.PlayCircle
                                "CAROUSEL_ALBUM" -> Icons.Filled.Collections
                                else -> Icons.Outlined.Image
                            },
                            contentDescription = media.mediaType,
                            modifier = Modifier.size(44.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = media.mediaType.replace("_", " "),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatCount(media.likesCount)} Me gusta • ${media.commentsCount} comentarios",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = InstagramColors.Pink
                    )
                    Text(
                        text = formatTimestamp(media.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = media.caption,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun AccountInfoSection(user: InstagramUser, isInstagramLogin: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkColors.Surface),
        border = BorderStroke(1.dp, InstagramColors.DeepPurple.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.SettingsApplications,
                    contentDescription = null,
                    tint = InstagramColors.Orange,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Detalles Técnicos de Integración",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            InfoItem(label = "User ID", value = user.id)
            InfoItem(label = "Username", value = "@${user.username}")
            InfoItem(label = "Tipo de Cuenta", value = user.accountType)
            InfoItem(
                label = "Modo de Datos",
                value = if (isInstagramLogin) "Meta Graph API (OAuth/Token)" else "Autenticación Local Simulada"
            )
            InfoItem(
                label = "Endpoint Graph API",
                value = "https://graph.instagram.com/me"
            )
            InfoItem(
                label = "Permisos Solicitados",
                value = "instagram_business_basic"
            )
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

private fun formatTimestamp(timestamp: String): String {
    return try {
        val datePart = timestamp.substringBefore("T")
        val parts = datePart.split("-")
        if (parts.size == 3) {
            val months = listOf(
                "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
            )
            val month = parts[1].toIntOrNull()?.let { months.getOrNull(it - 1) } ?: parts[1]
            "$month ${parts[2]}, ${parts[0]}"
        } else {
            timestamp
        }
    } catch (_: Exception) {
        timestamp
    }
}

/**
 * Modelo interno para representar historias activas y destacadas en la UI.
 */
data class StoryHighlightItem(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val hasActiveStory: Boolean,
    val isLiveStory: Boolean,
    val story: InstagramMedia?,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
)

