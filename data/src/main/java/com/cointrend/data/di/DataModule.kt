package com.cointrend.data.di

import com.cointrend.data.features.app.DataStoreAppRepository
import com.cointrend.data.features.favouritecoins.FavouriteCoinsLocalDataSource
import com.cointrend.data.features.favouritecoins.FavouriteCoinsRepositoryImpl
import com.cointrend.data.features.favouritecoins.local.RoomFavouriteCoinsLocalDataSource
import com.cointrend.data.features.marketchart.MarketChartRepositoryImpl
import com.cointrend.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cointrend.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.cointrend.data.features.marketdata.CoinMarketDataRepositoryImpl
import com.cointrend.data.features.marketdata.local.RoomCoinsMarketDataLocalDataSource
import com.cointrend.data.features.marketdata.remote.CoinGeckoCoinMarketDataRemoteDataSource
import com.cointrend.data.features.search.SearchRemoteDataSource
import com.cointrend.data.features.search.SearchRepositoryImpl
import com.cointrend.data.features.search.remote.CoinGeckoSearchRemoteDataSource
import com.cointrend.data.features.topcoins.TopCoinsLocalDataSource
import com.cointrend.data.features.topcoins.TopCoinsRemoteDataSource
import com.cointrend.data.features.topcoins.TopCoinsRepositoryImpl
import com.cointrend.data.features.topcoins.local.RoomTopCoinsLocalDataSource
import com.cointrend.data.features.topcoins.remote.CoinGeckoTopCoinsRemoteDataSource
import com.cointrend.data.features.trendingcoins.TrendingCoinsLocalDataSource
import com.cointrend.data.features.trendingcoins.TrendingCoinsRemoteDataSource
import com.cointrend.data.features.trendingcoins.TrendingCoinsRepositoryImpl
import com.cointrend.data.features.trendingcoins.local.RoomTrendingCoinsLocalDataSource
import com.cointrend.data.features.trendingcoins.remote.CoinGeckoTrendingCoinsRemoteDataSource
import com.cointrend.domain.features.app.AppRepository
import com.cointrend.domain.features.favouritecoins.FavouriteCoinsRepository
import com.cointrend.domain.features.marketchart.MarketChartRepository
import com.cointrend.domain.features.marketdata.CoinMarketDataRepository
import com.cointrend.domain.features.search.SearchRepository
import com.cointrend.domain.features.topcoins.TopCoinsRepository
import com.cointrend.domain.features.trendingcoins.TrendingCoinsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    // App
    @Binds
    abstract fun bindAppRepository(dataStoreAppRepository: DataStoreAppRepository): AppRepository


    // Top Coins
    @Binds
    abstract fun bindTopCoinsRepository(topCoinsRepositoryImpl: TopCoinsRepositoryImpl): TopCoinsRepository

    @Binds
    abstract fun bindTopCoinsRemoteDataSource(coinGeckoCoinsRepository: CoinGeckoTopCoinsRemoteDataSource): TopCoinsRemoteDataSource

    @Binds
    abstract fun bindTopCoinsLocalDataSource(roomTopCoinsLocalDataSource: RoomTopCoinsLocalDataSource): TopCoinsLocalDataSource


    // Market Chart
    @Binds
    abstract fun bindMarketChartRepository(marketChartRepositoryImpl: MarketChartRepositoryImpl): MarketChartRepository


    // Coin Market Data
    @Binds
    abstract fun bindCoinsMarketDataRepository(coinMarketDataRepositoryImpl: CoinMarketDataRepositoryImpl): CoinMarketDataRepository

    @Binds
    abstract fun bindCoinMarketDataRemoteDataSource(coinGeckoCoinMarketDataRemoteDataSource: CoinGeckoCoinMarketDataRemoteDataSource): CoinMarketDataRemoteDataSource

    @Binds
    abstract fun bindCoinMarketDataLocalDataSource(roomCoinsMarketDataLocalDataSource: RoomCoinsMarketDataLocalDataSource): CoinMarketDataLocalDataSource


    // Trending Coins
    @Binds
    abstract fun bindTrendingCoinsRepository(trendingCoinsRepositoryImpl: TrendingCoinsRepositoryImpl): TrendingCoinsRepository

    @Binds
    abstract fun bindTrendingCoinsRemoteDataSource(coinGeckoTrendingCoinsRemoteDataSource: CoinGeckoTrendingCoinsRemoteDataSource): TrendingCoinsRemoteDataSource

    @Binds
    abstract fun bindTrendingCoinsLocalDataSource(roomTrendingCoinsLocalDataSource: RoomTrendingCoinsLocalDataSource): TrendingCoinsLocalDataSource


    // Favourite Coins
    @Binds
    abstract fun bindFavouriteCoinsRepository(favouriteCoinsRepositoryImpl: FavouriteCoinsRepositoryImpl): FavouriteCoinsRepository

    @Binds
    abstract fun bindFavouriteCoinsLocalDataSource(roomFavouriteCoinsLocalDataSource: RoomFavouriteCoinsLocalDataSource): FavouriteCoinsLocalDataSource


    // Search
    @Binds
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindSearchRemoteDataSource(coinGeckoSearchRemoteDataSource: CoinGeckoSearchRemoteDataSource): SearchRemoteDataSource


}