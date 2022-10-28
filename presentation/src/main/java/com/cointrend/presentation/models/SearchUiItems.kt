package com.cointrend.presentation.models

import kotlinx.collections.immutable.ImmutableList

data class SearchTextFieldState(
    val text: String,
    val placeholderText: String,
    val isTrailingIconVisible: Boolean
)

sealed interface SearchUiState {
    data class Success(val coins: ImmutableList<CoinUiItem>) : SearchUiState
    object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
}