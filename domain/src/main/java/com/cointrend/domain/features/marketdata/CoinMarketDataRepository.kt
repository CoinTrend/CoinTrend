package com.cointrend.domain.features.marketdata

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cointrend.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cointrend.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cointrend.domain.models.CoinMarketData
import kotlinx.coroutines.flow.Flow

interface CoinMarketDataRepository : BaseAutomaticRefreshDataFlowRepository<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams> {

    override fun getDataFlow(inputParams: CoinMarketDataInputParams): Flow<CoinMarketData>

    override suspend fun refreshData(params: CoinMarketDataRefreshParams): Result<Unit>

}