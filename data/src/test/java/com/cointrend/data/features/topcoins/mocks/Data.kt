package com.cointrend.data.features.topcoins.mocks

import com.cointrend.domain.models.CoinMarketData
import com.cointrend.domain.models.CoinWithMarketData
import com.github.davidepanidev.kotlinextensions.utils.test.TestException
import java.time.LocalDateTime

val expectedCoinWithMarketData = CoinWithMarketData(
    id = "",
    name = "",
    symbol = "",
    image = "",
    marketData = CoinMarketData(
        0.0,
        marketCap = 0.0,
        marketCapChangePercentage24h = 0.0,
        totalVolume = 0.0,
        high24h = 0.0,
        low24h = 0.0,
        circulatingSupply = 0.0,
        totalSupply = 0.0,
        maxSupply = 0.0,
        ath = 0.0,
        athChangePercentage = 0.0,
        athDate = LocalDateTime.now(),
        atl = 0.0,
        atlChangePercentage = 0.0,
        atlDate = LocalDateTime.now(),
        priceChangePercentage = 0.0,
        sparklineData = null,
        lastUpdate = LocalDateTime.now()
    ),
    rank = 0
)

val expectedException = TestException("Ciao")
