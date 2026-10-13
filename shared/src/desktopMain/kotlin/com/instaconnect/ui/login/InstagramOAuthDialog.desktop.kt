package com.instaconnect.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.instaconnect.theme.DarkColors
import java.awt.Desktop
import java.net.URI

@Composable
actual fun InstagramOAuthDialog(
    authUrl: String,
    redirectUri: String,
    onCodeReceived: (code: String) -> Unit,
    onError: (errorDescription: String) -> Unit,
    onDismiss: () -> Unit
) {
    var manualCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(authUrl))
            }
        } catch (_: Exception) {}
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.width(420.dp).padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Autenticación con Instagram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Se ha abierto el navegador para iniciar sesión con Instagram. Si la redirección incluye un código, ingrésalo aquí:",
                    color = Color(0xFFA1A1A6),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = manualCode,
                    onValueChange = { manualCode = it },
                    label = { Text("Código de autorización") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val input = manualCode.trim()
                            val extractedCode = when {
                                input.contains("code=") -> {
                                    input.substringAfter("code=").substringBefore("&").removeSuffix("#_")
                                }
                                else -> input.removeSuffix("#_")
                            }
                            if (extractedCode.isNotBlank()) {
                                onCodeReceived(extractedCode)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Continuar", color = Color.Black)
                    }
                }
            }
        }
    }
}
