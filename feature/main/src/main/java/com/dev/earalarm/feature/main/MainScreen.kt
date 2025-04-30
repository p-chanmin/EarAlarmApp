package com.dev.earalarm.feature.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dev.core.admob.LocalAdMobManager
import com.dev.core.admob.banner.BannersAds
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.firebase.LocalFirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val firebaseManager = LocalFirebaseManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val onShowErrorSnackBar: (throwable: Throwable?) -> Unit = { throwable ->
        coroutineScope.launch {
            val unknownErrorMessage = context.getString(R.string.feature_main_error_message_unknown)

            firebaseManager.firebaseAnalytics.logEvent(FA.Event.ERROR_DISPLAYED) {
                param(FA.Param.Key.MESSAGE, throwable?.message ?: unknownErrorMessage)
            }
            snackBarHostState.showSnackbar(throwable?.message ?: unknownErrorMessage)
        }
    }

    MainScreenContent(
        navigator = navigator,
        onShowErrorSnackBar = onShowErrorSnackBar,
        snackBarHostState = snackBarHostState,
    )
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    navigator: MainNavigator,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    val adMobManager = LocalAdMobManager.current

    Scaffold(
        modifier = modifier,
        content = { paddingValues ->
            MainNavHost(
                navigator = navigator,
                paddingValues = paddingValues,
                onShowErrorSnackBar = onShowErrorSnackBar,
            )
        },
        bottomBar = {
            BannersAds(
                modifier = Modifier.fillMaxWidth(),
                adRequest = adMobManager.adRequest
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    modifier = Modifier
                        .padding(Paddings.small)
                        .padding(bottom = Paddings.large)
                ) {
                    Text(
                        text = data.visuals.message,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    )
}