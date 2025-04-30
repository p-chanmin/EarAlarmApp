package com.dev.earalarm.core.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.dev.earalarm.core.data.TimerRepository
import com.dev.firebase.FirebaseManager
import com.dev.firebase.model.FA
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EarAlarmPlayingService : Service() {

    private val binder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder = binder
    inner class LocalBinder : Binder()

    @Inject
    lateinit var timerRepository: TimerRepository

    @Inject
    lateinit var notificationHelper: EarAlarmNotificationManager

    @Inject
    lateinit var firebaseManager: FirebaseManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var mediaVolumeBeforeAlarm: Int = 0

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        .build()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        when (intent.action) {
            INTENT_ACTION_SERVICE_TIMER_ALARM_ON -> {
                serviceScope.launch { playAlarm() }
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun playAlarm() {
        notificationHelper.registerNotificationChannels()

        val volume = timerRepository.alarmVolume.first()
        val vibrate = timerRepository.vibrate.first()
        val mediaFile = timerRepository.media.first()

        startForeground(
            FOREGROUND_ID,
            notificationHelper.createForegroundNotificationBuilder()
        )

        mediaVolumeBeforeAlarm = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        if (!mediaPlayer.isPlaying) {
            if (mediaFile == null || !mediaFile.exists()) {
                timerRepository.removeMediaPath()
                mediaPlayer.release()
                mediaPlayer = MediaPlayer.create(
                    this@EarAlarmPlayingService,
                    R.raw.samplesound
                )
            } else {
                mediaPlayer.setDataSource(mediaFile.absolutePath)
                mediaPlayer.prepare()
            }

            audioManager.requestAudioFocus(focusRequest)

            audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume * 0.01f).toInt(),
                0
            )
            firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_RING) {
                param(FA.Param.Key.VOLUME, volume.toLong())
                param(FA.Param.Key.VIBRATE, vibrate.toString())
                param(
                    FA.Param.Key.MEDIA,
                    mediaFile?.let { FA.Param.Value.CUSTOM } ?: FA.Param.Value.DEFAULT
                )
            }

            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }
        if (vibrate) {
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 1000, 1000), 0)
            vibrator.vibrate(effect)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseManager.firebaseAnalytics.logEvent(FA.Event.ALARM_RING_STOP) {}
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()

                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    mediaVolumeBeforeAlarm,
                    0
                )
                delay(VOLUME_CHANGED_DELAY)
            }
            vibrator.cancel()

            audioManager.abandonAudioFocusRequest(focusRequest)
            timerRepository.removeTimerAlarmInfo()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        serviceScope.cancel()
    }

    companion object {
        const val INTENT_ACTION_SERVICE_TIMER_ALARM_ON = "intentActionServiceTimerAlarmOn"
        const val FOREGROUND_ID = 1
        const val VOLUME_CHANGED_DELAY = 100L
    }
}