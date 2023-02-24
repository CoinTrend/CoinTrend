package com.cointrend.data.features.topcoins.local.models

import androidx.room.Embedded
import androidx.room.Relation
import com.cointrend.data.db.room.models.CoinMarketDataEntity

data class TopCoinWithMarketDataEntity(
    @Embedded val coin: TopCoinEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "coinId"
    )
    val marketData: CoinMarketDataEntity?
)
