package com.dev.firebase.model

object FA {

    object Event {
        const val TIMER_START = "timer_start"
        const val ALARM_DISMISS = "alarm_dismiss"
        const val ALARM_RING = "alarm_ring"
        const val ALARM_RING_STOP = "alarm_ring_stop"
        const val QUICK_ADD = "timer_quick_add"
        const val PERMISSION_RESULT = "permission_result"
        const val NAVIGATION = "navigation"
        const val SETTING_SOUND_CHANGE = "setting_sound_change"
        const val SETTING_VOLUME_CHANGE = "setting_volume_change"
        const val SETTING_VIBRATE_CHANGE = "setting_vibrate_change"
        const val ERROR_DISPLAYED = "error_displayed"
        const val REVIEW_REQUEST = "review_request"
    }

    object Param {

        object Key {
            const val MESSAGE = "message"
            const val TOTAL_MINUTES = "total_minutes"
            const val DISMISS_TYPE = "dismiss_type"
            const val ADD_TYPE = "add_type"
            const val PERMISSION_TYPE = "permission_type"
            const val SHOW_RATIONALE = "show_rationale"
            const val STATUS = "status"
            const val DESTINATION = "destination"
            const val VOLUME = "volume"
            const val VIBRATE = "vibrate"
            const val MEDIA = "media"
        }

        object Value {
            const val DISMISS_NOTIFICATION = "dismiss_notification"
            const val DISMISS_SCREEN = "dismiss_screen"
            const val HOUR_1 = "1_hour"
            const val MIN_30 = "30_min"
            const val MIN_10 = "10_min"
            const val MIN_5 = "5_min"
            const val RESET = "reset"
            const val NOTIFICATION = "notification"
            const val EXACT_ALARM = "exact_alarm"
            const val GRANTED = "granted"
            const val DENIED = "denied"
            const val CUSTOM = "custom"
            const val DEFAULT = "default"
            const val COMPLETE = "complete"
        }
    }
}