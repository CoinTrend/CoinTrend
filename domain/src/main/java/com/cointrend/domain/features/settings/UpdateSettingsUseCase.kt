package com.cointrend.domain.features.settings

import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsConfiguration: SettingsConfiguration
) {

    operator fun invoke(
        currency: Currency,
        ordering: Ordering
    ) {
        settingsConfiguration.apply {
            this.currency = currency
            this.ordering = ordering
        }

    }

}