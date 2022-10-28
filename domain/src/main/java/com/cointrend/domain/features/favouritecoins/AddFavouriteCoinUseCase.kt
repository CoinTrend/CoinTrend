package com.cointrend.domain.features.favouritecoins

import com.cointrend.domain.models.Coin
import javax.inject.Inject

class AddFavouriteCoinUseCase @Inject constructor(
    private val favouriteCoinsRepository: FavouriteCoinsRepository
) {

    suspend operator fun invoke(coin: Coin) {
        favouriteCoinsRepository.addFavouriteCoin(coin = coin)
    }

}