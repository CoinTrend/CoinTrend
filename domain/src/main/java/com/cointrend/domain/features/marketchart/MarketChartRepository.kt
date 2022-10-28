package com.cointrend.domain.features.marketchart

import com.cointrend.domain.features.marketchart.models.MarketChartDataPoint
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.TimeRange

interface MarketChartRepository {

    suspend fun getMarketChartData(
        coinId: String,
        currency: Currency,
        timeRange: TimeRange
    ): Result<List<MarketChartDataPoint>>

}