package com.cointrend.domain.features.commons.automaticrefresh

import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate

/**
 * This UseCase refreshes manually features' data. The Result is of Unit type as
 * data can be only retrieved from the single source of truth provided by the
 * [BaseAutomaticRefreshDataFlowUseCase].
 */
abstract class BaseRefreshDataUseCase<OutputData : BaseDataWithLastUpdateDate, InputParams, RefreshParams>(
    private val repository: BaseAutomaticRefreshDataFlowRepository<OutputData, InputParams, RefreshParams>
) {

    protected abstract fun getRefreshParams(inputParams: InputParams): RefreshParams

    suspend operator fun invoke(inputParams: InputParams): Result<Unit> {
        return repository.refreshData(params = getRefreshParams(inputParams))
    }

}