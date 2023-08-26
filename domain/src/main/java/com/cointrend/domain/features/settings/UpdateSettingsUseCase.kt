package com.cointrend.domain.features.settings

import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsConfiguration: GlobalSettingsConfiguration
) {

    operator fun invoke(
        currency: Currency,
        ordering: Ordering,
    ) {
        settingsConfiguration.apply {
            this.currency = currency
            this.ordering = ordering
        }

    }

}