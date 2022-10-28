package com.cointrend.data.features.trendingcoins.local

import com.cointrend.data.features.trendingcoins.TrendingCoinsLocalDataSource
import com.cointrend.data.mappers.RoomDataMapper
import com.cointrend.domain.models.Coin
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class RoomTrendingCoinsLocalDataSource @Inject constructor(
    private val trendingCoinsDao: TrendingCoinsDao,
    private val mapper: RoomDataMapper,
    private val dispatchers: DispatcherProvider
) : TrendingCoinsLocalDataSource {

    override fun getAllCoinsFlow(): Flow<List<Coin>> {
        return trendingCoinsDao.getAllCoins()
            .flowOn(dispatchers.io)
            .map {
                mapper.mapCoinList(it)
            }
    }

    override suspend fun getLastUpdateDate(): LocalDateTime {
        val lastUpdateDate = withContext(dispatchers.io) {
            trendingCoinsDao.getLastUpdateDate()
        }

        return mapper.mapLocalDateTime(
            millis = lastUpdateDate
        )
    }

    override suspend fun insertCoins(coinsList: List<Coin>) {
        val trendingCoins = mapper.mapTrendingCoinEntityList(coinsList)

        withContext(dispatchers.io) {
            trendingCoinsDao.insertCoins(
                trendingCoins = trendingCoins
            )
        }
    }

    override suspend fun deleteAllCoins() {
        withContext(dispatchers.io) {
            trendingCoinsDao.deleteAllCoins()
        }
    }

}