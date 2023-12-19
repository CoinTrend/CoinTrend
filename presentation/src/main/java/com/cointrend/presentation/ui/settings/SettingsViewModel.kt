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
import com.cointrend.presentation.models.SettingsState
import com.cointrend.presentation.models.TimeRangeUi
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
            timeRangeOptions = TimeRangeUi.values().toList().toImmutableList(),
            selectedTimeRange = null
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

            val timeRangeUi = mapper.mapTimeRangeUi(timeRange = it.defaultTimeRange)

            state = state.copy(
                selectedTimeRange = timeRangeUi
            )
        }.onFailure {
            Timber.e("getSettingsConfigurationFlowUseCase ERROR: $it")

        }
    }


    fun onTimeRangeSelected(timeRangeUi: TimeRangeUi) {
        if (timeRangeUi != state.selectedTimeRange) {
            viewModelScope.launch {
                val result = setDefaultTimeRangeUseCase(timeRange = timeRangeUi.timeRange)

                result.onSuccess {
                    Timber.d("setDefaultTimeRangeUseCase() to ${timeRangeUi.timeRange} SUCCESS")
                }.onFailure {
                    Timber.e("setDefaultTimeRangeUseCase() to ${timeRangeUi.timeRange} ERROR: $it")
                }
            }
        }
    }

    private fun cancelSettingsConfigurationFlowCollection() {
        settingsConfigurationFlowJob?.cancel()
        settingsConfigurationFlowJob = null
    }

}