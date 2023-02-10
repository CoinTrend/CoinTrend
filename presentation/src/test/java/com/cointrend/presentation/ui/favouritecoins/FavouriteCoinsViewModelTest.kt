package com.cointrend.presentation.ui.favouritecoins

import com.cointrend.domain.features.favouritecoins.GetFavouriteCoinsFlowUseCase
import com.cointrend.domain.features.favouritecoins.RefreshFavouriteCoinsUseCase
import com.cointrend.domain.features.favouritecoins.ReorderFavouriteCoinUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.presentation.mappers.UiMapper
import com.cointrend.presentation.ui.mocks.getCoinsWithMarketDataList
import com.github.davidepanidev.androidextensions.tests.BaseCoroutineTestWithTestDispatcherProviderAndInstantTaskExecutorRule
import fr.haan.resultat.Resultat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
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

        cut = FavouriteCoinsViewModel(
            getFavouriteCoinsFlowUseCase = getFavouriteCoinFlowUseCase,
            refreshFavouriteCoinsUseCase = refreshFavouriteCoinsUseCase,
            reorderFavouriteCoinUseCase = reorderFavouriteCoinUseCase,
            mapper = mapper
        )
    }


    @Test
    fun `onCoinPositionReordered reorder coins locally and updates its state correctly`() {

    }


    private val mockFavouriteCoinsData = FavouriteCoinsData(
        coins = getCoinsWithMarketDataList(),
        lastUpdate = LocalDateTime.of(2023, 2, 10, 0, 0)
    )

}