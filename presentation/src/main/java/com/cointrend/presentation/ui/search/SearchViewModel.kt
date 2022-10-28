package com.cointrend.presentation.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.search.SearchUseCase
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.models.SearchTextFieldState
import com.cointrend.presentation.models.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getSearchUseCase: SearchUseCase,
    private val mapper: UiMapper
) : ViewModel() {

    var searchTextFieldState by mutableStateOf(
        SearchTextFieldState(
            text = "",
            placeholderText = "Search coins",
            isTrailingIconVisible = false
        )
    )
    private set

    var searchUiState by mutableStateOf<SearchUiState>(
        SearchUiState.Success(persistentListOf())
    )
    private set

    private var searchJob: Job? = null


    fun onSearchValueChanged(text: String) {
        searchJob?.cancel()
        searchJob = null
        searchUiState = SearchUiState.Loading

        searchTextFieldState = if (text.isBlank()) {
            searchUiState = SearchUiState.Success(persistentListOf())

            SearchTextFieldState(
                text = "",
                placeholderText = "Search coins",
                isTrailingIconVisible = false
            )
        } else {
            searchJob = viewModelScope.launch {
                delay(1000L)

                getSearchUseCase(query = text).onSuccess {
                    val mappedCoins = mapper.mapCoinUiItemsList(coins = it)
                    searchUiState = SearchUiState.Success(coins = mappedCoins)
                }.onFailure {
                    searchUiState = SearchUiState.Error(message = mapper.mapErrorToUiMessage(it))
                }

                searchJob = null
            }

            SearchTextFieldState(
                text = text,
                placeholderText = "",
                isTrailingIconVisible = true
            )
        }
    }

    fun onRetryClick() {
        onSearchValueChanged(text = searchTextFieldState.text)
    }

}