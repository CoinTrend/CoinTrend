package com.cointrend.data.features.favouritecoins

import com.cointrend.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cointrend.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.favouritecoins.FavouriteCoinsRepository
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsRefreshParams
import com.cointrend.domain.models.Coin
import com.cointrend.domain.models.CoinWithMarketData
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class FavouriteCoinsRepositoryImpl @Inject constructor(
    private val localSource: FavouriteCoinsLocalDataSource,
    private val marketDataRemoteDataSource: CoinMarketDataRemoteDataSource,
    private val marketDataLocalDataSource: CoinMarketDataLocalDataSource,
    private val dispatchers: DispatcherProvider
) : FavouriteCoinsRepository {

    override fun getDataFlow(inputParams: Unit): Flow<FavouriteCoinsData> {
        return localSource.getAllCoinsFlow()
            .catch {
                throw if (it is NullPointerException) {
                    // NullPointerException is thrown when the market data of some coins
                    // is missing. An EmptyDatabaseException is thrown instead so that the domain layer
                    // triggers a data refresh.
                    EmptyDatabaseException()
                } else it
            }
            .distinctUntilChanged()
            .map {
                if (it.isEmpty()) {
                    FavouriteCoinsData(
                        coins = it,
                        lastUpdate = LocalDateTime.now()
                    )
                } else {
                    FavouriteCoinsData(
                        coins = it,
                        lastUpdate = getLastUpdateDate(it)
                    )
                }
            }.flowOn(dispatchers.default)
    }

    override suspend fun refreshData(params: FavouriteCoinsRefreshParams): Result<Unit> {
        return Result.runCatching {
            withContext(dispatchers.default) {
                val coinsIdsList = localSource.getFavouriteCoinsIds()

                if (coinsIdsList.isNotEmpty()) {
                    val coinsMarketData = marketDataRemoteDataSource.retrieveCoinsMarketData(
                        coinIdsList = coinsIdsList,
                        currency = params.currency,
                    ).getOrElse { throw it }

                    marketDataLocalDataSource.insertCoinsMarketData(
                        coinIdsList = coinsIdsList,
                        coinsMarketData = coinsMarketData
                    )
                }
            }
        }
    }

    override suspend fun addFavouriteCoin(coin: Coin): Result<Unit> {
        return Result.runCatching {
            localSource.insertCoin(coin = coin)
        }
    }

    override suspend fun removeFavouriteCoin(coinId: String): Result<Unit> {
        return Result.runCatching {
            localSource.deleteCoin(coinId = coinId)
        }
    }

    override suspend fun getFavouriteCoinsIds(): Result<List<String>> {
        return Result.runCatching {
            localSource.getFavouriteCoinsIds()
        }
    }

    // Gets the least recent last update date among the coins
    private fun getLastUpdateDate(coinsList: List<CoinWithMarketData>): LocalDateTime {
        return coinsList.minOf { it.marketData.lastUpdate }
    }

}

interface FavouriteCoinsLocalDataSource {

    fun getAllCoinsFlow(): Flow<List<CoinWithMarketData>>
    suspend fun getFavouriteCoinsIds(): List<String>
    suspend fun insertCoin(coin: Coin)
    suspend fun deleteCoin(coinId: String)
    suspend fun deleteAllCoins()

}