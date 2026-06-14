package com.telogaspar.catbreed.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

data class AppColors(
    val paper: Color     = Color(0xFFEFE7D9),
    val card: Color      = Color(0xFFFAF5EC),
    val sunken: Color    = Color(0xFFE7DDCD),
    val ink: Color       = Color(0xFF221E17),
    val ink2: Color      = Color(0xFF6D6457),
    val ink3: Color      = Color(0xFF9C9282),
    val goldDeep: Color  = Color(0xFF9C7A23),
    val onGold: Color    = Color(0xFF412D00),
    val danger: Color    = Color(0xFFC44A43),
    val ghost: Color     = Color(0x28897A4A),
    val shadow: Color    = Color(0x1A3C301C),
    val goldStart: Color = Color(0xFFE9C176),
    val goldEnd: Color   = Color(0xFFC5A059),
) {
    val goldGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(goldStart, goldEnd),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        )
}

val LocalAppColors = staticCompositionLocalOf { AppColors() }

data class AppFonts(
    // Newsreader → serif, Manrope → sans-serif (swap in via Google Fonts for production)
    val serif: FontFamily = FontFamily.Serif,
    val sans: FontFamily  = FontFamily.Default,
)

val LocalAppFonts = staticCompositionLocalOf { AppFonts() }
