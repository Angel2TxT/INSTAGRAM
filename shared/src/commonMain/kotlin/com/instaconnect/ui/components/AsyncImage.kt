package com.instaconnect.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.instaconnect.theme.InstagramColors
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

expect fun ByteArray.toImageBitmap(): ImageBitmap?

private val imageCache = mutableMapOf<String, ImageBitmap>()
private val imageHttpClient by lazy { HttpClient() }

@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    loadingPlaceholder: @Composable (() -> Unit)? = null,
    errorPlaceholder: @Composable (() -> Unit)? = null
) {
    if (url.isBlank()) {
        errorPlaceholder?.invoke()
        return
    }

    var bitmap by remember(url) { mutableStateOf(imageCache[url]) }
    var isLoading by remember(url) { mutableStateOf(bitmap == null) }
    var isError by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        if (bitmap != null) {
            isLoading = false
            return@LaunchedEffect
        }

        try {
            val bytes = withContext(Dispatchers.Default) {
                if (url.startsWith("data:") || (url.length > 100 && !url.startsWith("http"))) {
                    val raw = if (url.contains("base64,")) url.substringAfter("base64,") else url
                    decodeBase64ToBytes(raw)
                } else {
                    val response: HttpResponse = imageHttpClient.get(url)
                    response.readRawBytes()
                }
            }
            val decoded = bytes.toImageBitmap()
            if (decoded != null) {
                imageCache[url] = decoded
                bitmap = decoded
            } else {
                isError = true
            }
        } catch (_: Throwable) {
            isError = true
        } finally {
            isLoading = false
        }
    }

    Crossfade(targetState = Triple(isLoading, isError, bitmap)) { (loading, error, img) ->
        when {
            img != null -> {
                Image(
                    bitmap = img,
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale
                )
            }
            loading -> {
                if (loadingPlaceholder != null) {
                    loadingPlaceholder()
                } else {
                    Box(modifier = modifier, contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = InstagramColors.Pink,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            else -> {
                errorPlaceholder?.invoke()
            }
        }
    }
}

private fun decodeBase64ToBytes(str: String): ByteArray {
    val table = IntArray(128) { -1 }
    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    for (i in alphabet.indices) {
        table[alphabet[i].code] = i
    }
    val clean = StringBuilder()
    for (i in 0 until str.length) {
        val c = str[i]
        if (!c.isWhitespace()) {
            clean.append(c)
        }
    }
    val n = clean.length
    val out = ArrayList<Byte>(n * 3 / 4)
    var buf = 0
    var bits = 0
    for (i in 0 until n) {
        val c = clean[i]
        if (c == '=') break
        val v = if (c.code < 128) table[c.code] else -1
        if (v < 0) continue
        buf = (buf shl 6) or v
        bits += 6
        if (bits >= 8) {
            bits -= 8
            out.add(((buf shr bits) and 0xFF).toByte())
        }
    }
    return out.toByteArray()
}
