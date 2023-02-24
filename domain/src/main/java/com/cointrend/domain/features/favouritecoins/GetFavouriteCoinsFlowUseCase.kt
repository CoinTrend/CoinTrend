package com.cointrend.domain.features.favouritecoins

import com.cointrend.domain.features.commons.automaticmissingmarketdatarefresh.BaseAutomaticRefreshCoinIfMissingMarketDataFlowUseCase
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsRefreshParams
import com.cointrend.domain.models.FAVOURITE_COINS_MINUTES_REFRESH_PERIOD
import javax.inject.Inject

/**
 * This UseCase provides the single source of truth of the top coins list.
 * It also automatically handles the refresh of data as a [BaseAutomaticRefreshCoinIfMissingMarketDataFlowUseCase].
 */
class GetFavouriteCoinsFlowUseCase @Inject constructor(
    favouriteCoinsRepository: FavouriteCoinsRepository,
    refreshFavouriteCoinsUseCase: RefreshFavouriteCoinsUseCase
) : BaseAutomaticRefreshCoinIfMissingMarketDataFlowUseCase<FavouriteCoinsData, Unit, FavouriteCoinsRefreshParams>(
    refreshDataUseCase = refreshFavouriteCoinsUseCase,
    repository = favouriteCoinsRepository,
    minutesRequiredToRefreshData = FAVOURITE_COINS_MINUTES_REFRESH_PERIOD.toLong()
)