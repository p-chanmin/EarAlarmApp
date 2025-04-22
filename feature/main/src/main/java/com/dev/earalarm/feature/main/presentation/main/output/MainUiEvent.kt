package kr.ac.tukorea.android.earalarm.presentation.main.output

sealed class MainUiEvent {
    data object SetAlarm : MainUiEvent()
    data object DismissAlarm : MainUiEvent()
}