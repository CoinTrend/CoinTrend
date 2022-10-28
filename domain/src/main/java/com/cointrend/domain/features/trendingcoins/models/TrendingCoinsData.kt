package com.cointrend.domain.features.trendingcoins.models

import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate
import com.cointrend.domain.models.Coin
import java.time.LocalDateTime

data class TrendingCoinsData(
    val trendingCoins: List<Coin>,
    override val lastUpdate: LocalDateTime
) : BaseDataWithLastUpdateDate(
    lastUpdate = lastUpdate
)


