package com.dev.core.admob

import android.app.Activity
import androidx.compose.runtime.staticCompositionLocalOf
import com.google.android.gms.ads.AdRequest

interface AdMobManager {
    val adRequest: AdRequest
    fun showInterstitialAlarmAd(activity: Activity)
}