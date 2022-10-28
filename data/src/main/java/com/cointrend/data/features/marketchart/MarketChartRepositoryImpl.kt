package com.cointrend.data.features.marketchart

import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.data.mappers.CoinGeckoDataMapper
import com.cointrend.domain.features.marketchart.MarketChartRepository
import com.cointrend.domain.features.marketchart.models.MarketChartDataPoint
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.TimeRange
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MarketChartRepositoryImpl @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper,
    private val dispatchers: DispatcherProvider
) : MarketChartRepository {

    override suspend fun getMarketChartData(
        coinId: String,
        currency: Currency,
        timeRange: TimeRange
    ): Result<List<MarketChartDataPoint>> {
        return Result.runCatching {
            withContext(dispatchers.default) {
                val result = withContext(dispatchers.io) {
                    coinGeckoApiService.getCoinMarketChart(
                        coinId = coinId,
                        currency = mapper.mapCurrencyToCoinGeckoApiValue(currency),
                        days = mapper.mapTimeRangeToCoinGeckoApiValue(timeRange)
                    )
                }

                mapper.mapMarketChartDataPointList(result)
            }

        }
    }

}