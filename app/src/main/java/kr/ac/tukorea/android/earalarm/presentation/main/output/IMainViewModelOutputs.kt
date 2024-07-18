package kr.ac.tukorea.android.earalarm.presentation.main.output

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IMainViewModelOutputs {
    val viewType: StateFlow<ViewType>
    val alarmUiState: StateFlow<AlarmUiState>
    val measuringUiState: StateFlow<MeasuringUiState>
    val event: SharedFlow<MainUiEvent>
}