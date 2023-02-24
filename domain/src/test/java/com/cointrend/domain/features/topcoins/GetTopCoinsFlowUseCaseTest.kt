package com.cointrend.domain.features.topcoins

import app.cash.turbine.test
import com.cointrend.domain.features.topcoins.mocks.*
import com.cointrend.domain.mocks.expectedException
import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import fr.haan.resultat.Resultat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

@OptIn(ExperimentalCoroutinesApi::class)
class GetTopCoinsFlowUseCaseTest : BaseCoroutineTestWithTestDispatcherProvider(
    dispatcher = StandardTestDispatcher()
) {

    @MockK private lateinit var refreshTopCoinsUseCase: RefreshTopCoinsUseCase

    private fun getCut(topCoinsRepository: TopCoinsRepository) = GetTopCoinsFlowUseCase(
        topCoinsRepository = topCoinsRepository,
        refreshTopCoinsUseCase = refreshTopCoinsUseCase
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }


    @Test
    fun `when topCoinsRepository fails throwing an exception outside flow, CUT rethrows the exception inside flow`() = runTest {
        val cut = getCut(TopCoinsRepositoryWithFailure())

        cut.invoke(Unit).test {
            val exception = awaitError()
            expectThat(exception).isEqualTo(expectedException)
        }
    }

    @Test
    fun `when topCoinsRepository throws EmptyDatabaseException, CUT emits Loading and calls refreshTopCoins`() =
        runTest {
            val cut = getCut(TopCoinsRepositoryWithInitialEmptyDatabase())

            coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.failure(expectedException)

            cut.invoke(Unit).test {
                val firstItem = awaitItem()
                expectThat(firstItem).isA<Resultat.Loading>()

                cancelAndIgnoreRemainingEvents()
            }

        coVerify(exactly = 1) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

    @Test
    fun `when topCoinsRepository throws EmptyDatabaseException and refreshTopCoins emits success but then topCoinsRepository throws another exception, CUT catches and rethrows it`() = runTest {
        val cut = getCut(TopCoinsRepositoryWithInitialEmptyDatabaseAndThenFailure())

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            val firstItem = awaitItem()
            expectThat(firstItem).isA<Resultat.Loading>()

            val exception = awaitError()
            expectThat(exception).isEqualTo(expectedException)
        }
    }

    @Test
    fun `when topCoinsRepository throws EmptyDatabaseException and refreshTopCoins emits success, CUT emits refreshed data`() = runTest {
        val cut = getCut(TopCoinsRepositoryWithInitialEmptyDatabaseAndThenSuccessData())

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            val firstItem = awaitItem()
            expectThat(firstItem).isA<Resultat.Loading>()

            val data = awaitItem()
            expectThat(data).isEqualTo(Resultat.success(expectedTopCoinDataThatDontRequireRefresh))

            awaitComplete()
        }

        coVerify(exactly = 1) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

    @Test
    fun `when topCoinsRepository emits data that should not be refreshed, CUT emits data`() = runTest {
        val cut = getCut(TopCoinsRepositoryWithSuccessRefreshedData())

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            val data = awaitItem()
            expectThat(data).isEqualTo(Resultat.success(expectedTopCoinDataThatDontRequireRefresh))

            awaitComplete()
        }

        coVerify(exactly = 0) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

    @Test
    fun `when topCoinsRepository emits data that should be refreshed, CUT emits old data, emits Loading and calls refreshTopCoins`() = runTest {
        val repository = TopCoinsRepositoryWithManualEmitOfData()
        val cut = getCut(repository)

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            repository.emit(expectedTopCoinDataThatShouldBeRefreshed)

            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(
                Resultat.success(
                    expectedTopCoinDataThatShouldBeRefreshed
                )
            )

            val secondItem = awaitItem()
            expectThat(secondItem).isA<Resultat.Loading>()
        }

        coVerify(exactly = 1) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

    @Test
    fun `when topCoinsRepository emits data that should be refreshed, CUT emits old data, emits Loading, calls refreshTopCoins and finally refreshed data`() = runTest {
        val repository = TopCoinsRepositoryWithManualEmitOfData()
        val cut = getCut(repository)

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            repository.emit(expectedTopCoinDataThatShouldBeRefreshed)

            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(
                Resultat.success(
                    expectedTopCoinDataThatShouldBeRefreshed
                )
            )

            val secondItem = awaitItem()
            expectThat(secondItem).isA<Resultat.Loading>()

            repository.emit(expectedTopCoinDataThatDontRequireRefresh)

            val refreshedData = awaitItem()
            expectThat(refreshedData).isEqualTo(
                Resultat.success(
                    expectedTopCoinDataThatDontRequireRefresh
                )
            )
        }

        coVerify(exactly = 1) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }


    @Test
    fun `when topCoinsRepository emits data that are updated but with missing market data that should so be refreshed, CUT emits old data, emits Loading and calls refreshTopCoins`() = runTest {
        val repository = TopCoinsRepositoryWithManualEmitOfData()
        val cut = getCut(repository)

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            repository.emit(expectedTopCoinDataUpdatedButWithMissingMarketDataThatShouldBeRefreshed)

            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(
                Resultat.success(
                    expectedTopCoinDataUpdatedButWithMissingMarketDataThatShouldBeRefreshed
                )
            )

            val secondItem = awaitItem()
            expectThat(secondItem).isA<Resultat.Loading>()
        }

        coVerify(exactly = 1) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

    @Test
    fun `when topCoinsRepository emits data that should be refreshed and with missing market data, CUT emits old data, emits Loading and calls refreshTopCoins 2 times`() = runTest {
        val repository = TopCoinsRepositoryWithManualEmitOfData()
        val cut = getCut(repository)

        coEvery { refreshTopCoinsUseCase.invoke(Unit) } returns Result.success(Unit)

        cut.invoke(Unit).test {
            repository.emit(expectedTopCoinDataThatShouldBeRefreshedAndWithMissingMarketData)

            val firstItem = awaitItem()
            expectThat(firstItem).isEqualTo(
                Resultat.success(
                    expectedTopCoinDataThatShouldBeRefreshedAndWithMissingMarketData
                )
            )

            val firstRefreshLoading = awaitItem()
            expectThat(firstRefreshLoading).isA<Resultat.Loading>()

            val secondRefreshLoading = awaitItem()
            expectThat(secondRefreshLoading).isA<Resultat.Loading>()
        }

        coVerify(exactly = 2) { refreshTopCoinsUseCase.invoke(Unit) }
        confirmVerified(refreshTopCoinsUseCase)
    }

}