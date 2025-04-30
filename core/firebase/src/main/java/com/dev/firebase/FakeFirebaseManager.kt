package com.dev.firebase

import com.google.firebase.analytics.FirebaseAnalytics

class FakeFirebaseManager : FirebaseManager {
    override val firebaseAnalytics: FirebaseAnalytics
        get() = TODO("Not yet implemented")

    override fun screenLogEvent(screenName: String, orientation: Int) {
        TODO("Not yet implemented")
    }
}