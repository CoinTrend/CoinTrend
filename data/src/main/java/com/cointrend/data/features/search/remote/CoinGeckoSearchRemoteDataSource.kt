package com.cointrend.data.features.search.remote

import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.data.features.search.SearchRemoteDataSource
import com.cointrend.data.mappers.CoinGeckoDataMapper
import com.cointrend.domain.models.Coin
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinGeckoSearchRemoteDataSource @Inject constructor(
    private val coinGeckoApiService: CoinGeckoApiService,
    private val mapper: CoinGeckoDataMapper,
    private val dispatchers: DispatcherProvider
) : SearchRemoteDataSource {

    override suspend fun search(query: String): Result<List<Coin>> {
        return Result.runCatching {
            val searchDto = withContext(dispatchers.io) {
                coinGeckoApiService.getSearch(
                    query = query
                )
            }

            mapper.mapCoins(searchDto)
        }
    }

}