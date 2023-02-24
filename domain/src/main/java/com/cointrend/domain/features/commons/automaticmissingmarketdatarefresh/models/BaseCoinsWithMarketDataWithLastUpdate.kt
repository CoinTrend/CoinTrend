package com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh.models

import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate
import com.cointrend.domain.models.CoinWithMarketData
import java.time.LocalDateTime

abstract class BaseCoinsWithMarketDataWithLastUpdate(
    open val coinsWithMarketDataList: List<CoinWithMarketData>,
    override val lastUpdate: LocalDateTime
) : BaseDataWithLastUpdateDate(
    lastUpdate = lastUpdate
)