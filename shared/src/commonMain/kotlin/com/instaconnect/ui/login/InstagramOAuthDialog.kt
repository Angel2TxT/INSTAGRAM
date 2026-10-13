package com.instaconnect.ui.login

import androidx.compose.runtime.Composable

/**
 * Diálogo / Pantalla modal para realizar el flujo de autenticación OAuth 2.0
 * oficial con la página web de Instagram en un WebView nativo seguro.
 *
 * @param authUrl URL de autorización oficial de Instagram.
 * @param redirectUri URI de redirección configurada en Meta Developers para interceptar el código.
 * @param onCodeReceived Callback invocado cuando Instagram redirige con el código de autorización exitoso.
 * @param onError Callback invocado si el usuario cancela o Instagram devuelve un error.
 * @param onDismiss Callback para cerrar el diálogo si el usuario presiona cancelar.
 */
@Composable
expect fun InstagramOAuthDialog(
    authUrl: String,
    redirectUri: String,
    onCodeReceived: (code: String) -> Unit,
    onError: (errorDescription: String) -> Unit,
    onDismiss: () -> Unit
)
