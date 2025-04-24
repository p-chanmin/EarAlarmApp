package com.dev.earalarm.feature.timer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.alarm.EarAlarmManager
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.core.model.TimerAlarmInfo
import com.dev.earalarm.feature.timer.model.HomeUiState
import com.dev.earalarm.feature.timer.model.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val earAlarmManager: EarAlarmManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState()
    )

    init {
        loadTimerSetting()
    }

    private fun loadTimerSetting() {
        combine(timerRepository.alarmVolume, timerRepository.media) { volume, media ->
            _homeUiState.update {
                it.copy(
                    volume = volume,
                    alarmMedia = media
                )
            }
        }.launchIn(viewModelScope)
    }

    fun updateNotificationPermissionState(permissionState: PermissionState, dialogState: Boolean) {
        viewModelScope.launch {
            _homeUiState.update {
                it.copy(
                    notificationPermissionState = permissionState,
                    deniedNotificationDialog = dialogState
                )
            }
        }
    }

    fun updateTimerAlarm(hour: Int, minute: Int) {
        viewModelScope.launch {
            _homeUiState.update {
                it.copy(
                    hour = hour,
                    minute = minute,
                    estimatedEndTime = getEstimatedEndTime(hour, minute)
                )
            }
        }
    }

    fun startTimerAlarm() {
        viewModelScope.launch {
            if (earAlarmManager.checkScheduleExactAlarms()) {
                val minute = _homeUiState.value.hour * 60 + _homeUiState.value.minute
//                val endTime = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minute.toLong())
                val endTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(minute.toLong())
                earAlarmManager.setTimerAlarm(
                    TimerAlarmInfo(
                        minute = minute,
                        startTime = ZonedDateTime.now(ZoneOffset.UTC).toString(),
                        endTime = endTime.toString()
                    )
                )
                println("알람 저장 완료.")
            } else {
                _homeUiState.update {
                    it.copy(
                        deniedExactAlarmDialog = true
                    )
                }
            }
        }
    }

    fun dismissDialog() {
        _homeUiState.update {
            it.copy(
                deniedNotificationDialog = false,
                deniedExactAlarmDialog = false,
            )
        }
    }

    private fun getEstimatedEndTime(hour: Int, minute: Int): String {
        return ZonedDateTime.now(ZoneOffset.UTC).plusHours(hour.toLong())
            .plusMinutes(minute.toLong()).withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("a hh:mm"))
    }

    override fun onCleared() {
        super.onCleared()
        println("HomeViewModel Cleared $this")
    }
}