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
        val name: String,
        val symbol: String,
        @SerializedName("market_cap_rank")
        val marketCapRank: Int, //TODO: set to nullable as it is not always available for less popular coins.
        val thumb: String? = null,
        val small: String? = null,
        val large: String? = null,
        val slug: String? = null,
        @SerializedName("price_btc")
        val priceBtc: Double? = null,
        val score: Int? = null
    )

}