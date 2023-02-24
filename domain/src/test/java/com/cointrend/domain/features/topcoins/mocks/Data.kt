package com.cointrend.domain.features.topcoins.mocks

import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.models.CoinWithMarketData
import com.cointrend.domain.models.TOP_COINS_MINUTES_REFRESH_PERIOD
import java.time.LocalDateTime

internal val expectedCoinWithMissingMarketData = CoinWithMarketData(
    id = "",
    symbol = "",
    name = "",
    image = "",
    rank = 0,
    marketData = null
)

internal val expectedTopCoinDataThatDontRequireRefresh = TopCoinsData(
    topCoins = emptyList(),
    lastUpdate = LocalDateTime.now()
)

internal val expectedTopCoinDataUpdatedButWithMissingMarketDataThatShouldBeRefreshed = expectedTopCoinDataThatDontRequireRefresh.copy(
    topCoins = listOf(expectedCoinWithMissingMarketData)
)

internal val expectedTopCoinDataThatShouldBeRefreshed = TopCoinsData(
    topCoins = emptyList(),
    lastUpdate = LocalDateTime.now()
        .minusMinutes(TOP_COINS_MINUTES_REFRESH_PERIOD.toLong())
        .minusMinutes(2)
)

internal val expectedTopCoinDataThatShouldBeRefreshedAndWithMissingMarketData = expectedTopCoinDataThatShouldBeRefreshed.copy(
    topCoins = listOf(expectedCoinWithMissingMarketData)
)