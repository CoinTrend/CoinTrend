package com.cointrend.domain.features.commons.automaticrefresh

import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate
import kotlinx.coroutines.flow.Flow

interface BaseAutomaticRefreshDataFlowRepository<OutputData : BaseDataWithLastUpdateDate, InputParams, RefreshParams> {

    fun getDataFlow(inputParams: InputParams): Flow<OutputData>

    suspend fun refreshData(params: RefreshParams): Result<Unit>

}