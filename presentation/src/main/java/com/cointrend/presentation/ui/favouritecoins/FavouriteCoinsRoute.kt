package com.cointrend.presentation.ui.favouritecoins

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.cointrend.presentation.models.CoinUiItem

@Composable
fun FavouriteCoinsRoute(
    onNavigateToCoinDetail: (CoinUiItem) -> Unit,
    viewModel: FavouriteCoinsViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = viewModel) {
        viewModel.init()

        onDispose {
            viewModel.onDispose()
        }
    }

    FavouriteCoinsContent(
        uiState = viewModel.state,
        onNavigateToCoinDetail = onNavigateToCoinDetail,
        onSwipeRefresh = viewModel::onSwipeRefresh,
        onRetryClick = viewModel::onRetryClick,
        onCoinPositionReordered = viewModel::onCoinPositionReordered
    )
}