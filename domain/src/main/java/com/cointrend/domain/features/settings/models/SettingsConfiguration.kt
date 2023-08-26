package com.cointrend.domain.features.settings.models

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import javax.inject.Singleton

@Singleton
data class SettingsConfiguration(
    val currency: Currency,
    val ordering: Ordering,
    val defaultTimeRange: TimeRange
)