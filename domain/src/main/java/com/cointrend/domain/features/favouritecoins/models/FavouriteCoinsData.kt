package com.cointrend.domain.features.favouritecoins.models

import com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh.models.BaseCoinsWithMarketDataWithLastUpdate
import com.cointrend.domain.models.CoinWithMarketData
import java.time.LocalDateTime

data class FavouriteCoinsData(
    val coins: List<CoinWithMarketData>,
    override val lastUpdate: LocalDateTime
) : BaseCoinsWithMarketDataWithLastUpdate(
    coinsWithMarketDataList = coins,
    lastUpdate = lastUpdate
)


