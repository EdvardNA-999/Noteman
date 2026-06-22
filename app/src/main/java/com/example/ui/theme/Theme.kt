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
    primary = SleekPrimaryDark,
    secondary = SleekSecondaryDark,
    tertiary = SleekTertiaryDark,
    background = SleekBackgroundDark,
    surface = SleekSurfaceDark,
    onPrimary = SleekBackgroundDark,
    onSecondary = SleekBackgroundDark,
    onTertiary = SleekBackgroundDark,
    onBackground = SleekOnSurfaceDark,
    onSurface = SleekOnSurfaceDark,
    surfaceVariant = SleekSurfaceVariantDark,
    onSurfaceVariant = SleekOnSurfaceVariantDark,
    outline = SleekOutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SleekPrimary,
    secondary = SleekSecondary,
    tertiary = SleekTertiary,
    background = SleekBackgroundLight,
    surface = SleekSurfaceLight,
    onPrimary = SleekBackgroundLight,
    onSecondary = SleekBackgroundLight,
    onTertiary = SleekBackgroundLight,
    onBackground = SleekOnSurfaceLight,
    onSurface = SleekOnSurfaceLight,
    surfaceVariant = SleekSurfaceVariantLight,
    onSurfaceVariant = SleekOnSurfaceVariantLight,
    outline = SleekOutlineLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // True to enable Material 3 dynamic color palettes if supported, false to force Cozy Paper
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
