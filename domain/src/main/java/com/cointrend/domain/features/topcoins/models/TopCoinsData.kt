package com.cointrend.domain.features.topcoins.models

import com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh.models.BaseCoinsWithMarketDataWithLastUpdate
import com.cointrend.domain.models.CoinWithMarketData
import java.time.LocalDateTime

data class TopCoinsData(
    val topCoins: List<CoinWithMarketData>,
    override val lastUpdate: LocalDateTime
) : BaseCoinsWithMarketDataWithLastUpdate(
    coinsWithMarketDataList = topCoins,
    lastUpdate = lastUpdate
)


