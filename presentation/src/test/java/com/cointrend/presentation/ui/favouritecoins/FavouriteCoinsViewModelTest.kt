package com.cointrend.presentation.ui.favouritecoins

import com.cointrend.domain.features.favouritecoins.GetFavouriteCoinsFlowUseCase
import com.cointrend.domain.features.favouritecoins.RefreshFavouriteCoinsUseCase
import com.cointrend.domain.features.favouritecoins.ReorderFavouriteCoinUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.mocks.*
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.models.FavouriteCoinUiData
import com.github.davidepanidev.androidextensions.tests.BaseCoroutineTestWithTestDispatcherProviderAndInstantTaskExecutorRule
import fr.haan.resultat.Resultat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteCoinsViewModelTest : BaseCoroutineTestWithTestDispatcherProviderAndInstantTaskExecutorRule(
    dispatcher = StandardTestDispatcher()
) {

    private lateinit var cut: FavouriteCoinsViewModel

    @MockK
    private lateinit var getFavouriteCoinFlowUseCase: GetFavouriteCoinsFlowUseCase

    @MockK
    private lateinit var refreshFavouriteCoinsUseCase: RefreshFavouriteCoinsUseCase

    @MockK
    private lateinit var reorderFavouriteCoinUseCase: ReorderFavouriteCoinUseCase

    @MockK
    private lateinit var mapper: UiMapper


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { getFavouriteCoinFlowUseCase.invoke(Unit) } returns flow {
            emit(Resultat.success(mockFavouriteCoinsData))
        }

        every { mapper.mapFavouriteCoinUiData(mockFavouriteCoinsData) } returns mockFavouriteCoinUiData


        cut = FavouriteCoinsViewModel(
            getFavouriteCoinsFlowUseCase = getFavouriteCoinFlowUseCase,
            refreshFavouriteCoinsUseCase = refreshFavouriteCoinsUseCase,
            reorderFavouriteCoinUseCase = reorderFavouriteCoinUseCase,
            mapper = mapper
        )

        initFavouriteCoinsViewModelStateByConsumingFirstFlowValue()
    }

    private fun initFavouriteCoinsViewModelStateByConsumingFirstFlowValue() = runTest {
        cut.init()
        advanceUntilIdle()
    }


    @Test
    fun `onCoinPositionReordered reorder coins locally and updates its state correctly`() = runTest {
        coEvery { reorderFavouriteCoinUseCase.invoke(any(), any()) } returns Result.success(Unit)

        cut.onCoinPositionReordered(
            coinId = "btc",
            fromIndex = 0,
            toIndex = 1
        )


        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )
        expectThat(cut.state.state).isEqualTo(CoinsListUiState.Idle)
    }

    @Test
    fun `onCoinPositionReordered calls reorderFavouriteCoinUseCase with correct parameters`() = runTest {
        val expectedId = "btc"
        val expectedToIndex = 1

        coEvery { reorderFavouriteCoinUseCase(expectedId, expectedToIndex) } returns Result.success(Unit)


        cut.onCoinPositionReordered(
            coinId = expectedId,
            fromIndex = 0,
            toIndex = expectedToIndex
        )

        advanceUntilIdle()

        coVerify(exactly = 1) {
            reorderFavouriteCoinUseCase(
                coinId = any(),
                toIndex = any()
            )
        }
        confirmVerified(reorderFavouriteCoinUseCase)
        expectThat(cut.state.state).isEqualTo(CoinsListUiState.Idle)
    }

    @Test
    fun `when onCoinPositionReordered is called two times sequentially in short time, reorderFavouriteCoinUseCase is called only the second time`() = runTest {
        coEvery { reorderFavouriteCoinUseCase(any(), any()) } returns Result.success(Unit)

        val expectedId = "btc"
        val firstCallExpectedToIndex = 1
        val secondCallExpectedToIndex = 2


        cut.onCoinPositionReordered(
            coinId = expectedId,
            fromIndex = 0,
            toIndex = firstCallExpectedToIndex
        )

        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )

        advanceTimeBy(
            FavouriteCoinsViewModel.delayBeforeCallingReorderFavouriteCoinUseCase -
                    (FavouriteCoinsViewModel.delayBeforeCallingReorderFavouriteCoinUseCase / 2)
        )

        cut.onCoinPositionReordered(
            coinId = expectedId,
            fromIndex = firstCallExpectedToIndex,
            toIndex = secondCallExpectedToIndex
        )

        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )

        advanceUntilIdle()


        coVerify(exactly = 1) {
            reorderFavouriteCoinUseCase(
                coinId = expectedId,
                toIndex = secondCallExpectedToIndex
            )
        }
        confirmVerified(reorderFavouriteCoinUseCase)
        expectThat(cut.state.state).isEqualTo(CoinsListUiState.Idle)
    }

    @Test
    fun `when onCoinPositionReordered is called two times sequentially in long time, reorderFavouriteCoinUseCase is called both times`() = runTest {
        coEvery { reorderFavouriteCoinUseCase(any(), any()) } returns Result.success(Unit)

        val expectedId = "btc"
        val firstCallExpectedToIndex = 1
        val secondCallExpectedToIndex = 2


        cut.onCoinPositionReordered(
            coinId = expectedId,
            fromIndex = 0,
            toIndex = firstCallExpectedToIndex
        )

        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )

        advanceTimeBy(
            FavouriteCoinsViewModel.delayBeforeCallingReorderFavouriteCoinUseCase +
                    (FavouriteCoinsViewModel.delayBeforeCallingReorderFavouriteCoinUseCase / 2)
        )

        coVerify(exactly = 1) {
            reorderFavouriteCoinUseCase(
                coinId = expectedId,
                toIndex = firstCallExpectedToIndex
            )
        }


        cut.onCoinPositionReordered(
            coinId = expectedId,
            fromIndex = firstCallExpectedToIndex,
            toIndex = secondCallExpectedToIndex
        )

        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )

        advanceUntilIdle()


        coVerify(exactly = 1) {
            reorderFavouriteCoinUseCase(
                coinId = expectedId,
                toIndex = secondCallExpectedToIndex
            )
        }
        confirmVerified(reorderFavouriteCoinUseCase)
        expectThat(cut.state.state).isEqualTo(CoinsListUiState.Idle)
    }




    private val mockFavouriteCoinsData = FavouriteCoinsData(
        coins = emptyList(),
        lastUpdate = LocalDateTime.of(2023, 2, 10, 0, 0)
    )

    private val mockFavouriteCoinUiData = FavouriteCoinUiData(
        coins = getCoinsWithMarketDataUiList().toImmutableList(),
        lastUpdate = "2023-02-10"
    )

}