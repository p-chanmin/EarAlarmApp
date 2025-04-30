package com.dev.firebase

import androidx.compose.runtime.staticCompositionLocalOf

val LocalFirebaseManager = staticCompositionLocalOf<FirebaseManager> {
    FakeFirebaseManager()
}