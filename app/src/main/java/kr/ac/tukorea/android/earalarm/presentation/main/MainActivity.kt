package kr.ac.tukorea.android.earalarm.presentation.main

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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
import kr.ac.tukorea.android.earalarm.databinding.DialogSettingBinding
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import kr.ac.tukorea.android.earalarm.presentation.main.model.MainUiEvent
import kr.ac.tukorea.android.earalarm.presentation.notification.NotificationHelper
import kr.ac.tukorea.android.earalarm.presentation.service.AlarmPlayingService
import kr.ac.tukorea.android.earalarm.utils.toPath
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
                it.toPath(this, mainViewModel.mainUiState.value.alarmMedia)
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel

        binding.timePicker.setIs24HourView(true)

        notificationHelper.registerNotificationChannels()

        collectViewModelEvent()
        loadInterstitialAd()
        askNotificationPermission()

        binding.btnSetting.setOnClickListener {
            val binding: DialogSettingBinding = DataBindingUtil.inflate<DialogSettingBinding?>(
                layoutInflater, R.layout.dialog_setting, null, false
            ).apply {
                viewModel = mainViewModel
                lifecycleOwner = this@MainActivity
                setmedia.setOnClickListener { filePickerLauncher.launch("audio/*") }
                volumeSeek.progress = mainViewModel.mainUiState.value.volume
            }

            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.setting_alarm_text))
                setIcon(R.mipmap.ic_earalarm_launcher)
                setView(binding.root)
                setPositiveButton(getString(R.string.setting_complete_text), null)
            }.show()
        }
    }

    private fun collectViewModelEvent() {
        this.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.event.collectLatest { event ->
                    when (event) {
                        MainUiEvent.SetAlarm -> loadInterstitialAd()
                        MainUiEvent.DismissAlarm -> dismissAlarm()
                        MainUiEvent.DeniedExactAlarm -> askExactAlarmPermission()
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
                permissionDialog(context = this,
                    title = R.string.permission_notification_title_request,
                    message = R.string.permission_notification_message_request,
                    positiveBtnText = R.string.permission_positive,
                    negativeBtnText = R.string.permission_negative,
                    positiveAction = {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    negativeAction = {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.permission_notification_denied),
                            Toast.LENGTH_SHORT
                        ).show()
                    }).show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun askExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionDialog(context = this,
                title = R.string.permission_exact_alarm_title_request,
                message = R.string.permission_exact_alarm_message_request,
                positiveBtnText = R.string.permission_positive,
                negativeBtnText = R.string.permission_negative,
                positiveAction = {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:${this.packageName}"),
                    )
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)
                },
                negativeAction = {
                    Toast.makeText(
                        this, getString(R.string.permission_exact_alarm_denied), Toast.LENGTH_SHORT
                    ).show()
                }).show()
        }
    }

    private fun permissionDialog(
        context: Context,
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes positiveBtnText: Int,
        @StringRes negativeBtnText: Int,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ): AlertDialog.Builder =
        AlertDialog.Builder(context).setTitle(getString(title)).setMessage(getString(message))
            .setPositiveButton(getString(positiveBtnText)) { _, _ ->
                positiveAction()
            }.setNegativeButton(getString(negativeBtnText)) { _, _ ->
                negativeAction()
            }
}