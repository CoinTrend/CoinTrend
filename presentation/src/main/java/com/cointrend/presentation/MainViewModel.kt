package com.cointrend.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.app.IsPlayStoreReviewAlertAlreadyShownUseCase
import com.cointrend.domain.features.app.SetPlayStoreReviewAlertShownUseCase
import com.cointrend.domain.features.settings.GetSettingsConfigurationFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isPlayStoreReviewAlertAlreadyShownUseCase: IsPlayStoreReviewAlertAlreadyShownUseCase,
    private val setPlayStoreReviewAlertShownUseCase: SetPlayStoreReviewAlertShownUseCase,
    private val getSettingsConfigurationFlowUseCase: GetSettingsConfigurationFlowUseCase,
) : ViewModel() {

    var shouldShowPlayStoreReviewAlert by mutableStateOf(false)
        private set

    init {
        // The GetSettingsConfigurationFlowUseCase is called in a blocking way so that the
        // GlobalSettingsConfiguration used throughout the App is initialised with the stored settings,
        // and the initial network calls are made with the correct settings.
        // TODO: check if there are better ways to enrsure the settings initialization without the runBlocking call.
        runBlocking {
            val initialSettings = getSettingsConfigurationFlowUseCase().catch {
                Timber.e("Initial SettingsConfiguration ERROR: $it")
            }.firstOrNull()

            Timber.d("Initial SettingsConfiguration SUCCESS: $initialSettings")
        }
    }


    fun init() {
        viewModelScope.launch {
            shouldShowPlayStoreReviewAlert = !isPlayStoreReviewAlertAlreadyShownUseCase()
        }
    }

    fun onPlayStoreReviewAlertShown() {
        shouldShowPlayStoreReviewAlert = false

        viewModelScope.launch {
            setPlayStoreReviewAlertShownUseCase()
        }
    }

}