package com.cointrend.domain.features.marketchart.models

import java.time.LocalDateTime

data class MarketChartDataPoint(
    val date: LocalDateTime,
    val price: Double
)
