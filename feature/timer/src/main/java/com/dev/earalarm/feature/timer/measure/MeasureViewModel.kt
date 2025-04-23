package com.dev.earalarm.feature.timer.measure

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.alarm.EarAlarmManager
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.timer.model.MeasureUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MeasureViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val alarmManager: EarAlarmManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private var measuringJob: Job? = null

    private val _measureUiState = MutableStateFlow(MeasureUiState())
    val measureUiState = _measureUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MeasureUiState()
    )

    init {
        viewModelScope.launch {
            timerRepository.alarmInfo.collectLatest { info ->
                if (info != null) {
                    _measureUiState.update {
                        val startTime = ZonedDateTime.parse(info.startTime)
                        val endTime = ZonedDateTime.parse(info.endTime)
                        it.copy(
                            minute = info.minute,
                            startTime = startTime,
                            endTime = endTime,
                            endTimeString = endTime.withZoneSameInstant(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("a hh:mm")),
                            progress = getTimerProgressFromNow(startTime, endTime),
                            leftTime = endTime.getRemainingTimeFromNow()
                        )
                    }
                    measuringTimer()
                } else {
                    measuringJob?.cancel()
                }
            }
        }
    }

    private fun measuringTimer() {
        measuringJob?.cancel()
        measuringJob = viewModelScope.launch {
            while (_measureUiState.value.progress < 1) {
                _measureUiState.update {
                    it.copy(
                        progress = getTimerProgressFromNow(it.startTime, it.endTime),
                        leftTime = it.endTime.getRemainingTimeFromNow()
                    )
                }
                delay(16)
            }
        }
    }

    fun dismissTimerAlarm() {
        viewModelScope.launch {
            alarmManager.cancelTimerAlarm()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun ZonedDateTime.getRemainingTimeFromNow(): String {
        val currentTime = ZonedDateTime.now()

        val duration = Duration.between(currentTime, this).seconds

        return if (duration < 0) "00:00:00" else String.format(
            "%02d:%02d:%02d",
            duration / 3600,
            (duration % 3600) / 60,
            duration % 60
        )
    }

    private fun getTimerProgressFromNow(startTime: ZonedDateTime, endTime: ZonedDateTime): Float {
        val totalTime = Duration.between(startTime, endTime).toMillis().toDouble()
        val measuredTime = Duration.between(startTime, ZonedDateTime.now()).toMillis().toDouble()
        val percent = (measuredTime / totalTime)
        return if (percent < 0) 0f else if (percent > 1) 1f else percent.toFloat()
    }

    override fun onCleared() {
        super.onCleared()
        println("MeasureViewModel Cleared $this")
    }
}