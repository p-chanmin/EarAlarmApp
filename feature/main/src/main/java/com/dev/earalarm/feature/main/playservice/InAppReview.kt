package com.dev.earalarm.feature.main.playservice

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.dev.firebase.LocalFirebaseManager
import com.dev.firebase.model.FA
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.firebase.analytics.logEvent
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Composable
internal fun InAppReview(
    lastReviewDate: ZonedDateTime?,
    setLastReviewDate: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val firebaseManager = LocalFirebaseManager.current

    LaunchedEffect(lastReviewDate) {
        val now = ZonedDateTime.now(ZoneOffset.UTC)

        if (lastReviewDate == null) {
            setLastReviewDate()
        } else {
            if (lastReviewDate.plusMonths(1).isBefore(now)) {
                val manager = ReviewManagerFactory.create(context)

                val request = manager.requestReviewFlow()

                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        activity?.let {
                            val flow = manager.launchReviewFlow(it, reviewInfo)
                            flow.addOnCompleteListener { _ ->
                                firebaseManager.firebaseAnalytics.logEvent(FA.Event.REVIEW_REQUEST) {
                                    param(FA.Param.Key.STATUS, FA.Param.Value.COMPLETE)
                                }
                            }
                        }
                        setLastReviewDate()
                    } else {
                        @ReviewErrorCode val reviewErrorCode =
                            (task.exception as ReviewException).errorCode
                        val message = (task.exception as ReviewException).message
                        firebaseManager.logCrashlyticsMessage("ReviewException: errorCode=$reviewErrorCode, message=$message")
                    }
                }
            }
        }
    }
}