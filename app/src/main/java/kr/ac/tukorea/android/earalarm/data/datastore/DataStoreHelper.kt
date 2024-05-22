package kr.ac.tukorea.android.earalarm.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kr.ac.tukorea.android.earalarm.data.model.TimerAlarmInfo
import java.io.File
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>, private val gson: Gson
) {

    val volume: Flow<Int> = dataStore.data.map {
        it[VOLUME] ?: DEFAULT_VOLUME_SIZE
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

    suspend fun storeVolume(volume: Int) {
        dataStore.edit {
            it[VOLUME] = volume
        }
    }

    suspend fun storeMediaPath(path: String) {
        dataStore.edit {
            it[MEDIA] = path
        }
    }

    suspend fun storeUserSettingVolume(volume: Int) {
        dataStore.edit {
            it[USER_SETTING_VOLUME] = volume
        }
    }

    suspend fun storeTimerAlarmInfo(timerAlarmInfo: TimerAlarmInfo) {
        dataStore.edit {
            it[TIMER_ALARM_INFO] = gson.toJson(timerAlarmInfo, TimerAlarmInfo::class.java)
        }
    }

    suspend fun deleteTimerAlarmInfo() {
        dataStore.edit {
            it.remove(TIMER_ALARM_INFO)
        }
    }

    companion object {
        val VOLUME = intPreferencesKey("volume")
        val MEDIA = stringPreferencesKey("media")
        val TIMER_ALARM_INFO = stringPreferencesKey("timerAlarmInfo")
        val USER_SETTING_VOLUME = intPreferencesKey("userSettingVolume")

        const val DEFAULT_VOLUME_SIZE = 80
    }
}