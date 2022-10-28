package com.cointrend.data.features.trendingcoins.remote

import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.data.features.trendingcoins.TrendingCoinsRemoteDataSource
import com.cointrend.data.mappers.CoinGeckoDataMapper
import com.cointrend.domain.models.Coin
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinGeckoTrendingCoinsRemoteDataSource @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper,
    private val dispatchers: DispatcherProvider
) : TrendingCoinsRemoteDataSource {

    override suspend fun retrieveTrendingCoins(): Result<List<Coin>> {
        return Result.runCatching {
            val trendingCoinsDto = withContext(dispatchers.io) {
                coinGeckoApiService.getSearchTrending()
            }

            mapper.mapCoins(trendingCoinsDto = trendingCoinsDto)
        }
    }

}