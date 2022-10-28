package com.cointrend.domain.features.marketdata

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowUseCase
import com.cointrend.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cointrend.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cointrend.domain.models.COIN_MARKET_DATA_REFRESH_PERIOD
import com.cointrend.domain.models.CoinMarketData
import javax.inject.Inject

class GetCoinMarketDataFlowUseCase @Inject constructor(
    coinMarketDataRepository: CoinMarketDataRepository,
    refreshCoinMarketDataUseCase: RefreshCoinMarketDataUseCase
) : BaseAutomaticRefreshDataFlowUseCase<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams>(
    refreshDataUseCase = refreshCoinMarketDataUseCase,
    repository = coinMarketDataRepository,
    minutesRequiredToRefreshData = COIN_MARKET_DATA_REFRESH_PERIOD.toLong()
)