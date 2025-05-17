package com.dev.earalarm.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.setting.model.SettingUiState
import com.dev.firebase.FirebaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val firebaseManager: FirebaseManager,
) : ViewModel() {

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
        combine(
            timerRepository.alarmVolume,
            timerRepository.media,
            timerRepository.vibrate
        ) { volume, media, vibrate ->
            _settingUiState.update {
                it.copy(
                    volume = volume,
                    alarmMedia = media,
                    vibrate = vibrate
                )
            }
        }.catch { throwable ->
            firebaseManager.reportNonFatalError(throwable)
            _errorFlow.emit(throwable)
        }.launchIn(viewModelScope)
    }

    fun setAlarmSound(path: String) {
        viewModelScope.launch {
            try {
                timerRepository.setMediaPath(path)
            } catch (e: Throwable) {
                firebaseManager.reportNonFatalError(e)
                _errorFlow.emit(e)
            }
        }
    }

    fun setVolume(volume: Int) {
        viewModelScope.launch {
            try {
                timerRepository.setAlarmVolume(volume)
            } catch (e: Throwable) {
                firebaseManager.reportNonFatalError(e)
                _errorFlow.emit(e)
            }
        }
    }

    fun setVibrate(vibrate: Boolean) {
        viewModelScope.launch {
            try {
                timerRepository.setVibrate(vibrate)
            } catch (e: Throwable) {
                firebaseManager.reportNonFatalError(e)
                _errorFlow.emit(e)
            }
        }
    }
}