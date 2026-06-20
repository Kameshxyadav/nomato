package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NomatoRed,
    secondary = NomatoGold,
    tertiary = NomatoVegGreen,
    background = Slate900,
    surface = Slate700,
    onPrimary = CardWhite,
    onSecondary = Slate900,
    onTertiary = CardWhite,
    onBackground = CardWhite,
    onSurface = CardWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NomatoRed,
    secondary = NomatoGold,
    tertiary = NomatoVegGreen,
    background = ScreenBg,
    surface = CardWhite,
    onPrimary = CardWhite,
    onSecondary = Slate900,
    onTertiary = CardWhite,
    onBackground = Slate900,
    onSurface = Slate900
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Disable dynamic colors to enforce branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
