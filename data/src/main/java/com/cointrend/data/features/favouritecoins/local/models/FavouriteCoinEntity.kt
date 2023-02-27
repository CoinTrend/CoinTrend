package com.cointrend.data.features.favouritecoins.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourite_coins"
)
data class FavouriteCoinEntity(
    @PrimaryKey val id: String,

    val name: String,
    val symbol: String,
    val image: String,
    val rank: Int? // Currently redundant as it corresponds to marketCapRank of CoinMarketData.
)
