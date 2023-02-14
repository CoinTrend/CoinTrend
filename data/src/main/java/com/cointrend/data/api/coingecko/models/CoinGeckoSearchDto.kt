package com.cointrend.data.api.coingecko.models


import com.google.gson.annotations.SerializedName

data class CoinGeckoSearchDto(
    val coins: List<CoinDto>
) {

    data class CoinDto(
        val id: String,
        val name: String,
        val symbol: String,
        @SerializedName("market_cap_rank")
        val marketCapRank: Int, //TODO: set to nullable as it is not always available for less popular coins.
        val large: String? = null
    )

}