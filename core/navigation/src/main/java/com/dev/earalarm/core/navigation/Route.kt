package com.dev.earalarm.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Timer : Route

    @Serializable
    data object Setting : Route
}