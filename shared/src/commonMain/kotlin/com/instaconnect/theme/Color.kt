package com.instaconnect.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta monocromática minimalista estilo Apple (Black & White / Titanium).
 * Diseño ultra limpio, elegante y sin colores estridentes.
 */
object AppleColors {
    val PureBlack = Color(0xFF000000)
    val DarkGraphite = Color(0xFF0D0D0E)
    val CardBackground = Color(0xFF161618)
    val ElevatedSurface = Color(0xFF1C1C1E)
    val SubtleBorder = Color(0xFF2C2C2E)
    val LightBorder = Color(0xFF38383A)
    val MutedText = Color(0xFF8E8E93)
    val SecondaryText = Color(0xFFA1A1A6)
    val PrimaryText = Color(0xFFFFFFFF)
    val TitaniumSilver = Color(0xFFD1D1D6)
    val White = Color(0xFFFFFFFF)
}

/**
 * Adaptador de compatibilidad de Instagram hacia la paleta Apple monocromo.
 */
object InstagramColors {
    // Escala monocromática sobria
    val Purple = Color(0xFFFFFFFF)
    val Indigo = Color(0xFFF2F2F7)
    val DeepPurple = Color(0xFF2C2C2E)
    val Pink = Color(0xFFFFFFFF)
    val RedPink = Color(0xFFE5E5EA)
    val Red = Color(0xFFFF453A) // Acento de error sutil iOS
    val Orange = Color(0xFFE5E5EA)
    val Amber = Color(0xFF8E8E93)
    val Yellow = Color(0xFFD1D1D6)
    val LightYellow = Color(0xFFF2F2F7)

    // Gradiente monocromático Apple estilo titanio espacial
    val GradientColors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFE5E5EA),
        Color(0xFFD1D1D6),
        Color(0xFF8E8E93)
    )

    // Avatares elegantes en escala de grises / titanio
    val AvatarColors = listOf(
        listOf(Color(0xFF2C2C2E), Color(0xFF3A3A3C)),
        listOf(Color(0xFF1C1C1E), Color(0xFF2C2C2E)),
        listOf(Color(0xFF3A3A3C), Color(0xFF48484A)),
        listOf(Color(0xFF242426), Color(0xFF323236))
    )
}

/**
 * Colores para el modo oscuro OLED estilo Apple (iOS Dark Theme).
 */
object DarkColors {
    val Background = Color(0xFF000000)       // OLED Black puro de Apple
    val Surface = Color(0xFF121214)          // Fondo secundario tipo iOS
    val SurfaceVariant = Color(0xFF1C1C1E)   // Componentes tipo tarjeta iOS
    val SurfaceElevated = Color(0xFF2C2C2E)  // Modales y botones elevados
    val OnBackground = Color(0xFFFFFFFF)     // Texto principal blanco
    val OnSurface = Color(0xFFF2F2F7)        // Texto secundario
    val OnSurfaceVariant = Color(0xFF8E8E93) // Subtítulos y etiquetas gris Apple
    val Outline = Color(0xFF2C2C2E)          // Bordes ultra finos tipo separator
    val CardGlow = Color.White.copy(alpha = 0.03f)
}
