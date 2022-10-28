package com.cointrend.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.cointrend.data.db.room.CoinsDatabase
import com.cointrend.data.features.favouritecoins.local.FavouriteCoinsDao
import com.cointrend.data.features.marketdata.local.CoinsMarketDataDao
import com.cointrend.data.features.topcoins.local.TopCoinsDao
import com.cointrend.data.features.trendingcoins.local.TrendingCoinsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")


@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CoinsDatabase {
        return Room.databaseBuilder(
            context,
            CoinsDatabase::class.java,
            "coins.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTopCoinsDao(
        coinsDatabase: CoinsDatabase
    ): TopCoinsDao = coinsDatabase.topCoinsDao()

    @Provides
    @Singleton
    fun provideCoinsMarketDataDao(
        coinsDatabase: CoinsDatabase
    ): CoinsMarketDataDao = coinsDatabase.coinsMarketDataDao()

    @Provides
    @Singleton
    fun provideTrendingCoinsDao(
        coinsDatabase: CoinsDatabase
    ): TrendingCoinsDao = coinsDatabase.trendingCoinsDao()

    @Provides
    @Singleton
    fun provideFavouriteCoinsDao(
        coinsDatabase: CoinsDatabase
    ): FavouriteCoinsDao = coinsDatabase.favouriteCoinsDao()

}