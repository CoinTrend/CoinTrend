package com.cointrend.domain.features.settings

import app.cash.turbine.test
import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.features.settings.models.toSettingsConfiguration
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotEqualTo


class GetSettingsConfigurationFlowUseCaseTest : BaseCoroutineTestWithTestDispatcherProvider(
    dispatcher = StandardTestDispatcher()
) {

    private lateinit var cut: GetSettingsConfigurationFlowUseCase

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var globalSettingsConfiguration: GlobalSettingsConfiguration

    // This class is not a mock for convenience but it is not the best way to test this class, as it also tests
    // UpdateGlobalSettingsConfigurationUseCase actual implementation.
    private lateinit var updateGlobalSettingsConfigurationUseCase: UpdateGlobalSettingsConfigurationUseCase


    @Before
    fun setUp() {
        globalSettingsConfiguration = GlobalSettingsConfiguration(
            currency = Currency.USD,
            ordering = Ordering.MarketCapDesc,
            defaultTimeRange = TimeRange.Week
        )

        updateGlobalSettingsConfigurationUseCase = UpdateGlobalSettingsConfigurationUseCase(
            globalSettingsConfiguration = globalSettingsConfiguration
        )

        MockKAnnotations.init(this)

        cut = GetSettingsConfigurationFlowUseCase(
            settingsRepository = settingsRepository,
            globalSettingsConfiguration = globalSettingsConfiguration,
            updateGlobalSettingsConfigurationUseCase = updateGlobalSettingsConfigurationUseCase
        )
    }

    @Test
    fun `when SettingsRepository return a null value, the GlobalSettingsConfiguration is returned`() = runTest {
        coEvery { settingsRepository.getSettingsConfigurationFlow() } returns flow {
            emit(null)
        }

        cut.invoke().test {
            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(globalSettingsConfiguration.toSettingsConfiguration())

            awaitComplete()
        }

        coVerify(exactly = 1) { settingsRepository.getSettingsConfigurationFlow() }
        confirmVerified(settingsRepository)
    }

    // This test tests both GetSettingsConfigurationFlowUseCase and UpdateGlobalSettingsConfigurationUseCase for convenience
    @Test
    fun `when SettingsRepository return a SettingsConfiguration value, the GlobalSettingsConfiguration is updated accordingly`() = runTest {
        val updatedSettingConfiguration = SettingsConfiguration(
            currency = Currency.BTC,
            ordering = Ordering.PriceChangeAsc,
            defaultTimeRange = TimeRange.Max
        )

        coEvery { settingsRepository.getSettingsConfigurationFlow() } returns flow {
            emit(updatedSettingConfiguration)
        }

        expectThat(globalSettingsConfiguration.toSettingsConfiguration()).isNotEqualTo(updatedSettingConfiguration)

        cut.invoke().test {
            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(updatedSettingConfiguration)
            expectThat(globalSettingsConfiguration.toSettingsConfiguration()).isEqualTo(updatedSettingConfiguration)

            awaitComplete()
        }

        coVerify(exactly = 1) { settingsRepository.getSettingsConfigurationFlow() }
        confirmVerified(settingsRepository)
    }

}