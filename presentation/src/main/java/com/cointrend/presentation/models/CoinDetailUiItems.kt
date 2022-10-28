package com.cointrend.presentation.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinUiItem(
    val id: String,
    val name: String,
    val symbol: String,
    val imageUrl: String,
    val marketCapRank: String
) : Parcelable

data class MarketChartUiData(
    val chartData: ImmutableList<DataPoint>,
    val startPrice: String,
    val startPriceDate: String,
    val lowestPrice: String,
    val lowestPriceDate: String,
    val highestPrice: String,
    val highestPriceDate: String,
    val priceChangePercentage: String,
    val trendColor: Color
)

data class CoinMarketUiData(
    val price: String,
    val marketDataList: ImmutableList<Pair<String, String>>
)

data class CoinDetailState(
    val coinMarketDataState: CoinMarketDataState,
    val coinMarketChartState: CoinMarketChartState,
    val isMarketChartVisible: Boolean,
    val timeRangeOptions: ImmutableList<TimeRangeUi>,
    val timeRangeSelected: TimeRangeUi,
    val isFavourite: Boolean
)

sealed interface CoinMarketChartState {
    object Loading : CoinMarketChartState
    data class Error(val message: String) : CoinMarketChartState
    data class Success(val data: MarketChartUiData) : CoinMarketChartState
}

sealed interface CoinMarketDataState {
    object Loading : CoinMarketDataState
    data class Error(val message: String) : CoinMarketDataState
    data class Success(val data: CoinMarketUiData) : CoinMarketDataState
}
