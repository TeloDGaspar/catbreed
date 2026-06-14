package com.telogaspar.catbreed.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.telogaspar.catbreed.core.theme.AppColors
import com.telogaspar.catbreed.core.theme.AppFonts
import com.telogaspar.catbreed.core.theme.LocalAppColors
import com.telogaspar.catbreed.core.theme.LocalAppFonts

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CatBreedsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalAppColors provides AppColors(
            paper     = HeritagePaper,
            card      = HeritageCard,
            sunken    = HeritageSunken,
            ink       = HeritageInk,
            ink2      = HeritageInk2,
            ink3      = HeritageInk3,
            goldDeep  = HeritageGoldDeep,
            onGold    = HeritageOnGold,
            danger    = HeritageDanger,
            ghost     = HeritageGhost,
            shadow    = HeritageShadow,
            goldStart = HeritageGold,
            goldEnd   = HeritageGoldEnd,
        ),
        LocalAppFonts provides AppFonts(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}