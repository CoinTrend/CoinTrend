package com.cointrend.data.features.marketdata.local

import androidx.room.*
import com.cointrend.data.db.room.models.CoinMarketDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CoinsMarketDataDao {

    @Transaction
    @Query("SELECT * FROM coins_market_data where coinId = :coinId")
    abstract fun getCoinMarketData(coinId: String): Flow<CoinMarketDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoinMarketData(coinMarketData: CoinMarketDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCoinsMarketData(coinsMarketData: List<CoinMarketDataEntity>)

    @Query("DELETE FROM coins_market_data")
    abstract suspend fun deleteAllCoinsMarketData()

}