package com.cointrend.domain.features.settings.models

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsConfiguration @Inject constructor(
    internal var currency: Currency,
    internal var ordering: Ordering,
    internal var defaultTimeRange: TimeRange
) {

    fun getCurrency() = currency
    fun getOrdering() = ordering
    fun getDefaultTimeRange() = defaultTimeRange

}