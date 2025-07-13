package com.dev.earalarm.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BlackOlive,
    onPrimary = White,
    background = Ivory,
)

private val DarkColorScheme = darkColorScheme(
    primary = Jet,
    onPrimary = White,
    background = Night,
)

private val LightEarAlarmColorScheme = EarAlarmColorScheme(
    primaryButton = BlackOlive,
    primaryButtonDisabled = DimGray,
    textPrimary = BlackOlive,
    textSecondary = DimGray,
    textInverted = White,
    active = LightGreen,
    inactive = Silver,
    warning = Red,
)

private val DarkEarAlarmColorScheme = EarAlarmColorScheme(
    primaryButton = Jet,
    primaryButtonDisabled = EerieBlack,
    textPrimary = White,
    textSecondary = DimGray,
    textInverted = White,
    active = LightGreen,
    inactive = Silver,
    warning = Red,
)

@Composable
fun EarAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val earAlarmColorScheme = when {
        darkTheme -> DarkEarAlarmColorScheme
        else -> LightEarAlarmColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(LocalEarAlarmColorScheme provides earAlarmColorScheme) {
            Surface(content = content)
        }
    }
}