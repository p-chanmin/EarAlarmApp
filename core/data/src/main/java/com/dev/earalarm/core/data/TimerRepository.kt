package com.dev.earalarm.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dev.earalarm.core.model.TimerAlarmInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class TimerRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val alarmVolume: Flow<Int> = dataStore.data.map {
        it[ALARM_VOLUME] ?: DEFAULT_VOLUME_SIZE
    }.distinctUntilChanged()

    val media: Flow<File?> = dataStore.data.map {
        it[MEDIA]?.let { path -> File(path) }
    }.distinctUntilChanged()

    val vibrate: Flow<Boolean> = dataStore.data.map {
        it[VIBRATE] ?: true
    }.distinctUntilChanged()

    val alarmInfo: Flow<TimerAlarmInfo?> = dataStore.data.map {
        it[TIMER_ALARM_INFO]?.let {
            Json.decodeFromString<TimerAlarmInfo>(it)
        }
    }.distinctUntilChanged()

    suspend fun setAlarmVolume(volume: Int) {
        dataStore.edit {
            it[ALARM_VOLUME] = volume
        }
    }

    suspend fun setMediaPath(path: String) {
        dataStore.edit {
            it[MEDIA] = path
        }
    }

    suspend fun setVibrate(isOn: Boolean) {
        dataStore.edit {
            it[VIBRATE] = isOn
        }
    }

    suspend fun removeMediaPath() {
        dataStore.edit {
            it.remove(MEDIA)
        }
    }

    suspend fun setTimerAlarmInfo(timerAlarmInfo: TimerAlarmInfo) {
        dataStore.edit {
            it[TIMER_ALARM_INFO] = Json.encodeToString(timerAlarmInfo)
        }
    }

    suspend fun removeTimerAlarmInfo() {
        dataStore.edit {
            it.remove(TIMER_ALARM_INFO)
        }
    }

    companion object {
        val ALARM_VOLUME = intPreferencesKey("volume")
        val MEDIA = stringPreferencesKey("media")
        val VIBRATE = booleanPreferencesKey("vibrate")
        val TIMER_ALARM_INFO = stringPreferencesKey("timerAlarmInfo")

        const val DEFAULT_VOLUME_SIZE = 80
    }
}