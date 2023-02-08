package com.cointrend.data.features.favouritecoins.local

import androidx.room.*
import com.cointrend.data.features.favouritecoins.local.models.FavouriteCoinEntity
import com.cointrend.data.features.favouritecoins.local.models.FavouriteCoinWithMarketDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavouriteCoinsDao {

    @Transaction
    @Query("SELECT * FROM favourite_coins")
    abstract fun getAllCoins(): Flow<List<FavouriteCoinWithMarketDataEntity>>

    @Transaction
    @Query("SELECT id FROM favourite_coins")
    abstract suspend fun getAllCoinsIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoin(coin: FavouriteCoinEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoins(coins: List<FavouriteCoinEntity>)

    @Query("DELETE FROM favourite_coins WHERE id = :coinId")
    abstract suspend fun deleteCoin(coinId: String)

    @Query("DELETE FROM favourite_coins")
    abstract suspend fun deleteAllCoins()

}