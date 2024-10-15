package kr.ac.tukorea.android.earalarm.presentation.service


import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.ac.tukorea.android.earalarm.R
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import kr.ac.tukorea.android.earalarm.presentation.notification.NotificationHelper
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AlarmPlayingService : Service() {

    private val binder: IBinder = LocalBinder()
    override fun onBind(intent: Intent): IBinder = binder
    inner class LocalBinder : Binder()

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    @Inject
    lateinit var notificationHelper: NotificationHelper

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

                    val volume = dataStoreHelper.volume.first() * 0.01f
                    val mediaFile = dataStoreHelper.mediaPath.first()

                    startForeground(
                        FOREGROUND_ID,
                        notificationHelper.createForegroundNotificationBuilder()
                    )

                    dataStoreHelper.storeUserSettingVolume(
                        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    )

                    if (!mediaPlayer.isPlaying) {
                        if (mediaFile == null || !mediaFile.exists()) {
                            dataStoreHelper.removeMediaPath()
                            mediaPlayer = MediaPlayer.create(
                                this@AlarmPlayingService,
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
                    dataStoreHelper.userSettingVolume.first(),
                    0
                )
                delay(VOLUME_CHANGED_DELAY)
            }

            audioManager.abandonAudioFocusRequest(focusRequest)
            dataStoreHelper.deleteTimerAlarmInfo()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    companion object {
        const val INTENT_ACTION_SERVICE_TIMER_ALARM_ON = "intentActionServiceTimerAlarmOn"
        const val FOREGROUND_ID = 1
        const val VOLUME_CHANGED_DELAY = 100L
    }
}