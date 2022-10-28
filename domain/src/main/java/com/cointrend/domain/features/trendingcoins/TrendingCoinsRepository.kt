package com.cointrend.domain.features.trendingcoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import kotlinx.coroutines.flow.Flow

interface TrendingCoinsRepository : BaseAutomaticRefreshDataFlowRepository<TrendingCoinsData, Unit, Unit> {

    override fun getDataFlow(inputParams: Unit): Flow<TrendingCoinsData>

    override suspend fun refreshData(params: Unit): Result<Unit>

}