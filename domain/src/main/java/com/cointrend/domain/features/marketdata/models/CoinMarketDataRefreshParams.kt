package com.cointrend.domain.features.marketdata.models

import com.cointrend.domain.models.Currency

data class CoinMarketDataRefreshParams(
    val coinId: String,
    val currency: Currency,
)
