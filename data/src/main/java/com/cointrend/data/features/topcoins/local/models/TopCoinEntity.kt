package com.cointrend.data.features.topcoins.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "top_coins",
    indices = [Index(value = ["id"], unique = true)],
)
data class TopCoinEntity(
    @PrimaryKey val sortedPosition: Int,

    val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    val rank: Int? // Currently redundant as it corresponds to marketCapRank of CoinMarketData.
)
