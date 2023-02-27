package com.cointrend.data.api.coingecko.models


import com.google.gson.annotations.SerializedName

data class CoinGeckoSearchTrendingDto(
    val coins: List<CoinDto>
) {

    data class CoinDto(
        val item: ItemDto
    )

    data class ItemDto(
        val id: String,
        @SerializedName("coin_id")
        val coinId: Int? = null,
        val name: String? = null,
        val symbol: String? = null,
        @SerializedName("market_cap_rank")
        val marketCapRank: Int? = null,
        val thumb: String? = null,
        val small: String? = null,
        val large: String? = null,
        val slug: String? = null,
        @SerializedName("price_btc")
        val priceBtc: Double? = null,
        val score: Int? = null
    )

}