package com.cointrend.domain.features.favouritecoins

import javax.inject.Inject

class ReorderFavouriteCoinUseCase @Inject constructor(
    private val favouriteCoinsRepository: FavouriteCoinsRepository
) {

    suspend operator fun invoke(coinId: String, toIndex: Int): Result<Unit> {
        return favouriteCoinsRepository.reorderFavouriteCoin(coinId = coinId, toIndex = toIndex)
    }

}