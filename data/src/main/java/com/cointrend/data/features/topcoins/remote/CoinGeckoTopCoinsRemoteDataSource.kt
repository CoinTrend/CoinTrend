package com.cointrend.data.features.topcoins.remote

import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.data.features.topcoins.TopCoinsRemoteDataSource
import com.cointrend.data.mappers.CoinGeckoDataMapper
import com.cointrend.domain.models.CoinWithMarketData
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinGeckoTopCoinsRemoteDataSource @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper,
    private val dispatchers: DispatcherProvider
) : TopCoinsRemoteDataSource {

    override suspend fun retrieveTopCoinsWithMarketData(
        numCoins: Int,
        currency: Currency,
        ordering: Ordering,
        timeRange: TimeRange
    ): Result<List<CoinWithMarketData>> {
        return Result.runCatching {
            val coinsList = withContext(dispatchers.io) {
                coinGeckoApiService.getCoinsMarkets(
                    currency = mapper.mapCurrencyToCoinGeckoApiValue(currency),
                    page = 1,
                    numCoinsPerPage = numCoins,
                    order = mapper.mapOrderingToCoinGeckoApiValue(ordering = ordering),
                    includeSparkline7dData = mapper.mapTimeRangeToIncludeSparkline7dData(timeRange = timeRange),
                    priceChangePercentageIntervals = mapper.mapTimeRangeToPriceChangeCoinGeckoApiValue(timeRange = timeRange)
                )
            }

            mapper.mapCoinsWithMarketDataList(coinsList)
        }
    }

}