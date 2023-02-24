package com.cointrend.presentation.ui.favouritecoins

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.favouritecoins.GetFavouriteCoinsFlowUseCase
import com.cointrend.domain.features.favouritecoins.RefreshFavouriteCoinsUseCase
import com.cointrend.domain.features.favouritecoins.ReorderFavouriteCoinUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.models.FavouriteCoinsState
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.haan.resultat.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteCoinsViewModel @Inject constructor(
    private val getFavouriteCoinsFlowUseCase: GetFavouriteCoinsFlowUseCase,
    private val refreshFavouriteCoinsUseCase: RefreshFavouriteCoinsUseCase,
    private val reorderFavouriteCoinUseCase: ReorderFavouriteCoinUseCase,
    private val mapper: UiMapper
) : ViewModel() {

    var state by mutableStateOf(
        FavouriteCoinsState(
            favouriteCoinsList = persistentListOf(),
            lastUpdateDate = "",
            state = CoinsListUiState.Idle
        )
    )
    private set

    private var favouriteCoinsFlowJob: Job? = null
    private val isFavouriteCoinsFlowInterrupted: Boolean get() {
        return favouriteCoinsFlowJob == null
    }

    private var reorderCoinJob: Job? = null


    fun init() {
        Timber.d("init()")
        initFavouriteCoinsFlowCollection()
    }

    fun onDispose() {
        Timber.d("onDispose()")
        cancelFavouriteCoinsFlowCollection()
    }

    private fun initFavouriteCoinsFlowCollection() {
        cancelFavouriteCoinsFlowCollection()

        // Single source of truth of the top coins list
        favouriteCoinsFlowJob = getFavouriteCoinsFlowUseCase(Unit)
            .onEach {
                handleGetFavouriteCoinsState(it)
            }.catch {
                handleGetFavouriteCoinsState(Resultat.failure(it))

                // After this catch the flow is interrupted and it must be collected
                // again to obtain new data. The handleRefresh() method handles this situation.
                cancelFavouriteCoinsFlowCollection()
            }.launchIn(viewModelScope)
    }

    private fun cancelFavouriteCoinsFlowCollection() {
        favouriteCoinsFlowJob?.cancel()
        favouriteCoinsFlowJob = null
    }

    private fun refreshFavouriteCoins() {
        if (state.state is CoinsListUiState.Refreshing) {
            return
        }

        // If the flow is interrupted it is reinitialized,
        // otherwise data is refreshed through the corresponding UseCase
        if (isFavouriteCoinsFlowInterrupted) {
            initFavouriteCoinsFlowCollection()
        } else {
            flow {
                emit(Resultat.loading())

                // The onSuccess Result is of Unit type as the top coins list must
                // always come from the single source of truth provided by the getTopCoinsFlowUseCase
                emit(refreshFavouriteCoinsUseCase(Unit).toResultat())
            }.onEach {
                handleRefreshFavouriteCoinsState(it)
            }.launchIn(viewModelScope)
        }
    }

    private fun handleGetFavouriteCoinsState(result: Resultat<FavouriteCoinsData>) {
        result.onSuccess {
            val uiData = mapper.mapFavouriteCoinUiData(it)

            state = state.copy(
                favouriteCoinsList = uiData.coins,
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

    private fun handleRefreshFavouriteCoinsState(result: Resultat<Unit>) {
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


    fun onRetryClick() {
        refreshFavouriteCoins()
    }

    fun onSwipeRefresh() {
        refreshFavouriteCoins()
    }

    fun onCoinPositionReordered(coinId: String, fromIndex: Int, toIndex: Int) {
        Timber.d("onCoinPositionReordered -> coinId: $coinId, fromIndex: $fromIndex, toIndex: $toIndex")

        // The list is first reordered directly in this ViewModel to
        // ensure a smooth drag and drop animation, as updating the local data source
        // requires a latency period.
        val reorderedList = try {
             state.favouriteCoinsList.toMutableList().apply {
                this.add(toIndex, removeAt(fromIndex))
            }.toImmutableList()
        } catch (e: Exception) {
            Timber.e("onCoinPositionReordered ERROR: $e")
            return
        }


        state = state.copy(
            favouriteCoinsList = reorderedList
        )


        val oldJob = reorderCoinJob

        reorderCoinJob = viewModelScope.launch {
            // The old job is canceled to avoid multiple reorder operations
            // happening simultaneously.
            oldJob?.cancelAndJoin()
            // The delay is applied so that if many reorder operations are
            // performed quickly, only the last one actually modifies the local data source.
            // This prevents that an 'outdated' reorder event interferes with the current
            // list in the ViewModel state.
            delay(delayBeforeCallingReorderFavouriteCoinUseCase)

            reorderFavouriteCoinUseCase(
                coinId = coinId,
                toIndex = toIndex
            )

            reorderCoinJob = null
        }
    }


    companion object {
        const val delayBeforeCallingReorderFavouriteCoinUseCase = 500L
    }

}