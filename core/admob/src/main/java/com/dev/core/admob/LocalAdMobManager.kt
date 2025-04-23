package com.dev.core.admob

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAdMobManager = staticCompositionLocalOf<AdMobManager> {
    error("No AdMobManager provided")
}