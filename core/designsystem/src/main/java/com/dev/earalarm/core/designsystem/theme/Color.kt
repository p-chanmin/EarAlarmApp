package com.dev.earalarm.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// background
val Ivory = Color(0xFFFFFCEE)
val Night = Color(0xFF111315)

// primary
val BlackOlive = Color(0xFF403F3B)
val Jet = Color(0xFF282828)

val EerieBlack = Color(0xFF1B1C1F)
val DimGray = Color(0xFF676767)
val White = Color(0xFFFFFFFF)

val LightGreen = Color(0xFF43AA8B)
val Red = Color(0xFFFF5B5B)
val Silver = Color(0xFFBFBDB2)

data class EarAlarmColorScheme(
    val primaryButton: Color = BlackOlive,
    val primaryButtonDisabled: Color = DimGray,
    val textPrimary: Color = BlackOlive,
    val textSecondary: Color = BlackOlive,
    val textInverted: Color = White,
    val active: Color = LightGreen,
    val inactive: Color = Silver,
    val warning: Color = Red,
)

object EarAlarmMaterialTheme {
    val colorScheme: EarAlarmColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalEarAlarmColorScheme.current
}

val LocalEarAlarmColorScheme = staticCompositionLocalOf {
    EarAlarmColorScheme()
}