package com.cointrend.data.features.trendingcoins

import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.trendingcoins.TrendingCoinsRepository
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import com.cointrend.domain.models.Coin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class TrendingCoinsRepositoryImpl @Inject constructor(
    private val remoteSource: TrendingCoinsRemoteDataSource,
    private val localSource: TrendingCoinsLocalDataSource
) : TrendingCoinsRepository {

    override fun getDataFlow(inputParams: Unit): Flow<TrendingCoinsData> {
        return localSource.getAllCoinsFlow()
            .distinctUntilChanged()
            .map {
                if (it.isEmpty()) {
                    throw EmptyDatabaseException()
                } else {
                    TrendingCoinsData(
                        trendingCoins = it,
                        lastUpdate = localSource.getLastUpdateDate()
                    )
                }
            }
    }

    override suspend fun refreshData(params: Unit): Result<Unit> {
        return Result.runCatching {
            val trendingCoins = remoteSource.retrieveTrendingCoins().getOrElse { throw it }

            localSource.insertCoins(trendingCoins)
        }
    }

}

interface TrendingCoinsLocalDataSource {

    fun getAllCoinsFlow(): Flow<List<Coin>>
    suspend fun getLastUpdateDate(): LocalDateTime
    suspend fun insertCoins(coinsList: List<Coin>)
    suspend fun deleteAllCoins()

}

interface TrendingCoinsRemoteDataSource {

    suspend fun retrieveTrendingCoins(): Result<List<Coin>>

}