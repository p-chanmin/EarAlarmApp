package com.dev.earalarm.feature.main.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TMainActivity : ComponentActivity() {

//    @Inject
//    lateinit var alarmHelper: AlarmHelper
//
//    @Inject
//    lateinit var notificationHelper: NotificationHelper
//
//    private val mainViewModel: MainViewModel by viewModels()
//    private var mInterstitialAd: InterstitialAd? = null
//
//    private val filePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            uri?.let {
//                it.toPath(this, mainViewModel.alarmUiState.value.alarmMedia)
//                    ?.let { path -> mainViewModel.setAlarmSound(path) }
//            }
//        }
//
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//            if (!isGranted) {
//                Toast.makeText(
//                    this, getString(R.string.permission_notification_denied), Toast.LENGTH_SHORT
//                ).show()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        notificationHelper.registerNotificationChannels()
//        collectViewModelEvent()
//        askNotificationPermission()
//        loadInterstitialAd()
//
//        setContent {
//            EarAlarmTheme {
//                val viewType by mainViewModel.viewType.collectAsState()
//
//                when (viewType) {
//                    ViewType.ALARM -> {
//                        AlarmScreen(
//                            alarmUiState = mainViewModel.output.alarmUiState.collectAsState(),
//                            input = mainViewModel.input,
//                            confirmExactAlarmPermission = { confirmExactAlarmPermission() },
//                            confirmNotificationPermission = { confirmNotificationPermission() },
//                            audioFilePickerLauncher = { filePickerLauncher.launch("audio/*") }
//                        )
//                    }
//
//                    ViewType.MEASURING -> {
//                        MeasuringScreen(
//                            measuringUiState = mainViewModel.output.measuringUiState.collectAsState(),
//                            input = mainViewModel.input
//                        )
//                    }
//                }
//            }
//        }
    }
//
//    private fun collectViewModelEvent() {
//        this.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mainViewModel.event.collectLatest { event ->
//                    when (event) {
//                        MainUiEvent.SetAlarm -> loadInterstitialAd()
//                        MainUiEvent.DismissAlarm -> dismissAlarm()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun loadInterstitialAd() {
//        InterstitialAd.load(this,
//            getString(R.string.admob_alarm_id),
//            AdRequest.Builder().build(),
//            object : InterstitialAdLoadCallback() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    mInterstitialAd = null
//                }
//
//                override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                    mInterstitialAd = interstitialAd
//                }
//            })
//    }
//
//    private fun dismissAlarm() {
//        this.stopService(
//            Intent(this, AlarmPlayingService::class.java)
//        )
//        mInterstitialAd?.show(this)
//    }
//
//    private fun askNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                mainViewModel.showDeniedNotificationDialog()
//            } else {
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    private fun confirmExactAlarmPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val intent = Intent(
//                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
//                Uri.parse("package:${this.packageName}"),
//            ).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            }
//            this.startActivity(intent)
//        }
//    }
//
//    private fun confirmNotificationPermission() {
//        val intent = Intent(
//            Settings.ACTION_APP_NOTIFICATION_SETTINGS,
//        ).apply {
//            putExtra(Settings.EXTRA_APP_PACKAGE, this@MainActivity.packageName)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        this.startActivity(intent)
//    }
}