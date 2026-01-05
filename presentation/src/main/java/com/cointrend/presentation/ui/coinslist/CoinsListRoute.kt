package com.cointrend.presentation.ui.coinslist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.cointrend.presentation.models.CoinUiItem

@Composable
fun CoinsListRoute(
    onNavigateToCoinDetail: (CoinUiItem) -> Unit,
    viewModel: CoinsListViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = viewModel) {
        viewModel.init()

        onDispose {
            viewModel.onDispose()
        }
    }

    CoinsListContent(
        uiState = viewModel.state,
        onNavigateToCoinDetail = onNavigateToCoinDetail,
        onSwipeRefresh = viewModel::onSwipeRefresh,
        onRetryClick = viewModel::onRetryClick
    )
}