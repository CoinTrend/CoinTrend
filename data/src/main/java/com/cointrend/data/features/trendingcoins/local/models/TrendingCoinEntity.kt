package com.cointrend.data.features.trendingcoins.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trending_coins",
    indices = [Index(value = ["id"], unique = true)],
)
data class TrendingCoinEntity(
    @PrimaryKey val sortedPosition: Int,

    val id: String,
    val name: String,
    val symbol: String,
    val image: String,
    val rank: Int?,
    val insertionDate: Long
)
