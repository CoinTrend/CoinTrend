package com.cointrend.data.features.marketdata

import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.marketdata.CoinMarketDataRepository
import com.cointrend.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cointrend.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cointrend.domain.models.CoinMarketData
import com.cointrend.domain.models.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CoinMarketDataRepositoryImpl @Inject constructor(
    private val remoteSource: CoinMarketDataRemoteDataSource,
    private val localSource: CoinMarketDataLocalDataSource
) : CoinMarketDataRepository {

    override fun getDataFlow(inputParams: CoinMarketDataInputParams): Flow<CoinMarketData> {
        return localSource.getCoinMarketDataFlow(coinId = inputParams.coinId)
            .distinctUntilChanged()
            .map {
                it ?: throw EmptyDatabaseException()
            }
    }

    override suspend fun refreshData(params: CoinMarketDataRefreshParams): Result<Unit> {
        return Result.runCatching {
            val coinMarketData = remoteSource.retrieveCoinMarketData(
                coinId = params.coinId,
                currency = params.currency,
            ).getOrElse { throw it }

            localSource.insertCoinMarketData(
                coinId = params.coinId,
                coinMarketData = coinMarketData
            )
        }
    }

}



interface CoinMarketDataLocalDataSource {

    fun getCoinMarketDataFlow(coinId: String): Flow<CoinMarketData?>
    suspend fun insertCoinMarketData(coinId: String, coinMarketData: CoinMarketData)
    suspend fun insertCoinsMarketData(coinIdsList: List<String>, coinsMarketData: List<CoinMarketData>)

}

interface CoinMarketDataRemoteDataSource {

    suspend fun retrieveCoinMarketData(
        coinId: String,
        currency: Currency,
    ): Result<CoinMarketData>

    suspend fun retrieveCoinsMarketData(
        coinIdsList: List<String>,
        currency: Currency,
    ): Result<List<CoinMarketData>>

}