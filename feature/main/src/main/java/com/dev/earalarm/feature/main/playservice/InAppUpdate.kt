package com.dev.earalarm.feature.main.playservice

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.earalarm.feature.main.R
import com.dev.firebase.LocalFirebaseManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.ZonedDateTime

private const val MIN_VERSION_DIFF_FOR_IMMEDIATE_UPDATE = 5L

@Composable
internal fun InAppUpdate(
    snackBarHostState: SnackbarHostState,
    rejectFlexibleUpdateDate: ZonedDateTime?,
    setRejectFlexibleUpdateDate: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firebaseManager = LocalFirebaseManager.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val lifecycleState by lifecycle.currentStateFlow.collectAsStateWithLifecycle()

    val appUpdateManager: AppUpdateManager = remember { AppUpdateManagerFactory.create(context) }

    val appUpdateImmediateResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        val activity = context as? Activity
        if (result.resultCode != Activity.RESULT_OK) {
            activity?.finish()
        }
    }

    val appUpdateFlexibleResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            setRejectFlexibleUpdateDate()
        }
    }

    val installStateUpdatedListener = remember {
        InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                scope.launch {
                    showFlexibleUpdateSnackBar(
                        snackBarHostState,
                        appUpdateManager,
                        context.getString(R.string.feature_main_update_complete),
                        context.getString(R.string.feature_main_update_install)
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        appUpdateManager.registerListener(installStateUpdatedListener)
        onDispose {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    LaunchedEffect(rejectFlexibleUpdateDate) {
        try {
            val now = ZonedDateTime.now(ZoneOffset.UTC)
            val versionCode = getAppVersionCode(context)
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                val availableImmediate =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                            && appUpdateInfo.availableVersionCode()
                        .toLong() - versionCode >= MIN_VERSION_DIFF_FOR_IMMEDIATE_UPDATE

                val availableFlexible =
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                            && rejectFlexibleUpdateDate?.plusDays(1)?.isBefore(now) ?: true

                when {
                    availableImmediate -> {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateImmediateResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        )
                    }

                    availableFlexible -> {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateFlexibleResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        )
                    }
                }
            }
        } catch (e: Exception) {
            firebaseManager.reportNonFatalError(e)
        }
    }

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            try {
                val versionCode = getAppVersionCode(context)
                val appUpdateInfoTask = appUpdateManager.appUpdateInfo

                appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                        && appUpdateInfo.availableVersionCode()
                            .toLong() - versionCode >= MIN_VERSION_DIFF_FOR_IMMEDIATE_UPDATE
                    ) {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            appUpdateImmediateResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        )
                    } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        scope.launch {
                            showFlexibleUpdateSnackBar(
                                snackBarHostState,
                                appUpdateManager,
                                context.getString(R.string.feature_main_update_complete),
                                context.getString(R.string.feature_main_update_install)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                firebaseManager.reportNonFatalError(e)
            }
        }
    }
}

private suspend fun showFlexibleUpdateSnackBar(
    snackBarHostState: SnackbarHostState,
    appUpdateManager: AppUpdateManager,
    message: String,
    actionLabel: String,
) {
    val snackBarResult = snackBarHostState.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = SnackbarDuration.Indefinite,
    )

    if (snackBarResult == SnackbarResult.ActionPerformed) {
        appUpdateManager.completeUpdate()
    }
}

private fun getAppVersionCode(context: Context): Long {
    val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toLong()
    }
}