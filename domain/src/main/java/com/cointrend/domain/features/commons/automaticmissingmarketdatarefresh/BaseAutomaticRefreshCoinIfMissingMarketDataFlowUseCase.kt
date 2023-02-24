package com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh

import com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh.models.BaseCoinsWithMarketDataWithLastUpdate
import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowUseCase
import com.cointrend.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cointrend.domain.models.CoinWithMarketData
import fr.haan.resultat.Resultat
import fr.haan.resultat.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform

/**
 * This UseCase provides a flow that extends the behavior of [BaseAutomaticRefreshDataFlowUseCase] specifically for [BaseCoinsWithMarketDataWithLastUpdate] data.
 * It automatically triggers a data refresh when some of the retrieved coins are missing the [CoinWithMarketData.marketData] value.
 */
abstract class BaseAutomaticRefreshCoinIfMissingMarketDataFlowUseCase<OutputData : BaseCoinsWithMarketDataWithLastUpdate, InputParams, RefreshParams>(
    refreshDataUseCase: BaseRefreshDataUseCase<OutputData, InputParams, RefreshParams>,
    repository: BaseAutomaticRefreshDataFlowRepository<OutputData, InputParams, RefreshParams>,
    minutesRequiredToRefreshData: Long
) : BaseAutomaticRefreshDataFlowUseCase<OutputData, InputParams, RefreshParams>(
    refreshDataUseCase = refreshDataUseCase,
    repository = repository,
    minutesRequiredToRefreshData = minutesRequiredToRefreshData
) {

    override fun getDataFlow(inputParams: InputParams): Flow<Resultat<OutputData>> {
        return super.getDataFlow(inputParams).transform { result ->
            emit(result)
            result.onSuccess {
                if (isSomeCoinMissingMarketData(coinsWithMarketDataList = it.coinsWithMarketDataList)) {
                    emitAll(refreshData(inputParams))
                }
            }
        }
    }

    private fun isSomeCoinMissingMarketData(coinsWithMarketDataList: List<CoinWithMarketData>): Boolean {
        return coinsWithMarketDataList.any {
            it.marketData == null
        }
    }

}