package com.dev.earalarm.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.earalarm.core.data.TimerRepository
import com.dev.earalarm.feature.timer.model.TimerState
import com.dev.firebase.FirebaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val firebaseManager: FirebaseManager,
) : ViewModel() {

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    val timerState = timerRepository.alarmInfo
        .map {
            if (it == null) {
                TimerState.Home
            } else {
                TimerState.Measure
            }
        }
        .catch { throwable ->
            firebaseManager.reportNonFatalError(throwable)
            _errorFlow.emit(throwable)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimerState.Loading
        )
}