package com.cointrend.presentation.di

import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.CurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.LocalizedCurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.LocalizedNumberFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.NumberFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateAndTimeFormatter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DateOnlyFormatter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TimeOnlyFormatter

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    @DateAndTimeFormatter
    fun provideDateTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    }

    @Provides
    @Singleton
    @DateOnlyFormatter
    fun provideDateFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.US)
    }

    @Provides
    @Singleton
    @TimeOnlyFormatter
    fun provideTimeFormatter(): DateTimeFormatter {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    }


    @Provides
    @Singleton
    fun provideCurrencyFormatter(): CurrencyFormatter {
        return LocalizedCurrencyFormatter(
            currency = Currency.getInstance(Locale.US),
            minimumFractionDigits = 2,
            maximumFractionDigits = 6
        )
    }

    @Provides
    @Singleton
    fun provideNumberFormatter(): NumberFormatter {
        return LocalizedNumberFormatter(
            locale = Locale.getDefault(),
            showAlwaysSign = true,
            maximumFractionDigits = 2,
            minimumFractionDigits = 2,
            divideValueBy100 = true
        )
    }

}