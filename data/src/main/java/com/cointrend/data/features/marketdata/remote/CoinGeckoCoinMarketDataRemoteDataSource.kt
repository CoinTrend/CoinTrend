package com.cointrend.data.features.marketdata.remote

import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.cointrend.data.mappers.CoinGeckoDataMapper
import com.cointrend.domain.models.CoinMarketData
import com.cointrend.domain.models.Currency
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinGeckoCoinMarketDataRemoteDataSource @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper,
    private val dispatchers: DispatcherProvider
) : CoinMarketDataRemoteDataSource {

    override suspend fun retrieveCoinMarketData(
        coinId: String,
        currency: Currency
    ): Result<CoinMarketData> {
        return Result.runCatching {
            val coinMarketData = withContext(dispatchers.io) {
                coinGeckoApiService.getCoinsMarkets(
                    currency = mapper.mapCurrencyToCoinGeckoApiValue(currency),
                    includeSparkline7dData = true,
                    priceChangePercentageIntervals = "7d",
                    coinIds = coinId
                ).first()
            }

            mapper.mapCoinMarketData(coinMarketData)
        }
    }

    override suspend fun retrieveCoinsMarketData(
        coinIdsList: List<String>,
        currency: Currency
    ): Result<List<CoinMarketData>> {
        return Result.runCatching {
            val coinsMarketData = withContext(dispatchers.io) {
                coinGeckoApiService.getCoinsMarkets(
                    currency = mapper.mapCurrencyToCoinGeckoApiValue(currency),
                    includeSparkline7dData = true,
                    priceChangePercentageIntervals = "7d",
                    coinIds = coinIdsList.joinToString(",")
                )
            }

            val mappedCoins = mapper.mapCoinsWithMarketDataList(coinsMarketData)
            val marketDataList = mutableListOf<CoinMarketData>()

            coinIdsList.forEach { id ->
                mappedCoins.find {
                    it.id == id
                }?.marketData?.let { data ->
                    marketDataList.add(data)
                } ?: throw IllegalStateException("The retrieved coins market data list does not contain data of coin with id '$id'.")
            }

            marketDataList
        }
    }

}