package com.cointrend.data.mappers

import com.cointrend.data.BuildConfig
import com.cointrend.data.api.coingecko.models.CoinGeckoMarketChartDto
import com.cointrend.data.api.coingecko.models.CoinGeckoMarketsDto
import com.cointrend.data.api.coingecko.models.CoinGeckoSearchDto
import com.cointrend.data.api.coingecko.models.CoinGeckoSearchTrendingDto
import com.cointrend.domain.features.marketchart.models.MarketChartDataPoint
import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.models.*
import com.github.davidepanidev.kotlinextensions.roundToNDecimals
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.sign

class CoinGeckoDataMapper @Inject constructor(
    private val settingsConfiguration: GlobalSettingsConfiguration
) {

    private fun mapCoin(coinDto: CoinGeckoSearchTrendingDto.CoinDto): Coin {
        return with(coinDto.item) {
            Coin(
                id = id,
                name = name.orEmpty(),
                symbol = symbol.orEmpty(),
                image = large.orEmpty(),
                rank = marketCapRank
            )
        }
    }

    fun mapCoins(trendingCoinsDto: CoinGeckoSearchTrendingDto): List<Coin> {
        return trendingCoinsDto.coins.map {
            mapCoin(coinDto = it)
        }
    }

    fun mapCoins(searchDto: CoinGeckoSearchDto): List<Coin> {
        return searchDto.coins.map {
            mapCoin(coinDto = it)
        }
    }

    private fun mapCoin(coinDto: CoinGeckoSearchDto.CoinDto): Coin {
        return with(coinDto) {
            Coin(
                id = id,
                name = name.orEmpty(),
                symbol = symbol.orEmpty(),
                image = large.orEmpty(),
                rank = marketCapRank
            )
        }
    }

    private fun mapCoinWithMarketData(coinResponse: CoinGeckoMarketsDto): CoinWithMarketData {
        return CoinWithMarketData(
            id = coinResponse.id,
            name = coinResponse.name.orEmpty(),
            symbol = coinResponse.symbol.orEmpty(),
            image = coinResponse.image.orEmpty(),
            marketData = mapCoinMarketData(coinResponse),
            rank = coinResponse.marketCapRank
        )
    }

    fun mapCoinMarketData(coinResponse: CoinGeckoMarketsDto): CoinMarketData {
        return with(coinResponse) {
            CoinMarketData(
                price = currentPrice,
                marketCapRank = marketCapRank,
                marketCap = marketCap,
                marketCapChangePercentage24h = marketCapChangePercentage24h,
                totalVolume = totalVolume,
                high24h = high24h,
                low24h = low24h,
                circulatingSupply = circulatingSupply,
                totalSupply = totalSupply,
                maxSupply = maxSupply,
                ath = ath,
                athChangePercentage = athChangePercentage,
                athDate = athDate?.toLocalDateTime(),
                atl = atl,
                atlChangePercentage = atlChangePercentage,
                atlDate = atlDate?.toLocalDateTime(),
                priceChangePercentage = when (settingsConfiguration.getDefaultTimeRange()) {
                    TimeRange.Day -> priceChangePercentage24h
                    TimeRange.Week -> priceChangePercentage7dInCurrency
                    TimeRange.Month -> priceChangePercentage30dInCurrency
                    TimeRange.SixMonths -> priceChangePercentage200dInCurrency
                    TimeRange.Year -> priceChangePercentage1yInCurrency
                    TimeRange.Max -> if (BuildConfig.DEBUG) {
                        throw IllegalStateException("mapCoinMarketData ERROR: Max value is not supported by coins/markets API. This values should never be reached anyway.")
                    } else {
                        Timber.e("mapCoinMarketData ERROR: defaulting to null as the Max value is not supported by coins/markets API. This value should never be reached anyway.")
                        null
                    }
                },
                sparklineData = sparklineIn7d?.price?.let {
                    if (settingsConfiguration.getDefaultTimeRange() == TimeRange.Day) {
                        val lastItems = it.takeLast(
                            (it.size / 7)
                        ).toMutableList()

                        // Add a last price item so that it matches exactly the priceChangePercentage24h value
                        // to show a more precise chart
                        if (lastItems.isNotEmpty()) {
                            val startPrice = lastItems.first()
                            val lastPrice = lastItems.last()

                            val priceChangePercentage = ((lastPrice - startPrice) / startPrice) * 100

                            if (priceChangePercentage24h != null && priceChangePercentage.sign != priceChangePercentage24h.sign) {
                                val lastPriceToAdd = ((priceChangePercentage24h * startPrice) / 100) + startPrice
                                lastItems.add(lastPriceToAdd)
                            }
                        }

                        lastItems.toList()
                    } else if (settingsConfiguration.getDefaultTimeRange() == TimeRange.Week) {
                        val div = when(it.size) {
                            in 0..100 -> 5
                            in 100..200 -> 10
                            else -> 20
                        }

                        it.filterIndexed { index, _ ->
                            index % div == 0
                        }
                    } else {
                        null
                    }
                }?.map {
                    it.roundToNDecimals(decimals = 8)
                },
                remoteLastUpdate = lastUpdated?.toLocalDateTime(),
                lastUpdate = LocalDateTime.now() // lastUpdate is LocalDateTime.now() so that the last update done by the App is considered
            )
        }
    }

    fun mapCoinsWithMarketDataList(coinsListResponse: List<CoinGeckoMarketsDto>): List<CoinWithMarketData> {
        return coinsListResponse.map { mapCoinWithMarketData(it) }
    }

    fun mapMarketChartDataPointList(marketChartDto: CoinGeckoMarketChartDto): List<MarketChartDataPoint> {
        val divAmount = when(marketChartDto.prices.size) {
            in 0..200 -> 1
            in 200..400 -> 2
            in 400..800 -> 6
            in 800..1600 -> 10
            else -> 12
        }

        return marketChartDto.prices.filterIndexed { index, _ ->
            index % divAmount == 0
        }.map {
            mapMarketChartDataPoint(it)
        }
    }

    private fun mapMarketChartDataPoint(dataPointList: List<Double>): MarketChartDataPoint {
        val date =
            Instant.ofEpochMilli(dataPointList.first().toLong()).atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        val price = dataPointList[1]

        return MarketChartDataPoint(
            date = date,
            price = price
        )
    }

    fun mapCurrencyToCoinGeckoApiValue(currency: Currency): String {
        return when (currency) {
            Currency.USD -> "usd"
            Currency.EUR -> "eur"
            Currency.BTC -> "btc"
        }
    }

    fun mapTimeRangeToCoinGeckoApiValue(timeRange: TimeRange): String {
        return when (timeRange) {
            TimeRange.Day -> "1"
            TimeRange.Week -> "7"
            TimeRange.Month -> "30"
            TimeRange.SixMonths -> "200" //200 instead of 180 to being consistent with the coins/markets API used for the top coins list which supports only 200d
            TimeRange.Year -> "365"
            TimeRange.Max -> "max"
        }
    }

    fun mapTimeRangeToPriceChangeCoinGeckoApiValue(timeRange: TimeRange): String {
        return when (timeRange) {
            TimeRange.Day -> "24h"
            TimeRange.Week -> "7d"
            TimeRange.Month -> "30d"
            TimeRange.SixMonths -> "200d" // 200d as coins/markets API supports only this value.
            TimeRange.Year -> "1y"
            TimeRange.Max -> {
                if (BuildConfig.DEBUG) {
                    throw IllegalStateException("mapTimeRangeToPriceChangeCoinGeckoApiValue ERROR: MAX is not available for coins/markets API. This value should never be reached.")
                } else {
                    Timber.e("mapTimeRangeToPriceChangeCoinGeckoApiValue ERROR: defaulting to 1y as MAX is not available for coins/markets API. This value should never be reached anyway.")
                    "1y"
                }
            }
        }
    }

    fun mapTimeRangeToIncludeSparkline7dData(timeRange: TimeRange): Boolean {
        return when (timeRange) {
            TimeRange.Day, TimeRange.Week -> true
            else -> false // As the sparkline data provided by CoinGecko are only for 7 days, does not make sense to show that data the time ranges greater than 7 days.
        }
    }

    fun mapOrderingToCoinGeckoApiValue(ordering: Ordering): String {
        return when (ordering) {
            Ordering.MarketCapDesc -> "market_cap_desc"
            else -> ""
        }
    }

    private fun String.toLocalDateTime(): LocalDateTime? {
        return try {
            Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

}