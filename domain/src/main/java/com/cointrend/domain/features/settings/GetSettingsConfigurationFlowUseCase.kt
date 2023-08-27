package com.cointrend.domain.features.settings

import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.features.settings.models.toSettingsConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSettingsConfigurationFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val globalSettingsConfiguration: GlobalSettingsConfiguration,
    private val updateGlobalSettingsConfigurationUseCase: UpdateGlobalSettingsConfigurationUseCase
) {

    operator fun invoke(): Flow<SettingsConfiguration> {
        return settingsRepository.getSettingsConfigurationFlow().map {
            // If SettingsConfiguration is null it means that there is no settings saved on the device,
            // so the default settings configuration from GlobalSettingsConfiguration is returned.
            // Otherwise, the SettingsConfiguration retrieved is used to update the GlobalSettingsConfiguration
            // and then is returned itself.

            it?.let {
                updateGlobalSettingsConfigurationUseCase(settingsConfiguration = it)
                globalSettingsConfiguration.toSettingsConfiguration()

            } ?: globalSettingsConfiguration.toSettingsConfiguration()
        }
    }

}