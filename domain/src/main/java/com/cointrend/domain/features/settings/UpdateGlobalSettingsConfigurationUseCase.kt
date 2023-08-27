package com.cointrend.domain.features.settings

import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import javax.inject.Inject

class UpdateGlobalSettingsConfigurationUseCase @Inject constructor(
    private val globalSettingsConfiguration: GlobalSettingsConfiguration
) {

    // This method is internal as the update of GlobalSettingsConfiguration must only happen
    // inside the domain layer
    internal operator fun invoke(settingsConfiguration: SettingsConfiguration) {
        with(globalSettingsConfiguration) {
            ordering = settingsConfiguration.ordering
            defaultTimeRange = settingsConfiguration.defaultTimeRange
            currency = settingsConfiguration.currency
        }
    }

}