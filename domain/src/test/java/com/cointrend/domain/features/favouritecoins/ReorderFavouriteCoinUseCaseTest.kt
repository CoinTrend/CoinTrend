package com.cointrend.domain.features.favouritecoins

import com.github.davidepanidev.kotlinextensions.utils.test.BaseCoroutineTestWithTestDispatcherProvider
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@OptIn(ExperimentalCoroutinesApi::class)
class ReorderFavouriteCoinUseCaseTest : BaseCoroutineTestWithTestDispatcherProvider(
    dispatcher = StandardTestDispatcher()
) {

    private lateinit var cut: ReorderFavouriteCoinUseCase

    @MockK
    private lateinit var repository: FavouriteCoinsRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        cut = ReorderFavouriteCoinUseCase(
            favouriteCoinsRepository = repository
        )
    }


    @Test
    fun `invoke calls reorderFavouriteCoin method of repository`() = runTest {
        val expectedResult = Result.success(Unit)
        coEvery { repository.reorderFavouriteCoin(any(), any()) } returns expectedResult

        val id = "bitcoin"
        val toIndex = 5

        val actualResult = cut.invoke(
            coinId = id,
            toIndex = toIndex
        )

        coVerify(exactly = 1) {
            repository.reorderFavouriteCoin(coinId = id, toIndex = toIndex)
        }
        expectThat(actualResult).isEqualTo(expectedResult)
    }

}