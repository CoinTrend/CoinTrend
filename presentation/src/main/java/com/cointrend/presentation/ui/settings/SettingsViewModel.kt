package com.cointrend.presentation.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.settings.GetSettingsConfigurationFlowUseCase
import com.cointrend.domain.features.settings.SetDefaultTimeRangeUseCase
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.presentation.mappers.SettingsUiMapper
import com.cointrend.presentation.models.SettingsPriceChangePeriodUi
import com.cointrend.presentation.models.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsConfigurationFlowUseCase: GetSettingsConfigurationFlowUseCase,
    private val setDefaultTimeRangeUseCase: SetDefaultTimeRangeUseCase,
    private val mapper: SettingsUiMapper
) : ViewModel() {

    var state by mutableStateOf(
        SettingsState(
            priceChangePeriodOptions = SettingsPriceChangePeriodUi.entries.toImmutableList(),
            selectedPriceChangePeriod = null
        )
    )
    private set

    private var settingsConfigurationFlowJob: Job? = null


    fun init() {
        initSettingsFlowCollection()
    }

    fun onDispose() {
        cancelSettingsConfigurationFlowCollection()
    }

    private fun initSettingsFlowCollection() {
        cancelSettingsConfigurationFlowCollection()

        // Single source of truth of the settings configuration
        settingsConfigurationFlowJob = getSettingsConfigurationFlowUseCase()
            .onEach {
                handleGetSettingsConfigurationState(Result.success(it))
            }.catch {
                handleGetSettingsConfigurationState(Result.failure(it))

                // After this catch the flow is interrupted and it must be collected
                // again to obtain new data.
                cancelSettingsConfigurationFlowCollection()
            }.launchIn(viewModelScope)
    }

    private fun handleGetSettingsConfigurationState(result: Result<SettingsConfiguration>) {
        result.onSuccess {
            Timber.d("getSettingsConfigurationFlowUseCase SUCCESS: $it")

            val priceChangePeriodUi = mapper.mapPriceChangePeriodUi(timeRange = it.defaultTimeRange)

            state = state.copy(
                selectedPriceChangePeriod = priceChangePeriodUi
            )
        }.onFailure {
            Timber.e("getSettingsConfigurationFlowUseCase ERROR: $it")

        }
    }


    fun onPriceChangePeriodSelected(priceChangePeriodUi: SettingsPriceChangePeriodUi) {
        if (priceChangePeriodUi != state.selectedPriceChangePeriod) {
            viewModelScope.launch {
                val result = setDefaultTimeRangeUseCase(timeRange = priceChangePeriodUi.timeRange)

                result.onSuccess {
                    Timber.d("setDefaultTimeRangeUseCase() to ${priceChangePeriodUi.timeRange} SUCCESS")
                }.onFailure {
                    Timber.e("setDefaultTimeRangeUseCase() to ${priceChangePeriodUi.timeRange} ERROR: $it")
                }
            }
        }
    }

    private fun cancelSettingsConfigurationFlowCollection() {
        settingsConfigurationFlowJob?.cancel()
        settingsConfigurationFlowJob = null
    }

}