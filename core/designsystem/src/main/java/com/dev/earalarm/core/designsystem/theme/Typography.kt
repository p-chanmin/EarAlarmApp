package com.dev.earalarm.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.dev.earalarm.core.designsystem.R

private val nanumbarun = FontFamily(
    Font(R.font.nanumbarun_gothic_regular, FontWeight.Normal),
    Font(R.font.nanumbarun_gothic_bold, FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 30.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 25.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 30.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = nanumbarun,
        fontSize = 14.sp,
    )
)