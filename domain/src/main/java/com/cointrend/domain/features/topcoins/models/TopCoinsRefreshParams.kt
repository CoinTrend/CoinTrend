package com.cointrend.domain.features.topcoins.models

import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering

data class TopCoinsRefreshParams(
    val numCoins: Int,
    val currency: Currency,
    val ordering: Ordering
)
