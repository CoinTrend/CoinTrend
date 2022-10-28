package com.cointrend.domain.features.topcoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.topcoins.models.TopCoinsRefreshParams
import kotlinx.coroutines.flow.Flow

interface TopCoinsRepository :
    BaseAutomaticRefreshDataFlowRepository<TopCoinsData, Unit, TopCoinsRefreshParams> {

    override fun getDataFlow(inputParams: Unit): Flow<TopCoinsData>

    override suspend fun refreshData(params: TopCoinsRefreshParams): Result<Unit>

}