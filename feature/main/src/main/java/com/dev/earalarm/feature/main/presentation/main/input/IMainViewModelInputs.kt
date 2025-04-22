package kr.ac.tukorea.android.earalarm.presentation.main.input

interface IMainViewModelInputs {
    fun updateTimePicker(hour: Int, minute: Int)
    fun updateTimePickerWithMinute(minute: Int)
    fun resetTimePicker()
    fun startTimerAlarm()
    fun setVolume(volume: Int)
    fun setAlarmSound(path: String)
    fun showSettingDialog()
    fun showDeniedNotificationDialog()
    fun dismissAlarm()
    fun dismissDialog()
}