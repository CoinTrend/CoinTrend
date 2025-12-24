package com.cointrend.presentation.ui.coindetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.cointrend.presentation.models.COIN_DETAIL_PARAMETER
import com.cointrend.presentation.models.CoinUiItem

@Composable
fun CoinDetailRoute(
    coinDetailMainUiData: CoinUiItem,
    onNavigateBack: () -> Unit,
    viewModel: CoinDetailViewModel = hiltViewModel()
) {
    // Initialize the ViewModel with the coin data
    LaunchedEffect(coinDetailMainUiData) {
        viewModel.setCoinDetailData(coinDetailMainUiData)
    }

    CoinDetailContent(
        coinDetailMainUiData = coinDetailMainUiData,
        marketDataState = viewModel.state.coinMarketDataState,
        marketChartState = viewModel.state.coinMarketChartState,
        isFavourite = viewModel.state.isFavourite,
        onNavigateBack = onNavigateBack,
        onFavouriteClick = viewModel::onFavouriteButtonClick,
        onTimeIntervalClick = viewModel::onTimeRangeSelected,
        onRetryMarketData = viewModel::getCoinMarketData,
        onRetryMarketChart = viewModel::getCoinMarketChart
    )
}