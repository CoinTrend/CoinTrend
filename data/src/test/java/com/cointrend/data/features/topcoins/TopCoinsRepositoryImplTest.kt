package com.cointrend.data.features.topcoins

import app.cash.turbine.test
import com.cointrend.data.features.mocks.expectedCoinWithMarketData
import com.cointrend.data.features.mocks.expectedException
import com.cointrend.domain.exceptions.EmptyDatabaseException
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.topcoins.models.TopCoinsRefreshParams
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure

@OptIn(ExperimentalCoroutinesApi::class)
class TopCoinsRepositoryImplTest : BaseCoroutineTestWithTestDispatcherProvider(
    dispatcher = StandardTestDispatcher()
) {
    private lateinit var cut: TopCoinsRepositoryImpl

    @MockK
    private lateinit var remoteDataSource: TopCoinsRemoteDataSource

    @MockK
    private lateinit var localDataSource: TopCoinsLocalDataSource

    private val refreshParams = TopCoinsRefreshParams(
        numCoins = 10,
        currency = Currency.BTC,
        ordering = Ordering.MarketCapAsc
    )


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        cut = TopCoinsRepositoryImpl(
            remoteSource = remoteDataSource,
            localSource = localDataSource,
            dispatchers = testDispatcherProvider
        )
    }

    @Test
    fun `getDataFlow throws EmptyDatabaseException when list from flow is empty`() = runTest {
        every { localDataSource.getAllCoinsFlow() } returns flow {
            emit(emptyList())
        }

        cut.getDataFlow(Unit).test {
            val exception = awaitError()
            expectThat(exception).isA<EmptyDatabaseException>()
        }
    }

    @Test
    fun `getDataFlow emits data when list from flow is not empty`() = runTest {
        val list = listOf(expectedCoinWithMarketData)

        every { localDataSource.getAllCoinsFlow() } returns flow {
            emit(list)
        }

        cut.getDataFlow(Unit).test {
            val item = awaitItem()
            expectThat(item).isEqualTo(
                TopCoinsData(
                    topCoins = list,
                    lastUpdate = list.first().marketData!!.lastUpdate
                )
            )

            awaitComplete()
        }
    }

    @Test
    fun `refreshData return Failure when remote source returns exception`() = runTest {
        coEvery { remoteDataSource.retrieveTopCoinsWithMarketData(any(), any(), any()) } throws(
            expectedException
        )

        val result = cut.refreshData(refreshParams)

        expectThat(result).isFailure().and {
            this.isEqualTo(expectedException)
        }
    }


}