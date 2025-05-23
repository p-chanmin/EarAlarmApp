package com.dev.earalarm.feature.main

import androidx.compose.runtime.Immutable
import java.time.ZonedDateTime

@Immutable
data class MainUiState(
    val lastReviewDate: ZonedDateTime? = ZonedDateTime.now(),
    val rejectFlexibleUpdateDate: ZonedDateTime? = ZonedDateTime.now(),
)
