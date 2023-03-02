package com.cointrend.presentation.mappers

import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import com.cointrend.presentation.mocks.expectedCoinWithMarketData
import com.cointrend.presentation.mocks.expectedCoinWithMissingMarketData
import com.cointrend.presentation.models.CoinWithMarketDataUiItem
import com.cointrend.presentation.models.CoinWithShimmeringMarketDataUiItem
import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.CurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.NumberFormatter
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import java.time.format.DateTimeFormatter

class UiMapperTest {

    private lateinit var cut: UiMapper

    @MockK
    private lateinit var currencyFormatter: CurrencyFormatter
    @MockK
    private lateinit var numberFormatter: NumberFormatter
    @MockK
    private lateinit var dateTimeFormatter: DateTimeFormatter


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        cut = UiMapper(
            currencyFormatter = currencyFormatter,
            numberFormatter = numberFormatter,
            dateTimeFormatter = dateTimeFormatter,
            dateOnlyFormatter = dateTimeFormatter,
            timeOnlyFormatter = dateTimeFormatter,
            settingsConfiguration = SettingsConfiguration(
                currency = Currency.USD,
                ordering = Ordering.MarketCapDesc,
                defaultTimeRange = TimeRange.Week
            )
        )
    }


    @Test
    fun `mapCoinWithMarketDataUiItemsList maps coins with CoinMarketData as CoinWithMarketDataUiItem and maps coins with missing CoinWithMarketData as CoinWithShimmeringMarketDataUiItem`() {
        every { currencyFormatter.format(any(), any()) } returns ""
        every { numberFormatter.formatToPercentage(any()) } returns ""
        every { dateTimeFormatter.format(any()) } returns ""

        val coinsWithMarketDataList = listOf(
            expectedCoinWithMarketData,
            expectedCoinWithMissingMarketData
        )

        val result = cut.mapCoinWithMarketDataUiItemsList(
            coinsList = coinsWithMarketDataList
        )

        expectThat(result[0]).isA<CoinWithMarketDataUiItem>()
        expectThat(result[1]).isA<CoinWithShimmeringMarketDataUiItem>()
    }

}