package com.cointrend.presentation.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    onNavigateToAbout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = viewModel) {
        viewModel.init()

        onDispose {
            viewModel.onDispose()
        }
    }

    SettingsContent(
        uiState = viewModel.state,
        onNavigateToAbout = onNavigateToAbout,
        onPriceChangePeriodSelected = viewModel::onPriceChangePeriodSelected
    )
}