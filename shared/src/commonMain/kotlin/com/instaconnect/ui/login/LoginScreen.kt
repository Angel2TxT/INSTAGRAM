package com.instaconnect.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.InstagramConfig
import com.instaconnect.data.models.TeamAvatars
import com.instaconnect.theme.DarkColors
import com.instaconnect.theme.InstagramColors
import com.instaconnect.ui.components.RemoteImage

/**
 * Pantalla de inicio de sesión oficial y exclusiva de Instagram.
 * Sin campos manuales de correo o contraseña: solo autenticación nativa de Instagram.
 */
@Composable
fun LoginScreen(
    onNormalLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onInstagramLogin: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var showApiSettingsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.Background)
    ) {
        // Botón discreto de configuración en la esquina superior derecha
        IconButton(
            onClick = { showApiSettingsDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(36.dp)
        ) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = "Configuración API",
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
        }

        // Contenido principal centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Ícono emblemático de Instagram con gradiente auténtico
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF833AB4),
                                Color(0xFFC13584),
                                Color(0xFFE1306C),
                                Color(0xFFFD1D1D),
                                Color(0xFFF77737)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Instagram",
                    modifier = Modifier.size(42.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Instagram",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Conéctate de forma rápida y segura",
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Tarjeta de inicio de sesión directo estilo Instagram
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkColors.Surface),
                border = BorderStroke(0.5.dp, DarkColors.Outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar del usuario de Instagram con borde gradiente
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF833AB4),
                                        Color(0xFFE1306C),
                                        Color(0xFFF77737)
                                    )
                                ),
                                CircleShape
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF262626)),
                        contentAlignment = Alignment.Center
                    ) {
                        RemoteImage(
                            url = TeamAvatars.ANGLL_TRUJILLO_AVATAR_BASE64,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "angll_trujillo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Cuenta de Instagram",
                        fontSize = 12.sp,
                        color = Color(0xFFA8A8A8)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón Principal: Continuar como @angll_trujillo
                    Button(
                        onClick = {
                            isLoading = true
                            onNormalLogin("angll_trujillo", "password")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0095F6),
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Continuar como @angll_trujillo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón Secundario: Iniciar sesión con otra cuenta (Meta OAuth Oficial)
                    OutlinedButton(
                        onClick = { onInstagramLogin() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF38383A)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Iniciar sesión con otra cuenta",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Badge de seguridad oficial de Meta
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1C1C1E))
                    .border(0.5.dp, Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Seguridad",
                    tint = Color(0xFF34C759),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Autenticación segura oficial vía Meta Graph API",
                    fontSize = 11.sp,
                    color = Color(0xFF8E8E93)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer
            Text(
                text = "Meta © 2026 • Instagram Connect",
                fontSize = 11.sp,
                color = Color(0xFF636366)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal de ajustes de API / Modo Mock
    if (showApiSettingsDialog) {
        var tempUseMock by remember { mutableStateOf(InstagramConfig.useMockData) }
        var tempToken by remember { mutableStateOf(InstagramConfig.customAccessToken) }

        AlertDialog(
            onDismissRequest = { showApiSettingsDialog = false },
            title = {
                Text(
                    "Configuración de API",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Alterna entre datos simulados para pruebas locales o la conexión en vivo con Meta Graph API.",
                        fontSize = 12.sp,
                        color = Color(0xFF8E8E93)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkColors.SurfaceVariant)
                            .border(0.5.dp, DarkColors.Outline, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Modo Demostración", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.White)
                            Text("Sin llamadas a internet", fontSize = 11.sp, color = Color(0xFF8E8E93))
                        }
                        Switch(
                            checked = tempUseMock,
                            onCheckedChange = { tempUseMock = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color.White,
                                uncheckedThumbColor = Color(0xFF8E8E93),
                                uncheckedTrackColor = Color(0xFF2C2C2E)
                            )
                        )
                    }

                    if (!tempUseMock) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = tempToken,
                            onValueChange = { tempToken = it },
                            label = { Text("Token de Acceso") },
                            placeholder = { Text("IGAA...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkColors.SurfaceVariant,
                                unfocusedContainerColor = DarkColors.SurfaceVariant,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color(0xFF2C2C2E),
                                focusedLabelColor = Color.White,
                                unfocusedLabelColor = Color(0xFF8E8E93),
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        InstagramConfig.useMockData = tempUseMock
                        InstagramConfig.customAccessToken = tempToken
                        showApiSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aplicar", color = Color.Black, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiSettingsDialog = false }) {
                    Text("Cerrar", color = Color(0xFF8E8E93))
                }
            },
            containerColor = DarkColors.Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
