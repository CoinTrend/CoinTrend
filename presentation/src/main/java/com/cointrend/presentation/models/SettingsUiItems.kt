package com.cointrend.presentation.models

import kotlinx.collections.immutable.ImmutableList


data class SettingsState(
    val timeRangeOptions: ImmutableList<TimeRangeUi>,
    val selectedTimeRange: TimeRangeUi?
)
