package com.dev.firebase

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : FirebaseManager {

    override val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun screenLogEvent(screenName: String, orientation: Int) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(
                "orientation",
                when (orientation) {
                    1 -> "portrait"
                    2 -> "landscape"
                    else -> "undefined"
                }
            )
        }
    }
}