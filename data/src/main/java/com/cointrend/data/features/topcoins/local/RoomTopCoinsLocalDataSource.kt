package com.cointrend.data.features.topcoins.local

import com.cointrend.data.features.topcoins.TopCoinsLocalDataSource
import com.cointrend.data.mappers.RoomDataMapper
import com.cointrend.domain.models.CoinWithMarketData
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomTopCoinsLocalDataSource @Inject constructor(
    private val topCoinsDao: TopCoinsDao,
    private val mapper: RoomDataMapper,
    private val dispatchers: DispatcherProvider
) : TopCoinsLocalDataSource {

    override fun getAllCoinsFlow(): Flow<List<CoinWithMarketData>> {
        return topCoinsDao.getAllCoins()
            .flowOn(dispatchers.io)
            .map {
                mapper.mapTopCoinWithMarketDataList(it)
            }
    }

    override suspend fun insertCoins(coinsList: List<CoinWithMarketData>) {
        val topCoins = mapper.mapTopCoinEntityList(coinsList)
        val coinsMarketData = mapper.mapCoinMarketDataEntityList(coinsList)

        withContext(dispatchers.io) {
            topCoinsDao.insertCoins(
                coinsMarketData = coinsMarketData,
                coins = topCoins,
            )
        }
    }

    override suspend fun deleteAllCoins() {
        withContext(dispatchers.io) {
            topCoinsDao.deleteAllCoins()
        }
    }

}