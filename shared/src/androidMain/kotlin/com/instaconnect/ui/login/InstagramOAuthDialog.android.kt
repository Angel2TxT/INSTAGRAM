package com.instaconnect.ui.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.instaconnect.theme.DarkColors

/**
 * Implementación de Android del diálogo OAuth con WebView nativo.
 */
@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun InstagramOAuthDialog(
    authUrl: String,
    redirectUri: String,
    onCodeReceived: (code: String) -> Unit,
    onError: (errorDescription: String) -> Unit,
    onDismiss: () -> Unit
) {
    var isPageLoading by remember { mutableStateOf(true) }
    var currentTitle by remember { mutableStateOf("Conectando con Instagram...") }
    var hasHandledCode by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 28.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkColors.Surface)
            ) {
                // Barra superior de navegación / seguridad
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = "Conexión segura",
                                tint = Color(0xFF34C759),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "api.instagram.com",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = currentTitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF8E8E93)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Cancelar",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkColors.SurfaceVariant
                    )
                )

                // Indicador de carga
                if (isPageLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        trackColor = Color(0xFF2C2C2E)
                    )
                }

                // WebView de Instagram
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                javaScriptCanOpenWindowsAutomatically = true
                                userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isPageLoading = true
                                    url?.let { checkUrlForCode(it) }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isPageLoading = false
                                    currentTitle = "Inicio de sesión oficial"
                                    url?.let { checkUrlForCode(it) }
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    return checkUrlForCode(url)
                                }

                                private fun checkUrlForCode(url: String): Boolean {
                                    if (hasHandledCode) return true

                                    val uri = Uri.parse(url)
                                    val cleanRedirect = redirectUri.trimEnd('/')

                                    // Si Instagram pasa por su link shim (l.instagram.com/?u=...)
                                    val effectiveUrl = if (url.contains("l.instagram.com") && uri.getQueryParameter("u") != null) {
                                        uri.getQueryParameter("u") ?: url
                                    } else {
                                        url
                                    }
                                    val effectiveUri = Uri.parse(effectiveUrl)

                                    val isRedirect = effectiveUrl.startsWith(cleanRedirect, ignoreCase = true) ||
                                            effectiveUrl.contains("localhost", ignoreCase = true) ||
                                            effectiveUri.getQueryParameter("code") != null

                                    if (isRedirect) {
                                        val code = effectiveUri.getQueryParameter("code")
                                        if (!code.isNullOrBlank()) {
                                            val cleanCode = code.removeSuffix("#_").removeSuffix("%23_").trim()
                                            hasHandledCode = true
                                            onCodeReceived(cleanCode)
                                            return true
                                        }

                                        val error = effectiveUri.getQueryParameter("error_description")
                                            ?: effectiveUri.getQueryParameter("error_reason")
                                            ?: effectiveUri.getQueryParameter("error")

                                        if (!error.isNullOrBlank()) {
                                            hasHandledCode = true
                                            onError(error)
                                            return true
                                        }
                                    }
                                    return false
                                }
                            }

                            loadUrl(authUrl)
                        }
                    }
                )
            }
        }
    }
}
