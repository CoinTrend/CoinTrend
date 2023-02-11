package com.cointrend.data.features.favouritecoins

import com.cointrend.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cointrend.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.cointrend.data.features.mocks.*
import com.cointrend.domain.models.toCoin
import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

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


    @Test
    fun `reorderFavouriteCoin reorder coins correcly and insert them back into the localSource`() = runTest {
        coEvery { localDataSource.getAllCoinsFlow() } returns flow {
            emit(getCoinsWithMarketDataList())
        }

        coEvery { localDataSource.insertCoins(any()) } just runs


        val result = cut.reorderFavouriteCoin(
            coinId = "usdc",
            toIndex = 0
        )

        expectThat(result).isSuccess()
        verify(exactly = 1) { localDataSource.getAllCoinsFlow() }
        coVerify(exactly = 1) {
            localDataSource.insertCoins(
                coins = listOf(
                    expectedCoinWithMarketDataUsdc.toCoin(),
                    expectedCoinWithMarketDataBtc.toCoin(),
                    expectedCoinWithMarketDataEth.toCoin(),
                    expectedCoinWithMarketDataUsdt.toCoin(),
                    expectedCoinWithMarketDataSol.toCoin()
                )
            )
        }
        confirmVerified(localDataSource)
    }

    @Test
    fun `reorderFavouriteCoin reorder coin in the same position correcly and insert them back into the localSource`() = runTest {
        coEvery { localDataSource.getAllCoinsFlow() } returns flow {
            emit(getCoinsWithMarketDataList())
        }

        coEvery { localDataSource.insertCoins(any()) } just runs


        val result = cut.reorderFavouriteCoin(
            coinId = "btc",
            toIndex = 0
        )

        expectThat(result).isSuccess()
        verify(exactly = 1) { localDataSource.getAllCoinsFlow() }
        coVerify(exactly = 1) {
            localDataSource.insertCoins(
                coins = listOf(
                    expectedCoinWithMarketDataBtc.toCoin(),
                    expectedCoinWithMarketDataEth.toCoin(),
                    expectedCoinWithMarketDataUsdc.toCoin(),
                    expectedCoinWithMarketDataUsdt.toCoin(),
                    expectedCoinWithMarketDataSol.toCoin()
                )
            )
        }
        confirmVerified(localDataSource)
    }

    @Test
    fun `reorderFavouriteCoin called with unknown coin, catches the Exception and returns Failure and does not modify the localSource`() = runTest {
        coEvery { localDataSource.getAllCoinsFlow() } returns flow {
            emit(getCoinsWithMarketDataList())
        }


        val result = cut.reorderFavouriteCoin(
            coinId = "ust",
            toIndex = 0
        )

        expectThat(result).isFailure().and {
            this.isA<IndexOutOfBoundsException>()
        }

        verify(exactly = 1) { localDataSource.getAllCoinsFlow() }
        confirmVerified(localDataSource)
    }

    @Test
    fun `reorderFavouriteCoin called with out of bound index, catches the Exception and returns Failure and does not modify the localSource`() = runTest {
        coEvery { localDataSource.getAllCoinsFlow() } returns flow {
            emit(getCoinsWithMarketDataList())
        }


        val result = cut.reorderFavouriteCoin(
            coinId = "btc",
            toIndex = 10
        )

        expectThat(result).isFailure().and {
            this.isA<IndexOutOfBoundsException>()
        }

        verify(exactly = 1) { localDataSource.getAllCoinsFlow() }
        confirmVerified(localDataSource)
    }

}