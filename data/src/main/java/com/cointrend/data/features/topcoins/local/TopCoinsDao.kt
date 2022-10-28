package com.cointrend.data.features.topcoins.local

import androidx.room.*
import com.cointrend.data.db.room.models.CoinMarketDataEntity
import com.cointrend.data.features.topcoins.local.models.TopCoinEntity
import com.cointrend.data.features.topcoins.local.models.TopCoinWithMarketDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TopCoinsDao {

    @Transaction
    @Query("SELECT * FROM top_coins")
    abstract fun getAllCoins(): Flow<List<TopCoinWithMarketDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoins(coinsMarketData: List<CoinMarketDataEntity>, coins: List<TopCoinEntity>)

    @Query("DELETE FROM top_coins")
    abstract suspend fun deleteAllCoins()

}