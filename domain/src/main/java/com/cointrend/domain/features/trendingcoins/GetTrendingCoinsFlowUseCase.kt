package com.cointrend.domain.features.trendingcoins

import com.cointrend.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowUseCase
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import com.cointrend.domain.models.TRENDING_COINS_MINUTES_REFRESH_PERIOD
import javax.inject.Inject

/**
 * This UseCase provides the single source of truth of the top coins list.
 * It also automatically handles the refresh of data as a [BaseAutomaticRefreshDataFlowUseCase].
 */
class GetTrendingCoinsFlowUseCase @Inject constructor(
    trendingCoinsRepository: TrendingCoinsRepository,
    refreshTrendingCoinsUseCase: RefreshTrendingCoinsUseCase
) : BaseAutomaticRefreshDataFlowUseCase<TrendingCoinsData, Unit, Unit>(
    refreshDataUseCase = refreshTrendingCoinsUseCase,
    repository = trendingCoinsRepository,
    minutesRequiredToRefreshData = TRENDING_COINS_MINUTES_REFRESH_PERIOD.toLong()
)