package com.cointrend.presentation.mappers

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.TimeRange
import com.cointrend.presentation.BuildConfig
import com.cointrend.presentation.models.SettingsCurrentCurrencyUi
import com.cointrend.presentation.models.SettingsPriceChangePeriodUi
import timber.log.Timber
import javax.inject.Inject

class SettingsUiMapper @Inject constructor() {

    fun mapPriceChangePeriodUi(timeRange: TimeRange): SettingsPriceChangePeriodUi {
        return when(timeRange) {
            TimeRange.Day -> SettingsPriceChangePeriodUi.Day
            TimeRange.Week -> SettingsPriceChangePeriodUi.Week
            TimeRange.Month -> SettingsPriceChangePeriodUi.Month
            TimeRange.SixMonths -> SettingsPriceChangePeriodUi.SixMonths
            TimeRange.Year -> SettingsPriceChangePeriodUi.Year
            TimeRange.Max -> {
                if (BuildConfig.DEBUG) {
                    throw IllegalStateException("mapPriceChangePeriodUi ERROR: timeRange: $timeRange cannot be mapped to a SettingsPriceChangePeriodUi. This value should never be reached.")
                } else {
                    Timber.e("mapPriceChangePeriodUi ERROR: timeRange: $timeRange cannot be mapped to a SettingsPriceChangePeriodUi. Returning default value SettingsPriceChangePeriodUi.Year")
                    SettingsPriceChangePeriodUi.Year
                }
            }
        }
    }

    fun mapCurrentCurrencyUi(currency: Currency): SettingsCurrentCurrencyUi {
        return when(currency) {
            Currency.USD -> SettingsCurrentCurrencyUi.USD
            Currency.EUR -> SettingsCurrentCurrencyUi.EUR
            Currency.GBP -> SettingsCurrentCurrencyUi.GBP
            Currency.BTC -> SettingsCurrentCurrencyUi.BTC
        }
    }

}