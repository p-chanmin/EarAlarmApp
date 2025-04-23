package com.dev.earalarm.feature.timer.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.timer.model.MeasureUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeasureViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _measureUiState = MutableStateFlow(MeasureUiState())
    val measureUiState = _measureUiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MeasureUiState()
    )

    init {
        println("MeasureViewModel Init $this")
    }

    fun dismissTimerAlarm() {
        viewModelScope.launch {
            timerRepository.removeTimerAlarmInfo()
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("MeasureViewModel Cleared $this")
    }
}