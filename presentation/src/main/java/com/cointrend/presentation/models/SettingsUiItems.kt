package com.cointrend.presentation.models

import kotlinx.collections.immutable.ImmutableList


data class SettingsState(
    val priceChangePeriodOptions: ImmutableList<SettingsPriceChangePeriodUi>,
    val selectedPriceChangePeriod: SettingsPriceChangePeriodUi?
)
