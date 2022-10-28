package com.cointrend.domain.features.marketdata

import com.cointrend.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cointrend.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cointrend.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.models.CoinMarketData
import javax.inject.Inject

/**
 * This UseCase refreshes the top coins list. The Result is of Unit type as
 * the coins list must be retrieved from the single source of truth provided by the
 * [GetCoinMarketDataFlowUseCase].
 */
class RefreshCoinMarketDataUseCase @Inject constructor(
    topCoinsRepository: CoinMarketDataRepository,
    private val settingsConfiguration: SettingsConfiguration
) : BaseRefreshDataUseCase<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams>(
    repository = topCoinsRepository
) {

    override fun getRefreshParams(inputParams: CoinMarketDataInputParams): CoinMarketDataRefreshParams {
        //val settings = settingsRepository.getSettingsFlow().last()
        val settings = settingsConfiguration

        return CoinMarketDataRefreshParams(
            coinId = inputParams.coinId,
            currency = settings.currency
        )
    }

}