package com.cointrend.domain.features.favouritecoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsRefreshParams
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import javax.inject.Inject

class RefreshFavouriteCoinsUseCase @Inject constructor(
    favouriteCoinsRepository: FavouriteCoinsRepository,
    private val settingsConfiguration: SettingsConfiguration
) : BaseRefreshDataUseCase<FavouriteCoinsData, Unit, FavouriteCoinsRefreshParams>(
    repository = favouriteCoinsRepository
) {

    override fun getRefreshParams(inputParams: Unit): FavouriteCoinsRefreshParams {
        //val settings = settingsRepository.getSettingsFlow().last()
        val settings = settingsConfiguration

        return FavouriteCoinsRefreshParams(
            currency = settings.currency
        )
    }

}