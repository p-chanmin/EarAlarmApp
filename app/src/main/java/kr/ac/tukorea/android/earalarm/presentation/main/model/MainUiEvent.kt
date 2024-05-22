package kr.ac.tukorea.android.earalarm.presentation.main.model

sealed class MainUiEvent {
    data object SetAlarm : MainUiEvent()
    data object DismissAlarm : MainUiEvent()
    data object DeniedExactAlarm : MainUiEvent()
}