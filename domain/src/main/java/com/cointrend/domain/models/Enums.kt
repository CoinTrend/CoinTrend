package com.cointrend.domain.models

enum class Ordering {
    MarketCapAsc,
    MarketCapDesc,
    PriceAsc,
    PriceDesc,
    PriceChangeAsc,
    PriceChangeDesc,
    NameAsc,
    NameDesc
}

enum class Currency {
    USD,
    EUR,
    BTC
}

enum class TimeRange {
    Day,
    Week,
    Month,
    ThreeMonths,
    Year,
    Max
}