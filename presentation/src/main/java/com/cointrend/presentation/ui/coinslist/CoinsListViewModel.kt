package com.cointrend.presentation.ui.coinslist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.settings.UpdateSettingsUseCase
import com.cointrend.domain.features.topcoins.GetTopCoinsFlowUseCase
import com.cointrend.domain.features.topcoins.RefreshTopCoinsUseCase
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.trendingcoins.GetTrendingCoinsFlowUseCase
import com.cointrend.domain.features.trendingcoins.RefreshTrendingCoinsUseCase
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.models.CoinsListState
import com.cointrend.presentation.models.CoinsListUiState
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.haan.resultat.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoinsListViewModel @Inject constructor(
    private val getTopCoinsFlowUseCase: GetTopCoinsFlowUseCase,
    private val refreshTopCoinsUseCase: RefreshTopCoinsUseCase,
    private val getTrendingCoinsFlowUseCase: GetTrendingCoinsFlowUseCase,
    private val refreshTrendingCoinsUseCase: RefreshTrendingCoinsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val mapper: UiMapper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    var state by mutableStateOf(
        CoinsListState(
            topCoinsList = persistentListOf(),
            trendingCoinsList = persistentListOf(),
            lastUpdateDate = "",
            state = CoinsListUiState.Idle
        )
    )
    private set

    private var isTopCoinsFlowInterrupted = false
    private var isTrendingCoinsFlowInterrupted = false


    init {
        initTrendingCoinsFlowCollection()
        initTopCoinsFlowCollection()
    }

    /**
     * Top Coins
     */
    private fun initTopCoinsFlowCollection() {
        // Single source of truth of the top coins list
        getTopCoinsFlowUseCase(Unit)
            .onEach {
                isTopCoinsFlowInterrupted = false
                handleGetTopCoinsState(it)
            }.catch {
                // After this catch the flow is interrupted and it must be collected
                // again to obtain new data. The handleRefresh() method handles this situation.
                isTopCoinsFlowInterrupted = true
                handleGetTopCoinsState(Resultat.failure(it))
            }.launchIn(viewModelScope)
    }

    private fun refreshTopCoins() {
        if (state.state is CoinsListUiState.Refreshing) {
            return
        }

        // If the flow is interrupted it is reinitialized,
        // otherwise data is refreshed through the corresponding UseCase
        if (isTopCoinsFlowInterrupted) {
            initTopCoinsFlowCollection()
        } else {
            flow {
                emit(Resultat.loading())

                // The onSuccess Result is of Unit type as the top coins list must
                // always come from the single source of truth provided by the getTopCoinsFlowUseCase
                emit(refreshTopCoinsUseCase(Unit).toResultat())
            }.onEach {
                handleRefreshTopCoinsState(it)
            }.launchIn(viewModelScope)
        }
    }

    private suspend fun handleGetTopCoinsState(result: Resultat<TopCoinsData>) {
        result.onSuccess {
            val uiData = withContext(dispatcherProvider.default) {
                mapper.mapTopCoinUiData(it)
            }

            state = state.copy(
                topCoinsList = uiData.topCoins,
                lastUpdateDate = uiData.lastUpdate,
                state = CoinsListUiState.Idle
            )
        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {
            state = state.copy(
                state = CoinsListUiState.Refreshing(isAutomaticRefresh = true)
            )
        }
    }

    private fun handleRefreshTopCoinsState(result: Resultat<Unit>) {
        // The onSuccess result just sets the state to Idle to replace the Refresh state as the
        // refresh operation is terminated. The refreshed coins will come from the single source of truth
        // provided by the getTopCoinsFlowUseCase
        result.onSuccess {
            state = state.copy(state = CoinsListUiState.Idle)
        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {
            state = state.copy(
                state = CoinsListUiState.Refreshing(isAutomaticRefresh = false)
            )
        }
    }


    /**
     * Trending Coins
     */
    private fun initTrendingCoinsFlowCollection() {
        // Single source of truth of the trending coins list
        getTrendingCoinsFlowUseCase(Unit)
            .onEach {
                isTrendingCoinsFlowInterrupted = false
                handleGetTrendingCoinsState(it)
            }.catch {
                // After this catch the flow is interrupted and it must be collected
                // again to obtain new data. The handleRefresh() method handles this situation.
                isTrendingCoinsFlowInterrupted = true
                handleGetTrendingCoinsState(Resultat.failure(it))
            }.launchIn(viewModelScope)
    }

    private fun refreshTrendingCoins() {
        if (isTrendingCoinsFlowInterrupted) {
            initTrendingCoinsFlowCollection()
        } else {
            flow {
                emit(Resultat.loading())

                // The onSuccess Result is of Unit type as the trending coins list must
                // always come from the single source of truth provided by the getTrendingCoinsFlowUseCase
                emit(refreshTrendingCoinsUseCase(Unit).toResultat())
            }.onEach {
                handleRefreshTrendingCoinsState(it)
            }.launchIn(viewModelScope)
        }
    }

    private fun handleGetTrendingCoinsState(result: Resultat<TrendingCoinsData>) {
        result.onSuccess {
            Timber.d("$it")
            val trendingCoins = mapper.mapCoinUiItemsList(
                trendingCoinsData = it
            )

            state = state.copy(
                trendingCoinsList = trendingCoins
            )
        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {

        }
    }

    private fun handleRefreshTrendingCoinsState(result: Resultat<Unit>) {
        // The onSuccess result just sets the state to Idle to replace the Refresh state as the
        // refresh operation is terminated. The refreshed coins will come from the single source of truth
        // provided by the getTrendingCoinsFlowUseCase
        result.onSuccess {

        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {

        }
    }


    fun onRetryClick() {
        refreshTopCoins()
        refreshTrendingCoins()
    }

    fun onSwipeRefresh() {
        refreshTopCoins()
        refreshTrendingCoins()
    }

    fun updateSettings() {
        updateSettingsUseCase(currency = Currency.BTC, ordering = Ordering.MarketCapDesc)
        refreshTopCoins()
    }

}