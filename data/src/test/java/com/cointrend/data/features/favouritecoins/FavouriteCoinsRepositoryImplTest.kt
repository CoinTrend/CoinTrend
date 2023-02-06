package com.cointrend.data.features.favouritecoins

import com.cointrend.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cointrend.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteCoinsRepositoryImplTest : BaseCoroutineTestWithTestDispatcherProvider(
    dispatcher = StandardTestDispatcher()
) {

    private lateinit var cut: FavouriteCoinsRepositoryImpl

    @MockK
    private lateinit var localDataSource: FavouriteCoinsLocalDataSource

    @MockK
    private lateinit var marketDataRemoteDataSource: CoinMarketDataRemoteDataSource

    @MockK
    private lateinit var marketDataLocalDataSource: CoinMarketDataLocalDataSource


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        cut = FavouriteCoinsRepositoryImpl(
            localSource = localDataSource,
            marketDataRemoteDataSource = marketDataRemoteDataSource,
            marketDataLocalDataSource = marketDataLocalDataSource,
            dispatchers = testDispatcherProvider
        )
    }
}