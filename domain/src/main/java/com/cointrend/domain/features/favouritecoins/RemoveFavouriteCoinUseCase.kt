package com.cointrend.domain.features.favouritecoins

import javax.inject.Inject

class RemoveFavouriteCoinUseCase @Inject constructor(
    private val favouriteCoinsRepository: FavouriteCoinsRepository
) {

    suspend operator fun invoke(coinId: String) {
        favouriteCoinsRepository.removeFavouriteCoin(coinId = coinId)
    }

}