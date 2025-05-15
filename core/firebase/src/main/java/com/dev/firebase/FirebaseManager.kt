package com.dev.firebase

import com.google.firebase.analytics.FirebaseAnalytics

interface FirebaseManager {
    val firebaseAnalytics: FirebaseAnalytics

    fun screenLogEvent(screenName: String, orientation: Int)
}