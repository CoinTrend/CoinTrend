package com.cointrend.domain.features.settings

import com.cointrend.domain.models.LaunchingPage
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SetLaunchingPageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getSettingsConfigurationFlowUseCase: GetSettingsConfigurationFlowUseCase,
) {

    suspend operator fun invoke(launchingPage:  LaunchingPage): Result<Unit> {
        val result = settingsRepository.setDefaultLaunchingPage(launchingPage = launchingPage)

        return if (result.isSuccess) {
            try {
                // Before returning, getSettingsConfigurationFlowUseCase() is called so that
                // the GlobalSettingsConfiguration is updated immediately with the new settings.
                getSettingsConfigurationFlowUseCase().firstOrNull()


                result
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else result
    }

}