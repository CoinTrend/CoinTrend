package com.cointrend.domain.features.settings

import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.TimeRange
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getSettingsConfigurationFlow(): Flow<SettingsConfiguration?>

    suspend fun setDefaultTimeRange(timeRange: TimeRange): Result<Unit>

}