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
                role = "Arquitectura KMP & Estructura del Proyecto",
                instagramUsername = "arbe__yy",
                instagramUrl = "https://www.instagram.com/arbe__yy/",
                avatarUrl = TeamAvatars.ARBE_YY_AVATAR_BASE64,
                subject = "Desarrollo Móvil Integral",
                semester = "10-A",
                description = "Diseño de la arquitectura limpia, configuración de Kotlin Multiplatform, modularización en shared y orquestación Gradle.",
                avatarColor = 0,
                instagramBio = "Arbey Torres\n67 seguidores • 52 siguiendo • 5 publicaciones\n\n👨‍💻 KMP Architect | Software Engineering\n🚀 10-A • Universidad Tecnológica de la Selva\n📱 Arquitecturas limpias & Compose Multiplatform",
                instagramFollowers = 67,
                instagramFollowing = 52,
                instagramPosts = 5
            ),
            Developer(
                name = "Angel Emmanuel Trujillo López",
                role = "Lead Developer & Meta Graph API Integration",
                instagramUsername = "angll_trujillo",
                instagramUrl = "https://www.instagram.com/angll_trujillo/",
                avatarUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
                subject = "Desarrollo Móvil Integral",
                semester = "10-A",
                description = "Cliente HTTP con Ktor 3, autenticación OAuth 2.0 con Instagram/Meta, serialización JSON y gestión de estado en tiempo real.",
                avatarColor = 1,
                instagramBio = "𝐀𝐧𝐠𝐞𝐥 𝐓𝐫𝐮𝐣𝐢𝐥𝐥𝐨\n\"Without a doubt, you’re everything I need.\"\n246 seguidores • 248 siguiendo\n\n⚡ Lead Developer @ GramPulse\n🌐 Ktor 3, OAuth 2.0 & Meta Graph API Integration\n🎓 10-A • Ing. en Desarrollo y Gestión de Software",
                instagramFollowers = 246,
                instagramFollowing = 248,
                instagramPosts = 1
            ),
            Developer(
                name = "Carlos Jafet Domínguez López",
                role = "UI/UX Designer & Compose Specialist",
                instagramUsername = "jzzstyl",
                instagramUrl = "https://www.instagram.com/jzzstyl/",
                avatarUrl = TeamAvatars.JZZSTYL_AVATAR_BASE64,
                subject = "Desarrollo Móvil Integral",
                semester = "10-A",
                description = "Diseño de interfaz glossy dark, diseño de feed, historias interactivas, animaciones y microinteracciones de Instagram.",
                avatarColor = 2,
                instagramBio = "Jaffe\n219 seguidores • 86 siguiendo • 0 publicaciones\n\n🎨 UI/UX Designer & Mobile Engineer\n✨ Creando experiencias con Jetpack Compose Multiplatform\n📚 10-A • Ing. en Desarrollo y Gestión de Software",
                instagramFollowers = 219,
                instagramFollowing = 86,
                instagramPosts = 0
            ),
            Developer(
                name = "Roldan Aldemar Arcos Gómez",
                role = "Backend, QA & Multiplatform Deployment",
                instagramUsername = "rdy__a",
                instagramUrl = "https://www.instagram.com/rdy__a/",
                avatarUrl = TeamAvatars.RDY_A_AVATAR_BASE64,
                subject = "Desarrollo Móvil Integral",
                semester = "10-A",
                description = "Pruebas multiplataforma en Android y Desktop, control de calidad, testing de integración y despliegue del proyecto.",
                avatarColor = 3,
                instagramBio = "Roldan\n3 seguidores • 2 siguiendo • 0 publicaciones\n\n🔍 QA Engineer & Multiplatform Testing\n🧪 Código limpio, cero bugs y rendimiento óptimo\n💻 10-A • Ing. en Desarrollo y Gestión de Software",
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
            .background(MaterialTheme.colorScheme.background)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkColors.Surface),
        border = BorderStroke(0.5.dp, DarkColors.Outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkColors.SurfaceVariant)
                        .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.School,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNIVERSIDAD TECNOLÓGICA DE LA SELVA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "Tecnología de la Información y Comunicación",
                        fontSize = 11.sp,
                        color = Color(0xFF8E8E93),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DarkColors.Outline.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            AcademicItem(label = "Carrera", value = "Ingeniería en Desarrollo y Gestión de Software")
            AcademicItem(label = "Asignatura", value = "Desarrollo Móvil Integral")
            AcademicItem(label = "Unidad de Aprendizaje", value = "2")
            AcademicItem(label = "Docente", value = "Armando Méndez Morales")
            AcademicItem(label = "Grado y Grupo", value = "10-A")
            AcademicItem(label = "Stack Tecnológico", value = "Kotlin Multiplatform • Jetpack Compose • Ktor 3")
        }
    }
}

@Composable
private fun AcademicItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f).padding(start = 8.dp)
        )
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
 * Modal detallado del perfil de Instagram del desarrollador.
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

    var selectedPostTopic by remember { mutableStateOf<String?>(null) }

    val uriHandler = LocalUriHandler.current

    val devHighlights = remember(developer.name) {
        when {
            developer.name.contains("Arbey", ignoreCase = true) ->
                listOf("Arquitectura 🏛️", "KMP 🚀", "Gradle ⚙️", "Clean Code 💻")
            developer.name.contains("Angel", ignoreCase = true) ->
                listOf("Lead Dev 👑", "Meta API 🌐", "Ktor 3 ⚡", "OAuth 2.0 🔑")
            developer.name.contains("Carlos", ignoreCase = true) ->
                listOf("UI/UX 🎨", "Compose 📱", "Glossy ✨", "Dark Mode 🌙")
            developer.name.contains("Roldan", ignoreCase = true) ->
                listOf("QA Testing 🔍", "CI/CD 🚀", "Android 🤖", "Desktop 🖥️")
            else ->
                listOf("Proyectos 🚀", "Tech Stack 💻", "Universidad 🎓", "Setup 🖥️")
        }
    }

    val devProjectPosts = remember(developer.name) {
        when {
            developer.name.contains("Arbey", ignoreCase = true) -> listOf(
                "Estructura modular Kotlin Multiplatform en shared",
                "Configuración Gradle, dependencias y targets KMP",
                "Arquitectura Repository y separación de capas",
                "Integración de build logic multiplataforma",
                "Gestión de modelos de datos compartidos",
                "Optimización de compilación Android & JVM"
            )
            developer.name.contains("Angel", ignoreCase = true) -> listOf(
                "Cliente HTTP con Ktor 3 y serialización JSON",
                "Flujo OAuth 2.0 e intercambio de código por Token",
                "Consumo de Meta Graph API (/me, /media, /stories)",
                "Gestión reactiva de estado de sesión y repositorios",
                "Peticiones de feed, métricas y detalles en tiempo real",
                "Manejo seguro de tokens y autorización persistente"
            )
            developer.name.contains("Carlos", ignoreCase = true) -> listOf(
                "Diseño visual glossy dark estilo Apple y Meta",
                "Componentes interactivos de Compose Multiplatform",
                "Feed, Stories bar, diálogos y transiciones fluidas",
                "Paleta de colores, tipografía y micro-animaciones",
                "Diseño responsivo para móvil Android y Desktop",
                "Efectos glassmorphism y barras de navegación"
            )
            developer.name.contains("Roldan", ignoreCase = true) -> listOf(
                "Pruebas multiplataforma en emuladores y JVM",
                "Validación de interoperabilidad Android y Desktop",
                "QA de flujos de autenticación y carga de medios",
                "Detección y corrección de bugs de renderizado",
                "Testing de rendimiento y consumo de memoria",
                "Despliegue y empaquetado final de la solución"
            )
            else -> listOf(
                "Arquitectura KMP y estructura limpia en shared",
                "Integración de autenticación OAuth y tokens",
                "Diseño de interfaz declarativa con Compose",
                "Manejo de estados reactivos y navegación",
                "Consumo y serialización de Graph API de Meta",
                "Optimizaciones de compilación y empaquetado"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (developer.instagramUrl.isNotBlank()) {
                    Button(
                        onClick = {
                            uriHandler.openUri(developer.instagramUrl)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.OpenInBrowser,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ver Instagram", color = Color.Black, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = if (developer.instagramUrl.isNotBlank()) Modifier.weight(0.9f) else Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text("Cerrar", maxLines = 1)
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
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "@${developer.instagramUsername}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Icon(
                    Icons.Filled.Verified,
                    contentDescription = null,
                    tint = InstagramColors.Purple,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header con avatar y métricas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InstagramAvatar(
                        name = developer.name,
                        size = 70.dp,
                        imageUrl = developer.avatarUrl,
                        gradientColors = gradientList,
                        fontSize = 24
                    )
                    Spacer(modifier = Modifier.width(14.dp))
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

                // Nombre y rol
                Text(
                    text = developer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = developer.role,
                    fontSize = 12.sp,
                    color = InstagramColors.Orange,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Bio
                Text(
                    text = developer.instagramBio,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Botones interactivos "Seguir" y "Abrir en Instagram"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleFollow,
                        modifier = Modifier.weight(1f).height(36.dp),
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
                        modifier = Modifier.weight(1f).height(36.dp),
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

                // Historias destacadas del perfil
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(devHighlights) { highlight ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Brush.sweepGradient(gradientList))
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(DarkColors.SurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = highlight,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contribuciones y publicaciones en el proyecto
                Text(
                    text = "Aportes al Proyecto GramPulse",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid 3x2 de contribuciones simuladas
                val chunkedPosts = devProjectPosts.chunked(3)
                chunkedPosts.forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowItems.forEach { topic ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                gradientList.first().copy(alpha = 0.5f),
                                                gradientList.last().copy(alpha = 0.3f)
                                            )
                                        )
                                    )
                                    .clickable { selectedPostTopic = topic },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Code,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                if (selectedPostTopic != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkColors.SurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "📌 Tarea: $selectedPostTopic",
                            fontSize = 12.sp,
                            color = InstagramColors.LightYellow,
                            fontWeight = FontWeight.Medium
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
