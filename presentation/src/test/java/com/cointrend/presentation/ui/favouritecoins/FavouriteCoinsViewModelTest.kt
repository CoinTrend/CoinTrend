package com.cointrend.presentation.ui.favouritecoins

import com.cointrend.domain.features.favouritecoins.GetFavouriteCoinsFlowUseCase
import com.cointrend.domain.features.favouritecoins.RefreshFavouriteCoinsUseCase
import com.cointrend.domain.features.favouritecoins.ReorderFavouriteCoinUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.models.FavouriteCoinUiData
import com.cointrend.presentation.ui.mocks.*
import com.github.davidepanidev.androidextensions.tests.BaseCoroutineTestWithTestDispatcherProviderAndInstantTaskExecutorRule
import fr.haan.resultat.Resultat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteCoinsViewModelTest : BaseCoroutineTestWithTestDispatcherProviderAndInstantTaskExecutorRule(
    dispatcher = UnconfinedTestDispatcher()
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
    }


    @Test
    fun `onCoinPositionReordered reorder coins locally and updates its state correctly`() = runTest {
        coEvery { reorderFavouriteCoinUseCase.invoke(any(), any()) } returns Result.success(Unit)

        cut.onCoinPositionReordered(
            coinId = "btc",
            fromIndex = 0,
            toIndex = 1
        )

        expectThat(cut.state.state).isEqualTo(CoinsListUiState.Idle)
        expectThat(cut.state.favouriteCoinsList).isEqualTo(
            persistentListOf(
                expectedCoinWithMarketDataEth,
                expectedCoinWithMarketDataBtc,
                expectedCoinWithMarketDataUsdc,
                expectedCoinWithMarketDataUsdt,
                expectedCoinWithMarketDataSol
            )
        )
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