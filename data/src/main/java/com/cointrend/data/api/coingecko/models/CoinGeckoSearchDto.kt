package com.cointrend.data.api.coingecko.models


import com.google.gson.annotations.SerializedName

data class CoinGeckoSearchDto(
    val coins: List<CoinDto>
) {

    data class CoinDto(
        val id: String,
        val name: String? = null,
        val symbol: String? = null,
        @SerializedName("market_cap_rank")
        val marketCapRank: Int? = null,
        val large: String? = null
    )

}