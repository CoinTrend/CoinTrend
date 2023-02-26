package com.cointrend.presentation.models

import kotlinx.collections.immutable.ImmutableList

data class TopCoinUiData(
    val topCoins: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val lastUpdate: String
)

data class FavouriteCoinUiData(
    val coins: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val lastUpdate: String
)

data class CoinsListState(
    val topCoinsList: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val trendingCoinsList: ImmutableList<CoinUiItem>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

data class FavouriteCoinsState(
    val favouriteCoinsList: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

sealed interface CoinsListUiState {
    object Idle : CoinsListUiState
    data class Refreshing(val isAutomaticRefresh: Boolean) : CoinsListUiState
    data class Error(val message: String) : CoinsListUiState
}