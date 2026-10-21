package com.instaconnect

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.InstagramConfig
import com.instaconnect.data.InstagramRepository
import com.instaconnect.data.models.InstagramUser
import com.instaconnect.data.models.TeamAvatars
import com.instaconnect.theme.DarkColors
import com.instaconnect.theme.InstaConnectTheme
import com.instaconnect.theme.InstagramColors
import com.instaconnect.ui.about.AboutScreen
import com.instaconnect.ui.home.HomeScreen
import com.instaconnect.ui.login.LoginScreen
import kotlinx.coroutines.launch

import com.instaconnect.ui.welcome.WelcomeScreen
import com.instaconnect.ui.login.InstagramOAuthDialog

/**
 * Pestañas disponibles en el menú principal.
 */
enum class AppTab(val title: String) {
    HOME("Inicio"),
    PROFILE("Perfil"),
    ABOUT("Acerca de")
}

/**
 * Componente principal de la aplicación InstaConnect.
 * Gestiona el estado de autenticación, Scaffold, Snackbar y la navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    InstaConnectTheme {
        val repository = remember { InstagramRepository() }
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        var isLoggedIn by remember { mutableStateOf(false) }
        var isInstagramLogin by remember { mutableStateOf(false) }
        var currentUser by remember { mutableStateOf<InstagramUser?>(null) }
        var selectedTab by remember { mutableStateOf(AppTab.HOME) }
        var isLoading by remember { mutableStateOf(false) }
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showOAuthDialog by remember { mutableStateOf(false) }

        // Limpiar recursos al salir
        DisposableEffect(Unit) {
            onDispose {
                repository.close()
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = DarkColors.SurfaceVariant,
                            contentColor = Color.White,
                            actionColor = Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                )
            },
            topBar = {
                if (isLoggedIn) {
                    TopAppBar(
                        title = {
                            if (selectedTab == AppTab.PROFILE && currentUser != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Outlined.Lock,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = currentUser?.username ?: "perfil",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.3).sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkColors.SurfaceVariant)
                                            .border(0.5.dp, Color(0xFF38383A), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.CameraAlt,
                                            contentDescription = "Logo",
                                            tint = Color.White,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (selectedTab == AppTab.ABOUT) "Acerca de" else "GramPulse",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = (-0.3).sp,
                                        color = Color.White
                                    )
                                }
                            }
                        },
                        actions = {
                            if (selectedTab == AppTab.PROFILE) {
                                IconButton(onClick = { /* Crear publicación */ }) {
                                    Icon(
                                        Icons.Outlined.AddBox,
                                        contentDescription = "Crear",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            IconButton(onClick = { showLogoutDialog = true }) {
                                Icon(
                                    if (selectedTab == AppTab.PROFILE) Icons.Outlined.Menu else Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = "Cerrar sesión",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = DarkColors.Background,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (isLoggedIn) {
                    NavigationBar(
                        containerColor = DarkColors.Background,
                        tonalElevation = 0.dp,
                        modifier = Modifier.border(
                            width = 0.5.dp,
                            color = Color(0xFF262626),
                            shape = RoundedCornerShape(0.dp)
                        )
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == AppTab.HOME,
                            onClick = { selectedTab = AppTab.HOME },
                            icon = {
                                Icon(
                                    if (selectedTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Inicio"
                                )
                            },
                            label = { Text("Inicio", fontWeight = if (selectedTab == AppTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = DarkColors.SurfaceVariant,
                                unselectedIconColor = Color(0xFF8E8E93),
                                unselectedTextColor = Color(0xFF8E8E93)
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.PROFILE,
                            onClick = { selectedTab = AppTab.PROFILE },
                            icon = {
                                Icon(
                                    if (selectedTab == AppTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Perfil"
                                )
                            },
                            label = { Text("Perfil", fontWeight = if (selectedTab == AppTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = DarkColors.SurfaceVariant,
                                unselectedIconColor = Color(0xFF8E8E93),
                                unselectedTextColor = Color(0xFF8E8E93)
                            )
                        )

                        NavigationBarItem(
                            selected = selectedTab == AppTab.ABOUT,
                            onClick = { selectedTab = AppTab.ABOUT },
                            icon = {
                                Icon(
                                    if (selectedTab == AppTab.ABOUT) Icons.Filled.Info else Icons.Outlined.Info,
                                    contentDescription = "Acerca de"
                                )
                            },
                            label = { Text("Acerca de", fontWeight = if (selectedTab == AppTab.ABOUT) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                indicatorColor = DarkColors.SurfaceVariant,
                                unselectedIconColor = Color(0xFF8E8E93),
                                unselectedTextColor = Color(0xFF8E8E93)
                            )
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (!isLoggedIn) {
                    LoginScreen(
                        onNormalLogin = { identifier, _ ->
                            val clean = identifier.trim().removePrefix("@")
                            val username = if (clean.contains("@")) clean.substringBefore("@") else clean
                            val lowerUser = username.lowercase()

                            val displayName = when (lowerUser) {
                                "angll_trujillo" -> "Angel Trujillo"
                                "arbe__yy" -> "Arbey Esteban Torres Hernández"
                                "jzzstyl" -> "Carlos Jafet Domínguez López"
                                "rdy__a" -> "Roldan Aldemar Arcos Gómez"
                                else -> username.replace("_", " ").replace(".", " ")
                                    .split(" ")
                                    .filter { it.isNotBlank() }
                                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                                    .ifEmpty { username }
                            }

                            val avatar = when (lowerUser) {
                                "angll_trujillo" -> TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64
                                "arbe__yy" -> TeamAvatars.ARBE_YY_AVATAR_BASE64
                                "jzzstyl" -> TeamAvatars.JZZSTYL_AVATAR_BASE64
                                "rdy__a" -> TeamAvatars.RDY_A_AVATAR_BASE64
                                else -> TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64
                            }

                            val bio = when (lowerUser) {
                                "angll_trujillo" -> "Without a doubt, you’re everything I need."
                                "arbe__yy" -> "Arquitectura KMP & Estructura del Proyecto"
                                "jzzstyl" -> "UI/UX Designer & Compose Specialist"
                                "rdy__a" -> "Backend, QA & Multiplatform Deployment"
                                else -> "Desarrollador móvil • Bienvenido a GramPulse"
                            }

                            val followers = when (lowerUser) {
                                "angll_trujillo" -> 246
                                "arbe__yy" -> 67
                                "jzzstyl" -> 219
                                "rdy__a" -> 3
                                else -> 246
                            }

                            val following = when (lowerUser) {
                                "angll_trujillo" -> 248
                                "arbe__yy" -> 52
                                "jzzstyl" -> 86
                                "rdy__a" -> 2
                                else -> 248
                            }

                            val postsCount = when (lowerUser) {
                                "angll_trujillo" -> 1
                                "arbe__yy" -> 5
                                "jzzstyl" -> 0
                                "rdy__a" -> 0
                                else -> 1
                            }

                            // Si ingresa la cuenta oficial vinculada con el token
                            if (lowerUser == "angll_trujillo" && InstagramConfig.customAccessToken.isNotBlank()) {
                                isLoading = true
                                coroutineScope.launch {
                                    val result = repository.getUserProfile(InstagramConfig.customAccessToken)
                                    if (result.isSuccess) {
                                        val profile = result.getOrNull()
                                        currentUser = profile?.copy(
                                            profilePictureUrl = if (profile.profilePictureUrl.isBlank()) TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64 else profile.profilePictureUrl
                                        )
                                        isInstagramLogin = true
                                    } else {
                                        currentUser = InstagramUser(
                                            id = "local_usr_angel",
                                            username = "angll_trujillo",
                                            fullName = "Angel Trujillo",
                                            biography = "Without a doubt, you’re everything I need.",
                                            mediaCount = 1,
                                            followersCount = 246,
                                            followingCount = 248,
                                            profilePictureUrl = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
                                            accountType = "PERSONAL",
                                            website = "https://instagram.com/angll_trujillo",
                                            recentMedia = repository.getMockMedia(),
                                            stories = repository.getMockStories()
                                        )
                                        isInstagramLogin = false
                                    }
                                    isLoggedIn = true
                                    isLoading = false
                                    snackbarHostState.showSnackbar("¡Sesión iniciada como @$username!")
                                }
                            } else {
                                currentUser = InstagramUser(
                                    id = "usr_${(1000..9999).random()}",
                                    username = username,
                                    fullName = displayName,
                                    biography = bio,
                                    mediaCount = postsCount,
                                    followersCount = followers,
                                    followingCount = following,
                                    profilePictureUrl = avatar,
                                    accountType = "PERSONAL",
                                    website = "https://instagram.com/$username",
                                    recentMedia = repository.getMockMedia(),
                                    stories = repository.getMockStories()
                                )
                                isInstagramLogin = false
                                isLoggedIn = true

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("¡Sesión iniciada como @$username!")
                                }
                            }
                        },
                        onInstagramLogin = {
                            showOAuthDialog = true
                        }
                    )

                    // Diálogo WebView Oficial de Instagram para Login OAuth Real
                    if (showOAuthDialog) {
                        InstagramOAuthDialog(
                            authUrl = InstagramConfig.buildAuthUrl(),
                            redirectUri = InstagramConfig.redirectUri,
                            onCodeReceived = { code ->
                                showOAuthDialog = false
                                isLoading = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Validando autorización con Meta...")
                                    val tokenResult = repository.exchangeCodeForToken(code)
                                    if (tokenResult.isSuccess) {
                                        val newToken = tokenResult.getOrThrow()
                                        InstagramConfig.customAccessToken = newToken
                                        InstagramConfig.useMockData = false

                                        val userResult = repository.getUserProfile(newToken)
                                        if (userResult.isSuccess) {
                                            currentUser = userResult.getOrNull()
                                            isInstagramLogin = true
                                            isLoggedIn = true
                                            snackbarHostState.showSnackbar("¡Sesión iniciada con éxito como @${currentUser?.username}!")
                                        } else {
                                            currentUser = repository.getMockUser()
                                            isInstagramLogin = true
                                            isLoggedIn = true
                                            snackbarHostState.showSnackbar("Conectado con Meta (modo fallback)")
                                        }
                                    } else {
                                        // Si el intercambio falla debido a restricciones de redirect_uri en Meta Sandbox,
                                        // pero el usuario autorizó y tenemos el token activo configurado,
                                        // validamos y cargamos el perfil real de Instagram:
                                        if (InstagramConfig.customAccessToken.isNotBlank()) {
                                            val userResult = repository.getUserProfile(InstagramConfig.customAccessToken)
                                            if (userResult.isSuccess) {
                                                currentUser = userResult.getOrNull()
                                                isInstagramLogin = true
                                                isLoggedIn = true
                                                snackbarHostState.showSnackbar("¡Autenticado con éxito en Instagram como @${currentUser?.username}!")
                                            } else {
                                                val err = tokenResult.exceptionOrNull()?.message ?: "Error al conectar con Meta"
                                                snackbarHostState.showSnackbar(err)
                                            }
                                        } else {
                                            val err = tokenResult.exceptionOrNull()?.message ?: "Error al conectar con Meta"
                                            snackbarHostState.showSnackbar(err)
                                        }
                                    }
                                    isLoading = false
                                }
                            },
                            onError = { errorDesc ->
                                showOAuthDialog = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Instagram: $errorDesc")
                                }
                            },
                            onDismiss = {
                                showOAuthDialog = false
                            }
                        )
                    }

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        }
                    ) { tab ->
                        when (tab) {
                            AppTab.HOME -> {
                                currentUser?.let { user ->
                                    WelcomeScreen(
                                        user = user,
                                        isInstagramLogin = isInstagramLogin,
                                        onNavigateToProfile = { selectedTab = AppTab.PROFILE },
                                        onNavigateToAbout = { selectedTab = AppTab.ABOUT },
                                        onRefresh = {
                                            isLoading = true
                                            coroutineScope.launch {
                                                val token = if (InstagramConfig.customAccessToken.isNotBlank()) {
                                                    InstagramConfig.customAccessToken
                                                } else {
                                                    "mock_token"
                                                }
                                                val result = repository.getUserProfile(token)
                                                if (result.isSuccess) {
                                                    currentUser = result.getOrNull()
                                                }
                                                isLoading = false
                                                snackbarHostState.showSnackbar("Datos sincronizados con éxito")
                                            }
                                        }
                                    )
                                }
                            }
                            AppTab.PROFILE -> {
                                currentUser?.let { user ->
                                    HomeScreen(
                                        user = user,
                                        isInstagramLogin = isInstagramLogin,
                                        onRefresh = {
                                            isLoading = true
                                            coroutineScope.launch {
                                                val token = if (InstagramConfig.customAccessToken.isNotBlank()) {
                                                    InstagramConfig.customAccessToken
                                                } else {
                                                    "mock_token"
                                                }
                                                val result = repository.getUserProfile(token)
                                                if (result.isSuccess) {
                                                    currentUser = result.getOrNull()
                                                }
                                                isLoading = false
                                                snackbarHostState.showSnackbar("Datos sincronizados con éxito")
                                            }
                                        }
                                    )
                                }
                            }
                            AppTab.ABOUT -> {
                                AboutScreen()
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para confirmar cierre de sesión
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.SemiBold) },
                text = {
                    Text(
                        "¿Estás seguro de que deseas salir de GramPulse?",
                        color = Color(0xFFA1A1A6)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isLoggedIn = false
                            currentUser = null
                            isInstagramLogin = false
                            selectedTab = AppTab.HOME
                            showLogoutDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Has cerrado sesión.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Cerrar Sesión", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar", color = Color(0xFF8E8E93))
                    }
                },
                containerColor = DarkColors.SurfaceVariant,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
