package com.cointrend.presentation.models

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

abstract class BaseCoinWithMarketDataUiItem(
    open val id: String,
    open val name: String,
    open val symbol: String,
    open val imageUrl: String,
    open val price: String,
    open val marketCapRank: String,
    open val priceChangePercentage: String,
    open val trendColor: Color,
    open val sparklineData: ImmutableList<DataPoint>,
    open val lastUpdate: String
)

data class CoinWithMarketDataUiItem(
    override val id: String,
    override val name: String,
    override val symbol: String,
    override val imageUrl: String,
    override val price: String,
    override val marketCapRank: String,
    override val priceChangePercentage: String,
    override val trendColor: Color,
    override val sparklineData: ImmutableList<DataPoint>,
    override val lastUpdate: String
) : BaseCoinWithMarketDataUiItem(
    id = id,
    name = name,
    symbol = symbol,
    imageUrl = imageUrl,
    price = price,
    marketCapRank = marketCapRank,
    priceChangePercentage = priceChangePercentage,
    trendColor = trendColor,
    sparklineData = sparklineData,
    lastUpdate = lastUpdate
)

/**
 * Class used to create items that should shimmer the market data fields, i.e. [price], [marketCapRank] and [marketCapRank].
 * Values of those fields are be used as a fake placeholder data behind the shimmer.
 */
data class CoinWithShimmeringMarketDataUiItem(
    override val id: String,
    override val name: String,
    override val symbol: String,
    override val imageUrl: String,
    override val price: String,
    override val marketCapRank: String,
    override val priceChangePercentage: String,
    override val trendColor: Color,
    override val sparklineData: ImmutableList<DataPoint>,
    override val lastUpdate: String
) : BaseCoinWithMarketDataUiItem(
    id = id,
    name = name,
    symbol = symbol,
    imageUrl = imageUrl,
    price = price,
    marketCapRank = marketCapRank,
    priceChangePercentage = priceChangePercentage,
    trendColor = trendColor,
    sparklineData = sparklineData,
    lastUpdate = lastUpdate
)