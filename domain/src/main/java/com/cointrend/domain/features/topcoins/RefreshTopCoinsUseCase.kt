package com.cointrend.domain.features.topcoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.topcoins.models.TopCoinsRefreshParams
import com.cointrend.domain.models.NUM_TOP_COINS_TO_SHOW
import javax.inject.Inject

class RefreshTopCoinsUseCase @Inject constructor(
    topCoinsRepository: TopCoinsRepository,
    private val settingsConfiguration: SettingsConfiguration
) : BaseRefreshDataUseCase<TopCoinsData, Unit, TopCoinsRefreshParams>(
    repository = topCoinsRepository
) {

    override fun getRefreshParams(inputParams: Unit): TopCoinsRefreshParams {
        //val settings = settingsRepository.getSettingsFlow().last()
        val settings = settingsConfiguration

        return TopCoinsRefreshParams(
            numCoins = NUM_TOP_COINS_TO_SHOW,
            currency = settings.currency,
            ordering = settings.ordering
        )
    }

}