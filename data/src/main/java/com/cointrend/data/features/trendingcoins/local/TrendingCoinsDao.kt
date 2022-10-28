package com.cointrend.data.features.trendingcoins.local

import androidx.room.*
import com.cointrend.data.features.trendingcoins.local.models.TrendingCoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrendingCoinsDao {

    @Transaction
    @Query("SELECT * FROM trending_coins")
    abstract fun getAllCoins(): Flow<List<TrendingCoinEntity>>

    @Transaction
    @Query("SELECT MAX(insertionDate) FROM trending_coins")
    abstract suspend fun getLastUpdateDate(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoins(trendingCoins: List<TrendingCoinEntity>)

    @Query("DELETE FROM trending_coins")
    abstract suspend fun deleteAllCoins()

}