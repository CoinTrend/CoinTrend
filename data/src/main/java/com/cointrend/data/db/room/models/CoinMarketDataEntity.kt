package com.cointrend.data.db.room.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins_market_data")
data class CoinMarketDataEntity(
    @PrimaryKey val coinId: String,

    val currentPrice: Double?,
    val marketCapRank: Int?,
    val marketCap: Double?,
    val marketCapChangePercentage24h: Double?,
    val totalVolume: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val athChangePercentage: Double?,
    val athDate: Long?,
    val atl: Double?,
    val atlChangePercentage: Double?,
    val atlDate: Long?,
    val priceChangePercentage: Double?,
    val sparklineData: String?,
    val remoteLastUpdate: Long?,
    val lastUpdate: Long
)
