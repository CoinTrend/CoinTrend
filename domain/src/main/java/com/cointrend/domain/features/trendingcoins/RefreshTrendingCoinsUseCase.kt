package com.cointrend.domain.features.trendingcoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import javax.inject.Inject

class RefreshTrendingCoinsUseCase @Inject constructor(
    trendingCoinsRepository: TrendingCoinsRepository
) : BaseRefreshDataUseCase<TrendingCoinsData, Unit, Unit>(
    repository = trendingCoinsRepository
) {

    override fun getRefreshParams(inputParams: Unit) = Unit

}