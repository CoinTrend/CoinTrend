package com.cointrend.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cointrend.domain.features.app.IsPlayStoreReviewAlertAlreadyShownUseCase
import com.cointrend.domain.features.app.SetPlayStoreReviewAlertShownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isPlayStoreReviewAlertAlreadyShownUseCase: IsPlayStoreReviewAlertAlreadyShownUseCase,
    private val setPlayStoreReviewAlertShownUseCase: SetPlayStoreReviewAlertShownUseCase
) : ViewModel() {

    var shouldShowPlayStoreReviewAlert by mutableStateOf(false)
        private set


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