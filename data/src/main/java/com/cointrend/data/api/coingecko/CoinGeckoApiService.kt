package com.cointrend.data.api.coingecko

import com.cointrend.data.api.coingecko.models.CoinGeckoMarketChartDto
import com.cointrend.data.api.coingecko.models.CoinGeckoMarketsDto
import com.cointrend.data.api.coingecko.models.CoinGeckoSearchDto
import com.cointrend.data.api.coingecko.models.CoinGeckoSearchTrendingDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinGeckoApiService {

    @GET("coins/markets")
    suspend fun getCoinsMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("page") page: Int = 1,
        @Query("per_page") numCoinsPerPage: Int = 100,
        @Query("order") order: String = "market_cap_desc",
        @Query("sparkline") includeSparkline7dData: Boolean = false,
        @Query("price_change_percentage") priceChangePercentageIntervals: String = "",
        @Query("ids") coinIds: String? = null
    ): List<CoinGeckoMarketsDto>

    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "1"
    ): CoinGeckoMarketChartDto

    @GET("search/trending")
    suspend fun getSearchTrending(): CoinGeckoSearchTrendingDto

    @GET("search")
    suspend fun getSearch(
        @Query("query") query: String
    ): CoinGeckoSearchDto


    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }

}