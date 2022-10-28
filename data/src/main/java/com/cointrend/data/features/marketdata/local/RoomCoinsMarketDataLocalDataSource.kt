package com.cointrend.data.features.marketdata.local

import com.cointrend.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cointrend.data.mappers.RoomDataMapper
import com.cointrend.domain.models.CoinMarketData
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomCoinsMarketDataLocalDataSource @Inject constructor(
    private val coinsMarketDataDao: CoinsMarketDataDao,
    private val mapper: RoomDataMapper,
    private val dispatchers: DispatcherProvider
) : CoinMarketDataLocalDataSource {

    override fun getCoinMarketDataFlow(coinId: String): Flow<CoinMarketData?> {
        return coinsMarketDataDao.getCoinMarketData(coinId = coinId)
            .flowOn(dispatchers.io)
            .map {
                it?.let {
                    mapper.mapCoinMarketData(it)
                }
            }
    }

    override suspend fun insertCoinMarketData(coinId: String, coinMarketData: CoinMarketData) {
        withContext(dispatchers.io) {
            coinsMarketDataDao.insertCoinMarketData(
                coinMarketData = mapper.mapCoinMarketDataEntity(
                    coinId = coinId,
                    coinMarketData = coinMarketData
                )
            )
        }
    }

    override suspend fun insertCoinsMarketData(
        coinIdsList: List<String>,
        coinsMarketData: List<CoinMarketData>
    ) {
        val mappedCoinsMarketDataEntities = coinIdsList.mapIndexed { index, id ->
            mapper.mapCoinMarketDataEntity(
                coinId = id,
                coinMarketData = coinsMarketData[index]
            )
        }

        withContext(dispatchers.io) {
            coinsMarketDataDao.insertCoinsMarketData(coinsMarketData = mappedCoinsMarketDataEntities)
        }
    }

}