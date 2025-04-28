package com.dev.earalarm.core.navigation

import kotlinx.serialization.Serializable

const val DEEP_LINK_BASE_PATH = "earalarm://feature/"

sealed interface Route {
    @Serializable
    data object Timer : Route

    @Serializable
    data object Setting : Route
}