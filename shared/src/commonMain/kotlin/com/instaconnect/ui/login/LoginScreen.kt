package com.instaconnect.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instaconnect.data.InstagramConfig
import com.instaconnect.theme.AppleColors
import com.instaconnect.theme.DarkColors

/**
 * Pantalla de inicio de sesión estilo Apple (Minimalista, Negro y Blanco).
 * Diseño limpio con acabados en titanio y contraste puro.
 */
@Composable
fun LoginScreen(
    onNormalLogin: (email: String, password: String) -> Unit,
    onInstagramLogin: () -> Unit
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showApiSettingsDialog by remember { mutableStateOf(false) }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkColors.Background)
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo estilizado minimalista Apple
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkColors.SurfaceVariant)
                        .border(0.5.dp, Color(0xFF38383A), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Logo",
                        modifier = Modifier.size(34.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "GramPulse",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Inicia sesión con tu cuenta de Instagram",
                    fontSize = 14.sp,
                    color = Color(0xFF8E8E93),
                    letterSpacing = (-0.2).sp
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Tarjeta principal estilo iOS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkColors.Surface
                ),
                border = BorderStroke(0.5.dp, DarkColors.Outline)
            ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )

                            // Botón de configuración de API
                            IconButton(
                                onClick = { showApiSettingsDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Settings,
                                    contentDescription = "Configuración API",
                                    tint = Color(0xFF8E8E93),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Campo Usuario de Instagram / Correo / Teléfono
                        OutlinedTextField(
                            value = usernameOrEmail,
                            onValueChange = {
                                usernameOrEmail = it
                                errorMessage = null
                            },
                            label = { Text("Usuario, correo o teléfono") },
                            placeholder = { Text("@usuario o correo", color = Color(0xFF636366), fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF8E8E93),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
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
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Campo Contraseña
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text("Contraseña") },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF8E8E93),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Ver contraseña",
                                        tint = Color(0xFF8E8E93),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
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
                            ),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true
                        )

                        // Mensaje de error
                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFFF453A),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Botón de rellenar demo
                        TextButton(
                            onClick = {
                                usernameOrEmail = "angll_trujillo"
                                password = "password2026"
                                errorMessage = null
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = "Autocompletar @angll_trujillo",
                                fontSize = 11.sp,
                                color = Color(0xFF8E8E93)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Botón Principal: Iniciar Sesión con cualquier cuenta
                        Button(
                            onClick = {
                                val cleanInput = usernameOrEmail.trim()
                                if (cleanInput.isBlank() || password.isBlank()) {
                                    errorMessage = "Por favor completa el usuario y la contraseña"
                                } else if (cleanInput.length < 3) {
                                    errorMessage = "El usuario o correo debe tener al menos 3 caracteres"
                                } else if (password.length < 3) {
                                    errorMessage = "La contraseña debe tener al menos 3 caracteres"
                                } else {
                                    isLoading = true
                                    onNormalLogin(cleanInput, password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Iniciar Sesión",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Divisor "o bien"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF2C2C2E)
                            )
                            Text(
                                "  o bien  ",
                                color = Color(0xFF8E8E93),
                                fontSize = 11.sp
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF2C2C2E)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botón Secundario: Acceso Directo con Meta Graph API oficial
                        OutlinedButton(
                            onClick = { onInstagramLogin() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))
                                )
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkColors.SurfaceVariant.copy(alpha = 0.85f),
                                contentColor = Color.White
                            ),
                            enabled = !isLoading
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Conectar con Meta Graph API",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "10° Cuatrimestre • Desarrollo Móvil Multiplataforma",
                    fontSize = 11.sp,
                    color = Color(0xFF8E8E93)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (InstagramConfig.useMockData) "Modo Demostración" else "Meta Graph API • Conexión Activa",
                    fontSize = 10.sp,
                    color = Color(0xFFA1A1A6)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
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
