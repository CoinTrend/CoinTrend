package com.cointrend.domain.features.settings

import com.cointrend.domain.features.marketdata.DeleteAllMarketDataUseCase
import com.cointrend.domain.models.TimeRange
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SetDefaultTimeRangeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getSettingsConfigurationFlowUseCase: GetSettingsConfigurationFlowUseCase,
    private val deleteAllMarketDataUseCase: DeleteAllMarketDataUseCase
) {

    suspend operator fun invoke(timeRange: TimeRange): Result<Unit> {
        val result = settingsRepository.setDefaultTimeRange(timeRange = timeRange)

        return if (result.isSuccess) {
            try {
                // Before returning, getSettingsConfigurationFlowUseCase() is called so that
                // the GlobalSettingsConfiguration is updated immediately with the new settings.
                getSettingsConfigurationFlowUseCase().firstOrNull()

                // Before returning, all market data is also deleted so that a data refresh is triggered
                // and coins data is automatically retrieved with the updated settings
                // TODO: implement a base UseCase that implements this behavior for all the UseCases for updating settings
                deleteAllMarketDataUseCase()

                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else result
    }

}