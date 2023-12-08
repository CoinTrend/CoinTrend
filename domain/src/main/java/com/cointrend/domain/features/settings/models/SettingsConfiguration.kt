package com.cointrend.domain.features.settings.models

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange


data class SettingsConfiguration(
    val currency: Currency,
    val ordering: Ordering,
    val defaultTimeRange: TimeRange
)