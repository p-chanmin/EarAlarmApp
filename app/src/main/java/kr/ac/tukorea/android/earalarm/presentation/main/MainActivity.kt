package kr.ac.tukorea.android.earalarm.presentation.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.databinding.ActivityMainBinding
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import kr.ac.tukorea.android.earalarm.presentation.main.output.MainUiEvent
import kr.ac.tukorea.android.earalarm.presentation.main.output.ViewType
import kr.ac.tukorea.android.earalarm.presentation.main.screen.AlarmScreen
import kr.ac.tukorea.android.earalarm.presentation.main.screen.MeasuringScreen
import kr.ac.tukorea.android.earalarm.presentation.main.viewmodel.MainViewModel
import kr.ac.tukorea.android.earalarm.presentation.notification.NotificationHelper
import kr.ac.tukorea.android.earalarm.presentation.service.AlarmPlayingService
import kr.ac.tukorea.android.earalarm.ui.theme.EarAlarmTheme
import kr.ac.tukorea.android.earalarm.utils.toPath
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmHelper: AlarmHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                it.toPath(this, mainViewModel.alarmUiState.value.alarmMedia)
                    ?.let { path -> mainViewModel.setAlarmSound(path) }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    this, getString(R.string.permission_notification_denied), Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationHelper.registerNotificationChannels()
        collectViewModelEvent()
        askNotificationPermission()
        loadInterstitialAd()

        setContent {
            EarAlarmTheme {
                val viewType by mainViewModel.viewType.collectAsState()

                when (viewType) {
                    ViewType.ALARM -> {
                        AlarmScreen(
                            alarmUiState = mainViewModel.output.alarmUiState.collectAsState(),
                            input = mainViewModel.input,
                            confirmExactAlarmPermission = { confirmExactAlarmPermission() },
                            confirmNotificationPermission = { confirmNotificationPermission() },
                            audioFilePickerLauncher = { filePickerLauncher.launch("audio/*") }
                        )
                    }

                    ViewType.MEASURING -> {
                        MeasuringScreen(
                            measuringUiState = mainViewModel.output.measuringUiState.collectAsState(),
                            input = mainViewModel.input
                        )
                    }
                }
            }
        }
    }

    private fun collectViewModelEvent() {
        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.event.collectLatest { event ->
                    when (event) {
                        MainUiEvent.SetAlarm -> loadInterstitialAd()
                        MainUiEvent.DismissAlarm -> dismissAlarm()
                    }
                }
            }
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(this,
            getString(R.string.admob_alarm_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun dismissAlarm() {
        this.stopService(
            Intent(this, AlarmPlayingService::class.java)
        )
        mInterstitialAd?.show(this)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                mainViewModel.showDeniedNotificationDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun confirmExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${this.packageName}"),
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            this.startActivity(intent)
        }
    }

    private fun confirmNotificationPermission() {
        val intent = Intent(
            Settings.ACTION_APP_NOTIFICATION_SETTINGS,
        ).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, this@MainActivity.packageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        this.startActivity(intent)
    }
}