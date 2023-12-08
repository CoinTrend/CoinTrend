package com.cointrend.domain.features.settings

import com.cointrend.domain.models.TimeRange
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class SetDefaultTimeRangeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getSettingsConfigurationFlowUseCase: GetSettingsConfigurationFlowUseCase
) {

    suspend operator fun invoke(timeRange: TimeRange): Result<Unit> {
        val result = settingsRepository.setDefaultTimeRange(timeRange = timeRange)

        return if (result.isSuccess) {
            try {
                // Before returning, getSettingsConfigurationFlowUseCase() is called so that
                // the GlobalSettingsConfiguration is sure to be updated immediately.
                getSettingsConfigurationFlowUseCase().last()
                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else result
    }

}