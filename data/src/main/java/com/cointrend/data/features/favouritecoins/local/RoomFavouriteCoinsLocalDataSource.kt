package com.cointrend.data.features.favouritecoins.local

import com.cointrend.data.features.favouritecoins.FavouriteCoinsLocalDataSource
import com.cointrend.data.mappers.RoomDataMapper
import com.cointrend.domain.models.Coin
import com.cointrend.domain.models.CoinWithMarketData
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomFavouriteCoinsLocalDataSource @Inject constructor(
    private val dao: FavouriteCoinsDao,
    private val mapper: RoomDataMapper,
    private val dispatchers: DispatcherProvider
) : FavouriteCoinsLocalDataSource {

    override fun getAllCoinsFlow(): Flow<List<CoinWithMarketData>> {
        return dao.getAllCoins()
            .flowOn(dispatchers.io)
            .map {
                mapper.mapFavouriteCoinWithMarketDataList(it)
            }
    }

    override suspend fun getFavouriteCoinsIds(): List<String> {
        return withContext(dispatchers.io) {
            dao.getAllCoinsIds()
        }
    }

    override suspend fun insertCoin(coin: Coin) {
        val mappedCoin = mapper.mapFavouriteCoinEntity(coin)

        withContext(dispatchers.io) {
            dao.insertCoin(
                coin = mappedCoin
            )
        }
    }

    override suspend fun insertCoins(coins: List<Coin>) {
        val mappedCoins = coins.map {
            mapper.mapFavouriteCoinEntity(it)
        }

        withContext(dispatchers.io) {
            dao.insertCoins(
                coins = mappedCoins
            )
        }
    }

    override suspend fun deleteCoin(coinId: String) {
        withContext(dispatchers.io) {
            dao.deleteCoin(coinId = coinId)
        }
    }

    override suspend fun deleteAllCoins() {
        withContext(dispatchers.io) {
            dao.deleteAllCoins()
        }
    }

}