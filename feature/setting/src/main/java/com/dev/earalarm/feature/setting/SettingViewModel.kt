package com.dev.earalarm.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.setting.model.SettingUiState
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
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
): ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _settingUiState = MutableStateFlow(SettingUiState())
    val settingUiState = _settingUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        SettingUiState()
    )

    init {
        loadTimerSetting()
    }

    private fun loadTimerSetting() {
        combine(timerRepository.alarmVolume, timerRepository.mediaPath) { volume, media ->
            _settingUiState.update {
                it.copy(
                    volume = volume,
                    alarmMedia = media
                )
            }
        }.launchIn(viewModelScope)
    }

    fun setVolume(volume: Int) {
        viewModelScope.launch {
            timerRepository.setAlarmVolume(volume)
        }
    }

    fun setAlarmSound(path: String) {
        viewModelScope.launch {
            timerRepository.setMediaPath(path)
        }
    }
}