package com.dev.earalarm.core.alarm

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.dev.earalarm.core.data.TimerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    private lateinit var audioManager: AudioManager

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        )
        .build()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager

        when (intent.action) {
            INTENT_ACTION_SERVICE_TIMER_ALARM_ON -> {
                CoroutineScope(Dispatchers.IO).launch {

                    notificationHelper.registerNotificationChannels()

                    val volume = timerRepository.alarmVolume.first() * 0.01f
                    val mediaFile = timerRepository.mediaPath.first()

                    startForeground(
                        FOREGROUND_ID,
                        notificationHelper.createForegroundNotificationBuilder()
                    )

                    timerRepository.setUserSettingVolume(
                        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    )

                    if (!mediaPlayer.isPlaying) {
                        if (mediaFile == null || !mediaFile.exists()) {
                            timerRepository.removeMediaPath()
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
                            (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume).toInt(),
                            0
                        )

                        mediaPlayer.start()
                        mediaPlayer.isLooping = true
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()

                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    timerRepository.userSettingVolume.first(),
                    0
                )
                delay(VOLUME_CHANGED_DELAY)
            }

            audioManager.abandonAudioFocusRequest(focusRequest)
            timerRepository.removeTimerAlarmInfo()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    companion object {
        const val INTENT_ACTION_SERVICE_TIMER_ALARM_ON = "intentActionServiceTimerAlarmOn"
        const val FOREGROUND_ID = 1
        const val VOLUME_CHANGED_DELAY = 100L
    }
}