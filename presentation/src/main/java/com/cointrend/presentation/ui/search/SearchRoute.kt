package com.cointrend.presentation.ui.search

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cointrend.presentation.models.CoinUiItem

@Composable
fun SearchRoute(
    onNavigateToCoinDetail: (CoinUiItem) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    SearchContent(
        searchTextFieldState = viewModel.searchTextFieldState,
        searchUiState = viewModel.searchUiState,
        onSearchValueChanged = viewModel::onSearchValueChanged,
        onClearSearchClick = { viewModel.onSearchValueChanged("") },
        onRetryClick = viewModel::onRetryClick,
        onNavigateToCoinDetail = onNavigateToCoinDetail
    )
}