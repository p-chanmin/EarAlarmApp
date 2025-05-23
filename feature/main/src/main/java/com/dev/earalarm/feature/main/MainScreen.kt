package com.dev.earalarm.feature.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.core.admob.LocalAdMobManager
import com.dev.core.admob.banner.BannersAds
import com.dev.earalarm.core.designsystem.theme.EarAlarmMaterialTheme
import com.dev.earalarm.core.designsystem.theme.Paddings
import com.dev.earalarm.feature.main.playservice.InAppReview
import com.dev.earalarm.feature.main.playservice.InAppUpdate
import com.dev.firebase.LocalFirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.launch

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.mainUiState.collectAsStateWithLifecycle()
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

    LaunchedEffect(Unit) {
        mainViewModel.errorFlow.collect { throwable ->
            onShowErrorSnackBar(throwable)
        }
    }

    MainScreenContent(
        mainUiState = mainUiState,
        navigator = navigator,
        onShowErrorSnackBar = onShowErrorSnackBar,
        snackBarHostState = snackBarHostState,
        setLastReviewDate = mainViewModel::setLastReviewDate,
        setRejectFlexibleUpdateDate = mainViewModel::setRejectFlexibleUpdateDate,
    )
}

@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    navigator: MainNavigator,
    onShowErrorSnackBar: (throwable: Throwable?) -> Unit,
    snackBarHostState: SnackbarHostState,
    setLastReviewDate: () -> Unit,
    setRejectFlexibleUpdateDate: () -> Unit,
) {
    val adMobManager = LocalAdMobManager.current
    val flexibleSnackBarHostState = remember { SnackbarHostState() }

    InAppReview(
        lastReviewDate = mainUiState.lastReviewDate,
        setLastReviewDate = setLastReviewDate
    )

    InAppUpdate(
        snackBarHostState = flexibleSnackBarHostState,
        rejectFlexibleUpdateDate = mainUiState.rejectFlexibleUpdateDate,
        setRejectFlexibleUpdateDate = setRejectFlexibleUpdateDate
    )

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

    SnackbarHost(
        hostState = flexibleSnackBarHostState,
        modifier = Modifier.systemBarsPadding(),
    ) { data ->
        Snackbar(
            modifier = Modifier
                .padding(Paddings.small)
                .padding(top = Paddings.medium),
            action = {
                data.visuals.actionLabel?.let {
                    Text(
                        modifier = Modifier
                            .padding(end = Paddings.small)
                            .clickable { data.performAction() },
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = EarAlarmMaterialTheme.colorScheme.active
                    )
                }
            }
        ) {
            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}