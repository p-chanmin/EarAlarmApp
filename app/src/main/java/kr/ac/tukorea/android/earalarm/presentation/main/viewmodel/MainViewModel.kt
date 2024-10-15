package kr.ac.tukorea.android.earalarm.presentation.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.ac.tukorea.android.earalarm.data.datastore.DataStoreHelper
import kr.ac.tukorea.android.earalarm.data.model.TimerAlarmInfo
import kr.ac.tukorea.android.earalarm.presentation.alarm.AlarmHelper
import kr.ac.tukorea.android.earalarm.presentation.main.input.IMainViewModelInputs
import kr.ac.tukorea.android.earalarm.presentation.main.output.AlarmUiState
import kr.ac.tukorea.android.earalarm.presentation.main.output.IMainViewModelOutputs
import kr.ac.tukorea.android.earalarm.presentation.main.output.MainUiEvent
import kr.ac.tukorea.android.earalarm.presentation.main.output.MeasuringUiState
import kr.ac.tukorea.android.earalarm.presentation.main.output.ViewType
import kr.ac.tukorea.android.earalarm.utils.getRemainingTimeFromNow
import kr.ac.tukorea.android.earalarm.utils.getTimerProgressFromNow
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
    private val alarmHelper: AlarmHelper,
) : ViewModel(), IMainViewModelOutputs, IMainViewModelInputs {

    val output: IMainViewModelOutputs = this
    val input: IMainViewModelInputs = this

    private val _viewType = MutableStateFlow(ViewType.ALARM)
    override val viewType = _viewType.asStateFlow()

    private val _alarmUiState = MutableStateFlow(AlarmUiState())
    override val alarmUiState = _alarmUiState.asStateFlow()

    private val _measuringUiState = MutableStateFlow(MeasuringUiState())
    override val measuringUiState = _measuringUiState.asStateFlow()

    private val _event = MutableSharedFlow<MainUiEvent>()
    override val event = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            dataStoreHelper.volume.collectLatest { volume ->
                _alarmUiState.update { it.copy(volume = volume) }
            }
        }

        viewModelScope.launch {
            dataStoreHelper.mediaPath.collectLatest { file ->
                _alarmUiState.update { it.copy(alarmMedia = file) }
            }
        }

        viewModelScope.launch {
            dataStoreHelper.alarmInfo.collectLatest { info ->
                if (info == null) {
                    _viewType.update { ViewType.ALARM }
                } else {
                    _measuringUiState.update {
                        val startTime = ZonedDateTime.parse(info.startTime)
                        val endTime = ZonedDateTime.parse(info.endTime)
                        it.copy(
                            isMeasuring = true,
                            minute = info.minute,
                            startTime = startTime,
                            endTime = endTime,
                            endTimeString = endTime.withZoneSameInstant(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("a hh:mm")),
                            progress = getTimerProgressFromNow(startTime, endTime),
                            leftTime = endTime.getRemainingTimeFromNow()
                        )
                    }
                    _viewType.update { ViewType.MEASURING }
                    measuringTimer()
                }
            }
        }
    }

    private fun measuringTimer() {
        viewModelScope.launch {
            while (_measuringUiState.value.isMeasuring) {
                _measuringUiState.update {
                    it.copy(
                        isMeasuring = it.progress < 100,
                        progress = getTimerProgressFromNow(it.startTime, it.endTime),
                        leftTime = it.endTime.getRemainingTimeFromNow()
                    )
                }
                delay(100)
            }
        }
    }

    override fun updateTimePicker(hour: Int, minute: Int) {
        _alarmUiState.update {
            it.copy(
                hour = hour,
                minute = minute,
                estimatedEndTime = getEstimatedEndTime(hour, minute),
                timerStartEnabled = hour != 0 || minute != 0
            )
        }
    }

    override fun updateTimePickerWithMinute(minute: Int) {
        val newMinute = (_alarmUiState.value.minute + minute) % 60
        val newHour = min(23, _alarmUiState.value.hour + (_alarmUiState.value.minute + minute) / 60)
        _alarmUiState.update {
            it.copy(
                hour = newHour,
                minute = newMinute,
                estimatedEndTime = getEstimatedEndTime(newHour, newMinute),
                timerStartEnabled = newHour != 0 || newMinute != 0
            )
        }
    }

    override fun resetTimePicker() {
        _alarmUiState.update {
            it.copy(
                hour = 0,
                minute = 0,
                estimatedEndTime = getEstimatedEndTime(0, 0),
                timerStartEnabled = false
            )
        }
    }

    override fun startTimerAlarm() {
        viewModelScope.launch {
            if (alarmHelper.checkScheduleExactAlarms()) {
                val minute = _alarmUiState.value.hour * 60 + _alarmUiState.value.minute
                val endTime = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minute.toLong())
//                val endTime = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(minute.toLong())

                alarmHelper.setTimerAlarm(
                    TimerAlarmInfo(
                        minute = minute,
                        startTime = ZonedDateTime.now(ZoneOffset.UTC).toString(),
                        endTime = endTime.toString()
                    )
                )
                _event.emit(MainUiEvent.SetAlarm)
            } else {
                _alarmUiState.update {
                    it.copy(deniedExactAlarmDialog = true)
                }
            }
        }
    }

    override fun setVolume(volume: Int) {
        viewModelScope.launch {
            dataStoreHelper.storeVolume(volume)
        }
    }

    override fun setAlarmSound(path: String) {
        viewModelScope.launch {
            dataStoreHelper.storeMediaPath(path)
        }
    }

    override fun dismissAlarm() {
        viewModelScope.launch {
            _alarmUiState.update {
                it.copy(
                    hour = 0,
                    minute = 0,
                    estimatedEndTime = getEstimatedEndTime(0, 0),
                    timerStartEnabled = false
                )
            }
            _event.emit(MainUiEvent.DismissAlarm)
            alarmHelper.cancelTimerAlarm()
        }
    }

    override fun showSettingDialog() {
        _alarmUiState.update {
            it.copy(alarmSettingDialog = true)
        }
    }

    override fun showDeniedNotificationDialog() {
        _alarmUiState.update {
            it.copy(deniedNotificationDialog = true)
        }
    }

    override fun dismissDialog() {
        _alarmUiState.update {
            it.copy(
                deniedExactAlarmDialog = false,
                deniedNotificationDialog = false,
                alarmSettingDialog = false
            )
        }
    }

    private fun getEstimatedEndTime(hour: Int, minute: Int): String {
        return ZonedDateTime.now(ZoneOffset.UTC).plusHours(hour.toLong())
            .plusMinutes(minute.toLong()).withZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("a hh:mm"))
    }
}