package com.cointrend.domain.features.favouritecoins

import javax.inject.Inject

class GetFavouriteCoinsIdsUseCase @Inject constructor(
    private val favouriteCoinsRepository: FavouriteCoinsRepository,
) {

    suspend operator fun invoke(): Result<List<String>> {
        return favouriteCoinsRepository.getFavouriteCoinsIds()
    }

}