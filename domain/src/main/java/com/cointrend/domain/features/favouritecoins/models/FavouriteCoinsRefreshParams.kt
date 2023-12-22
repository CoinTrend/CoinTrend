package com.cointrend.domain.features.favouritecoins.models

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.TimeRange

data class FavouriteCoinsRefreshParams(
    val currency: Currency,
    val timeRange: TimeRange
)
