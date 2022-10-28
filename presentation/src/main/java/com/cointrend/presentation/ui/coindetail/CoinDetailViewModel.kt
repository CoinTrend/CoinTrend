package com.cointrend.presentation.ui.coindetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.favouritecoins.AddFavouriteCoinUseCase
import com.cointrend.domain.features.favouritecoins.GetFavouriteCoinsIdsUseCase
import com.cointrend.domain.features.favouritecoins.RemoveFavouriteCoinUseCase
import com.cointrend.domain.features.marketchart.GetMarketChartDataUseCase
import com.cointrend.domain.features.marketdata.GetCoinMarketDataFlowUseCase
import com.cointrend.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.CoinMarketData
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.models.*
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.haan.resultat.Resultat
import fr.haan.resultat.onFailure
import fr.haan.resultat.onLoading
import fr.haan.resultat.onSuccess
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    settingsConfiguration: SettingsConfiguration,
    private val getCoinMarketDataFlowUseCase: GetCoinMarketDataFlowUseCase,
    private val getMarketChartDataUseCase: GetMarketChartDataUseCase,
    private val getFavouriteCoinsIdsUseCase: GetFavouriteCoinsIdsUseCase,
    private val addFavouriteCoinUseCase: AddFavouriteCoinUseCase,
    private val removeFavouriteCoinUseCase: RemoveFavouriteCoinUseCase,
    private val mapper: UiMapper,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val coinDetailMainUiData: CoinUiItem
        get() = savedStateHandle[COIN_DETAIL_PARAMETER]
            ?: throw RuntimeException("CoinDetailViewModel must be provided with $COIN_DETAIL_PARAMETER parameter.")

    var state by mutableStateOf(
        CoinDetailState(
            coinMarketDataState = CoinMarketDataState.Success(
                data = CoinMarketUiData(
                    price = "",
                    marketDataList = persistentListOf()
                )
            ),
            coinMarketChartState = CoinMarketChartState.Loading,
            isMarketChartVisible = false,
            timeRangeOptions = TimeRangeUi.values().toList().toImmutableList(),
            timeRangeSelected = TimeRangeUi.values().first(),
            isFavourite = false
        )
    )
    private set

    private var getMatketChartJob: Job? = null


    init {
        checkIfCoinIsFavourite()
        getCoinMarketData()

        val defaultTimeRange = settingsConfiguration.getDefaultTimeRange()
        state = state.copy(
            timeRangeSelected = TimeRangeUi.values().first { it.timeRange == defaultTimeRange }
        )

        getMarketChartData()
    }

    private fun getCoinMarketData() {
        getCoinMarketDataFlowUseCase(
            inputParams = CoinMarketDataInputParams(
                coinId = coinDetailMainUiData.id
            )
        ).onEach {
            handleGetCoinMarketDataState(it)
        }.catch {
            handleGetCoinMarketDataState(Resultat.failure(it))
        }.launchIn(viewModelScope)
    }

    private fun handleGetCoinMarketDataState(result: Resultat<CoinMarketData>) {
        var newState: CoinMarketDataState = state.coinMarketDataState

        result.onSuccess {
            val uiData = mapper.mapCoinMarketUiData(it)

            newState = CoinMarketDataState.Success(uiData)
        }.onFailure {
            newState = CoinMarketDataState.Error(message = mapper.mapErrorToUiMessage(it))
        }.onLoading {
            newState = CoinMarketDataState.Loading
        }

        state = state.copy(
            coinMarketDataState = newState
        )
    }

    fun onTimeRangeSelected(timeRangeUi: TimeRangeUi) {
        if (timeRangeUi != state.timeRangeSelected) {
            state = state.copy(
                timeRangeSelected = timeRangeUi
            )
            getMarketChartData()
        }
    }

    private fun getMarketChartData() {
        val timeRange = state.timeRangeSelected.timeRange

        // Old job to cancel to avoid illegal concurrent states
        val oldJob = getMatketChartJob

        getMatketChartJob = viewModelScope.launch {
            oldJob?.cancelAndJoin()

            val loadingJob = async {
                delay(250)
                state = state.copy(
                    coinMarketChartState = CoinMarketChartState.Loading
                )
            }

            val getDataJob = async {
                getMarketChartDataUseCase(
                    coinId = coinDetailMainUiData.id,
                    timeRange = timeRange
                )
            }

            val result = getDataJob.await()
            loadingJob.cancelAndJoin()

            var newState = state.coinMarketChartState

            result.onSuccess {
                newState = CoinMarketChartState.Success(
                    data = withContext(dispatcherProvider.default) {
                        mapper.mapMarketChartUiData(it)
                    }
                )
            }.onFailure {
                newState = CoinMarketChartState.Error(message = mapper.mapErrorToUiMessage(it))
            }

            delay(200L)
            state = state.copy(
                coinMarketChartState = newState
            )

            getMatketChartJob = null
        }
    }

    fun showMarketChart() {
        if (!state.isMarketChartVisible) {
            state = state.copy(
                isMarketChartVisible = true
            )
        }
    }

    fun onMarketChartErrorRetry() {
        onErrorRetry()
    }

    fun onMarketDataErrorRetry() {
        onErrorRetry()
    }

    private fun onErrorRetry() {
        if (state.coinMarketChartState is CoinMarketChartState.Error) {
            getMarketChartData()
        }

        if (state.coinMarketDataState is CoinMarketDataState.Error) {
            getCoinMarketData()
        }
    }

    fun onFavouriteButtonClick() {
        viewModelScope.launch {
            if (state.isFavourite) {
                removeFavouriteCoinUseCase(
                    coinId = coinDetailMainUiData.id
                )
            } else {
                addFavouriteCoinUseCase(
                    coin = mapper.mapCoin(coinUiItem = coinDetailMainUiData)
                )
            }

            checkIfCoinIsFavourite()
        }

    }

    private fun checkIfCoinIsFavourite() {
        viewModelScope.launch {
            getFavouriteCoinsIdsUseCase().onSuccess {
                val isFavourite = it.contains(coinDetailMainUiData.id)

                state = state.copy(
                    isFavourite = isFavourite
                )
            }.onFailure {
                Timber.e("Cannot retrieve coin favourite state: $it")
            }
        }
    }

}