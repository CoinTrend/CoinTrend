package com.cointrend.di

import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DefaultDispatcherProvider
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import com.github.davidepanidev.kotlinextensions.utils.serialization.SerializationManager
import com.github.davidepanidev.kotlinextensions.utils.serialization.gson.GsonSerializationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun provideSettingsConfiguration(): SettingsConfiguration {
        return SettingsConfiguration(
            currency = Currency.USD,
            ordering = Ordering.MarketCapDesc,
            defaultTimeRange = TimeRange.Week
        )
    }

    @Provides
    @Singleton
    fun provideSerializationManager(): SerializationManager {
        return GsonSerializationManager()
    }

    @Provides
    @Singleton
    fun providerDispatcherProvider(): DispatcherProvider {
        return DefaultDispatcherProvider()
    }

}