package com.cointrend.domain.features.marketchart

import com.cointrend.domain.features.marketchart.models.MarketChartDataPoint
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.TimeRange
import javax.inject.Inject

class GetMarketChartDataUseCase @Inject constructor(
    private val marketChartRepository: MarketChartRepository,
    private val settingsConfiguration: SettingsConfiguration
) {

    suspend operator fun invoke(
        coinId: String,
        timeRange: TimeRange
    ): Result<List<MarketChartDataPoint>> {
        return marketChartRepository.getMarketChartData(
            coinId = coinId,
            currency = settingsConfiguration.currency,
            timeRange = timeRange
        )
    }

}