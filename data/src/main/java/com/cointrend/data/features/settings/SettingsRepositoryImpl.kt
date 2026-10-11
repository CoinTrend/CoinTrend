package com.cointrend.data.features.settings

import com.cointrend.domain.features.settings.SettingsRepository
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.LaunchingPage
import com.cointrend.domain.models.TimeRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
): SettingsRepository {

    override fun getSettingsConfigurationFlow(): Flow<SettingsConfiguration?> {
        return localDataSource.getSettingsFlow().distinctUntilChanged()
    }

    override suspend fun setDefaultTimeRange(timeRange: TimeRange): Result<Unit> {
        return Result.runCatching {
            localDataSource.setDefaultTimeRange(timeRange = timeRange)
        }
    }

    override suspend fun setDefaultLaunchingPage(launchingPage: LaunchingPage): Result<Unit> {
        return Result.runCatching {
            localDataSource.setDefaultLaunchingPage(launchingPage = launchingPage)
        }
    }

}

interface SettingsLocalDataSource {

    fun getSettingsFlow(): Flow<SettingsConfiguration?>
    suspend fun setDefaultTimeRange(timeRange: TimeRange)
    suspend fun  setDefaultLaunchingPage(launchingPage: LaunchingPage)

}