package com.instaconnect.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.instaconnect.theme.DarkColors

@Composable
actual fun InstagramOAuthDialog(
    authUrl: String,
    redirectUri: String,
    onCodeReceived: (code: String) -> Unit,
    onError: (errorDescription: String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkColors.Surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Autenticación iOS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("El flujo web de OAuth en iOS requiere WKWebView.", color = Color(0xFFA1A1A6), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(18.dp))
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("Cerrar", color = Color.Black)
                }
            }
        }
    }
}
