package com.cointrend.data.features.settings

import com.cointrend.domain.features.settings.SettingsRepository
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.TimeRange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
): SettingsRepository {

    override fun getSettingsConfigurationFlow(): Flow<SettingsConfiguration?> {
        TODO("Not yet implemented")
    }

    override suspend fun setDefaultTimeRange(timeRange: TimeRange): Result<Unit> {
        return Result.runCatching {
            localDataSource.setDefaultTimeRange(timeRange = timeRange)
        }
    }

}

interface SettingsLocalDataSource {

    fun getSettingsFlow(): Flow<SettingsConfiguration?>
    suspend fun setDefaultTimeRange(timeRange: TimeRange)

}