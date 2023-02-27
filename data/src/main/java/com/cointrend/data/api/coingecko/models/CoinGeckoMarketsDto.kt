package com.cointrend.data.api.coingecko.models

import com.google.gson.annotations.SerializedName

data class CoinGeckoMarketsDto(
    val id: String,
    val symbol: String? = null,
    val name: String? = null,
    val image: String? = null,
    @SerializedName("current_price")
    val currentPrice: Double? = null,
    @SerializedName("market_cap")
    val marketCap: Double? = null,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int? = null,
    @SerializedName("fully_diluted_valuation")
    val fullyDilutedValuation: Double? = null,
    @SerializedName("total_volume")
    val totalVolume: Double? = null,
    @SerializedName("high_24h")
    val high24h: Double? = null,
    @SerializedName("low_24h")
    val low24h: Double? = null,
    @SerializedName("price_change_24h")
    val priceChange24h: Double? = null,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double? = null,
    @SerializedName("market_cap_change_24h")
    val marketCapChange24h: Double? = null,
    @SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double? = null,
    @SerializedName("circulating_supply")
    val circulatingSupply: Double? = null,
    @SerializedName("total_supply")
    val totalSupply: Double? = null,
    @SerializedName("max_supply")
    val maxSupply: Double? = null,
    val ath: Double? = null,
    @SerializedName("ath_change_percentage")
    val athChangePercentage: Double? = null,
    @SerializedName("ath_date")
    val athDate: String? = null,
    val atl: Double? = null,
    @SerializedName("atl_change_percentage")
    val atlChangePercentage: Double? = null,
    @SerializedName("atl_date")
    val atlDate: String? = null,
    @SerializedName("sparkline_in_7d")
    val sparklineIn7d: SparklineIn7d? = null,
    @SerializedName("last_updated")
    val lastUpdated: String? = null,
    @SerializedName("price_change_percentage_7d_in_currency")
    val priceChangePercentage7dInCurrency: Double? = null
)


data class SparklineIn7d(
    val price: List<Double>? = null
)
