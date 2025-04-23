package com.dev.earalarm.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dev.earalarm.core.model.TimerAlarmInfo
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class TimerRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson,
) {

    val alarmVolume: Flow<Int> = dataStore.data.map {
        it[ALARM_VOLUME] ?: DEFAULT_VOLUME_SIZE
    }.distinctUntilChanged()

    val mediaPath: Flow<File?> = dataStore.data.map {
        it[MEDIA]?.let { path -> File(path) }
    }.distinctUntilChanged()

    val userSettingVolume: Flow<Int> = dataStore.data.map {
        it[USER_SETTING_VOLUME] ?: DEFAULT_VOLUME_SIZE
    }.distinctUntilChanged()

    val alarmInfo: Flow<TimerAlarmInfo?> = dataStore.data.map {
        gson.fromJson(it[TIMER_ALARM_INFO], TimerAlarmInfo::class.java)
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

    suspend fun removeMediaPath() {
        dataStore.edit {
            it.remove(MEDIA)
        }
    }

    suspend fun setUserSettingVolume(volume: Int) {
        dataStore.edit {
            it[USER_SETTING_VOLUME] = volume
        }
    }

    suspend fun setTimerAlarmInfo(timerAlarmInfo: TimerAlarmInfo) {
        dataStore.edit {
            it[TIMER_ALARM_INFO] = gson.toJson(timerAlarmInfo, TimerAlarmInfo::class.java)
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
        val TIMER_ALARM_INFO = stringPreferencesKey("timerAlarmInfo")
        val USER_SETTING_VOLUME = intPreferencesKey("userSettingVolume")

        const val DEFAULT_VOLUME_SIZE = 80
    }
}