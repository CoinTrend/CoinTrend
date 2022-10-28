package com.cointrend.presentation.models

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList

data class CoinWithMarketDataUiItem(
    val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val price: String,
    val marketCapRank: String,
    val priceChangePercentage: String,
    val trendColor: Color,
    val sparklineData: ImmutableList<DataPoint>?,
    val lastUpdate: String
)

data class TopCoinUiData(
    val topCoins: ImmutableList<CoinWithMarketDataUiItem>,
    val lastUpdate: String
)

data class FavouriteCoinUiData(
    val coins: ImmutableList<CoinWithMarketDataUiItem>,
    val lastUpdate: String
)

data class CoinsListState(
    val topCoinsList: ImmutableList<CoinWithMarketDataUiItem>,
    val trendingCoinsList: ImmutableList<CoinUiItem>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

data class FavouriteCoinsState(
    val favouriteCoinsList: ImmutableList<CoinWithMarketDataUiItem>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

sealed interface CoinsListUiState {
    object Idle : CoinsListUiState
    data class Refreshing(val isAutomaticRefresh: Boolean) : CoinsListUiState
    data class Error(val message: String) : CoinsListUiState
}