package com.dev.earalarm.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dev.core.admob.AdMobManager
import com.dev.core.admob.LocalAdMobManager
import com.dev.earalarm.core.designsystem.theme.EarAlarmTheme
import com.dev.firebase.FirebaseManager
import com.dev.firebase.LocalFirebaseManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var adMobManager: AdMobManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
        }

        enableEdgeToEdge()

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            EarAlarmTheme {
                CompositionLocalProvider(
                    LocalAdMobManager provides adMobManager,
                    LocalFirebaseManager provides firebaseManager
                ) {
                    MainScreen(
                        navigator = navigator,
                    )
                }
            }
        }
    }
}