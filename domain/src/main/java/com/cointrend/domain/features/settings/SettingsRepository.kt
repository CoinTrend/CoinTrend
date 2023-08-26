package com.cointrend.domain.features.settings

import com.cointrend.domain.models.TimeRange

interface SettingsRepository {

    suspend fun setDefaultTimeRange(timeRange: TimeRange): Result<Unit>

}