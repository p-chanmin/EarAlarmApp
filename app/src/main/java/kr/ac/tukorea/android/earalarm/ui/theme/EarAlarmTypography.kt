package kr.ac.tukorea.android.earalarm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kr.ac.tukorea.android.earalarm.R

private val nanumbarunBold = FontFamily(
    Font(R.font.nanumbarun_gothic_bold, FontWeight.Bold)
)
private val nanumbarunRegular = FontFamily(
    Font(R.font.nanumbarun_gothic_regular, FontWeight.Normal)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = nanumbarunBold,
        fontSize = 30.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = nanumbarunBold,
        fontSize = 25.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = nanumbarunBold,
        fontSize = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = nanumbarunRegular,
        fontSize = 30.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = nanumbarunRegular,
        fontSize = 25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = nanumbarunRegular,
        fontSize = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = nanumbarunBold,
        fontSize = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = nanumbarunBold,
        fontSize = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = nanumbarunRegular,
        fontSize = 14.sp,
    )
)