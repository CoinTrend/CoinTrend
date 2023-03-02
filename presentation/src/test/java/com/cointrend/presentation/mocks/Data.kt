package com.cointrend.presentation.mocks

import androidx.compose.ui.graphics.Color
import com.cointrend.domain.models.CoinMarketData
import com.cointrend.domain.models.CoinWithMarketData
import com.cointrend.presentation.models.CoinWithMarketDataUiItem
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

internal val expectedCoinWithMarketData = CoinWithMarketData(
    id = "btc",
    name = "",
    symbol = "",
    image = "",
    marketData = CoinMarketData(
        price = 0.0,
        marketCapRank = 0,
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
        remoteLastUpdate = null,
        lastUpdate = LocalDateTime.now()
    ),
    rank = 0
)

internal val expectedCoinWithMissingMarketData = expectedCoinWithMarketData.copy(
    marketData = null
)

internal val expectedCoinWithMarketDataBtc = expectedCoinWithMarketData.toUiModel()
internal val expectedCoinWithMarketDataEth = expectedCoinWithMarketData.copy(id = "eth").toUiModel()
internal val expectedCoinWithMarketDataUsdc = expectedCoinWithMarketData.copy(id = "usdc").toUiModel()
internal val expectedCoinWithMarketDataUsdt = expectedCoinWithMarketData.copy(id = "usdt").toUiModel()
internal val expectedCoinWithMarketDataSol = expectedCoinWithMarketData.copy(id = "sol").toUiModel()

internal fun getCoinsWithMarketDataUiList() = listOf(
    expectedCoinWithMarketDataBtc,
    expectedCoinWithMarketDataEth,
    expectedCoinWithMarketDataUsdc,
    expectedCoinWithMarketDataUsdt,
    expectedCoinWithMarketDataSol,
)

internal fun CoinWithMarketData.toUiModel() = CoinWithMarketDataUiItem(
    id = this.id,
    name = this.name,
    symbol = this.symbol,
    imageUrl = "",
    price = "",
    marketCapRank = "",
    priceChangePercentage = "",
    trendColor = Color.Black,
    sparklineData = persistentListOf(),
    lastUpdate = ""
)
