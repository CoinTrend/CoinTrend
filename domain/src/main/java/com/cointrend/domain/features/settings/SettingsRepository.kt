package com.cointrend.domain.features.settings

import com.cointrend.domain.models.TimeRange

interface SettingsRepository {

    fun setDefaultTimeRange(timeRange: TimeRange): Result<Unit>

}