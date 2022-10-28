package com.cointrend.data.features.topcoins

import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.topcoins.TopCoinsRepository
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.topcoins.models.TopCoinsRefreshParams
import com.cointrend.domain.models.CoinWithMarketData
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class TopCoinsRepositoryImpl @Inject constructor(
    private val remoteSource: TopCoinsRemoteDataSource,
    private val localSource: TopCoinsLocalDataSource,
    private val dispatchers: DispatcherProvider
) : TopCoinsRepository {

    override fun getDataFlow(inputParams: Unit): Flow<TopCoinsData> {
        return localSource.getAllCoinsFlow()
            .distinctUntilChanged()
            .map {
                if (it.isEmpty()) {
                    throw EmptyDatabaseException()
                } else {
                    TopCoinsData(
                        topCoins = it,
                        lastUpdate = getLastUpdateDate(it)
                    )
                }
            }.flowOn(dispatchers.default)
    }

    override suspend fun refreshData(params: TopCoinsRefreshParams): Result<Unit> {
        return Result.runCatching {
            withContext(dispatchers.default) {
                val coinsList = remoteSource.retrieveTopCoinsWithMarketData(
                    currency = params.currency,
                    numCoins = params.numCoins,
                    ordering = Ordering.MarketCapDesc,
                ).getOrElse { throw it }

                val sortedCoinsList = sortCoins(
                    coinsList = coinsList,
                    ordering = params.ordering
                )

                localSource.insertCoins(sortedCoinsList)
            }
        }
    }

    // Gets the least recent last update date among the coins
    private fun getLastUpdateDate(coinsList: List<CoinWithMarketData>): LocalDateTime {
        return coinsList.minOf { it.marketData.lastUpdate }
    }

    private fun sortCoins(coinsList: List<CoinWithMarketData>, ordering: Ordering): List<CoinWithMarketData> {
        val itemsList = coinsList.toMutableList()

        when (ordering) {
            Ordering.MarketCapAsc -> itemsList.sortByDescending { it.rank }
            Ordering.MarketCapDesc -> itemsList.sortBy { it.rank }
            Ordering.PriceAsc -> itemsList.sortBy { it.marketData.price }
            Ordering.PriceDesc -> itemsList.sortByDescending { it.marketData.price }
            Ordering.PriceChangeAsc -> itemsList.sortBy { it.marketData.priceChangePercentage }
            Ordering.PriceChangeDesc -> itemsList.sortByDescending { it.marketData.priceChangePercentage }
            Ordering.NameAsc -> itemsList.sortBy { it.name }
            Ordering.NameDesc -> itemsList.sortByDescending { it.name }
        }

        return itemsList
    }

}

interface TopCoinsLocalDataSource {

    fun getAllCoinsFlow(): Flow<List<CoinWithMarketData>>
    suspend fun insertCoins(coinsList: List<CoinWithMarketData>)
    suspend fun deleteAllCoins()

}

interface TopCoinsRemoteDataSource {

    suspend fun retrieveTopCoinsWithMarketData(
        numCoins: Int,
        currency: Currency,
        ordering: Ordering,
    ): Result<List<CoinWithMarketData>>

}