package com.cointrend.data.features.favouritecoins.local.models

import androidx.room.Embedded
import androidx.room.Relation
import com.cointrend.data.db.room.models.CoinMarketDataEntity

data class FavouriteCoinWithMarketDataEntity(
    @Embedded val coin: FavouriteCoinEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "coinId"
    )
    val marketData: CoinMarketDataEntity?
)
