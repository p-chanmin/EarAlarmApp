package com.dev.earalarm.feature.timer.model

sealed class TimerState {
    data object Home : TimerState()
    data object Measure : TimerState()
    data object Loading : TimerState()
}