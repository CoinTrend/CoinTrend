package com.cointrend.presentation.mappers

import android.os.Build
import androidx.compose.ui.graphics.Color
import com.cointrend.domain.exceptions.TemporarilyUnavailableNetworkServiceException
import com.cointrend.domain.features.favouritecoins.models.FavouriteCoinsData
import com.cointrend.domain.features.marketchart.models.MarketChartDataPoint
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.features.topcoins.models.TopCoinsData
import com.cointrend.domain.features.trendingcoins.models.TrendingCoinsData
import com.cointrend.domain.models.*
import com.cointrend.presentation.BuildConfig
import com.cointrend.presentation.di.DateAndTimeFormatter
import com.cointrend.presentation.di.DateOnlyFormatter
import com.cointrend.presentation.di.TimeOnlyFormatter
import com.cointrend.presentation.models.*
import com.cointrend.presentation.theme.NegativeTrend
import com.cointrend.presentation.theme.PositiveTrend
import com.github.davidepanidev.kotlinextensions.formatToCurrency
import com.github.davidepanidev.kotlinextensions.formatToPercentage
import com.github.davidepanidev.kotlinextensions.toFormattedString
import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.CurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.NumberFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import timber.log.Timber
import java.io.IOException
import java.math.RoundingMode
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val numberFormatter: NumberFormatter,
    @DateAndTimeFormatter private val dateTimeFormatter: DateTimeFormatter,
    @DateOnlyFormatter private val dateOnlyFormatter: DateTimeFormatter,
    @TimeOnlyFormatter private val timeOnlyFormatter: DateTimeFormatter,
    private val settingsConfiguration: SettingsConfiguration
) {

    fun mapCoin(coinUiItem: CoinUiItem): Coin {
        return with(coinUiItem) {
            Coin(
                id = id,
                name = name,
                symbol = symbol,
                image = imageUrl,
                rank = marketCapRank.toInt()
            )
        }
    }

    fun mapCoinUiItemsList(coins: List<Coin>): ImmutableList<CoinUiItem> {
        return coins.map {
            with(it) {
                CoinUiItem(
                    id = id,
                    name = name,
                    symbol = symbol,
                    imageUrl = image,
                    marketCapRank = rank.toString(),
                )
            }
        }.toImmutableList()
    }

    fun mapCoinUiItemsList(trendingCoinsData: TrendingCoinsData): ImmutableList<CoinUiItem> {
        return mapCoinUiItemsList(
            coins = trendingCoinsData.trendingCoins
        )
    }

    fun mapTopCoinUiData(topCoinData: TopCoinsData): TopCoinUiData {
        return TopCoinUiData(
            topCoins = mapCoinWithMarketDataUiItemsList(topCoinData.topCoins),
            lastUpdate = with(topCoinData.lastUpdate) {
                if (isToday()) {
                    toFormattedString(timeOnlyFormatter)
                } else {
                    toFormattedString(dateTimeFormatter)
                }
            }
        )
    }

    fun mapFavouriteCoinUiData(coinsData: FavouriteCoinsData): FavouriteCoinUiData {
        // The last update date presented is the most recent one to show the most
        // recent update date to the user, differently from the lastUpdate
        // considered at domain layer which is the least recent one.
        val lastUpdate = coinsData.coins.maxOfOrNull { it.lastUpdateOrNow() } ?: LocalDateTime.now()

        return FavouriteCoinUiData(
            coins = mapCoinWithMarketDataUiItemsList(coinsData.coins),
            lastUpdate = with(lastUpdate) {
                if (isToday()) {
                    toFormattedString(timeOnlyFormatter)
                } else {
                    toFormattedString(dateTimeFormatter)
                }
            }
        )
    }

    /**
     * Returns the actual implementation of [BaseCoinWithMarketDataUiItem] depending if
     * the the marketData field of the input [coin] is null or not.
     * @return [CoinWithMarketDataUiItem] if marketData of the input [coin] is not null. [CoinWithShimmeringMarketDataUiItem] otherwise.
     */
    private fun mapCoinWithMarketDataUiItem(coin: CoinWithMarketData): BaseCoinWithMarketDataUiItem {
        return coin.marketData?.let { marketData ->
            CoinWithMarketDataUiItem(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol.uppercase(),
                imageUrl = coin.image,
                price = marketData.price.toFormattedCurrency(),
                marketCapRank = coin.rank.toString(),
                priceChangePercentage = marketData.priceChangePercentage.formatToPercentage(
                    numberFormatter
                ),
                trendColor = marketData.priceChangePercentage.correspondingTrendColor(),
                sparklineData = coin.marketData?.sparklineData?.mapIndexed { _, d ->
                    DataPoint(y = d, xLabel = null, yLabel = null)
                }?.toImmutableList(),
                lastUpdate = marketData.lastUpdate.toFormattedString(formatter = dateTimeFormatter)
            )
        } ?: kotlin.run {
            CoinWithShimmeringMarketDataUiItem(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol.uppercase(),
                imageUrl = coin.image,
                price = "1,000.00$",
                marketCapRank = "N.A.",
                priceChangePercentage = "+0.00%",
                trendColor = Color.Gray,
                sparklineData = null,
                lastUpdate = "Not available"
            )
        }
    }

    fun mapCoinMarketUiData(coinMarketData: CoinMarketData): CoinMarketUiData {
        return with(coinMarketData) {
            CoinMarketUiData(
                price = price.toFormattedCurrency(),
                marketDataList = persistentListOf(
                    Pair("Market Cap", marketCap.toFormattedCurrency()),
                    Pair("Trading Volume 24h", totalVolume.toFormattedCurrency()),
                    Pair("Highest Price 24h", high24h.toFormattedCurrency()),
                    Pair("Lowest Price 24h", low24h.toFormattedCurrency()),
                    Pair("Available Supply", circulatingSupply.toFormattedCurrency(withoutSymbol = true)),
                    Pair("Total Supply", totalSupply?.toFormattedCurrency(withoutSymbol = true) ?: "Not available"),
                    Pair("Max Supply", totalSupply?.toFormattedCurrency(withoutSymbol = true) ?: "Not available"),
                    Pair("All-Time High Price", ath.toFormattedCurrency()),
                    Pair("All-Time High Date", athDate?.toFormattedString(dateOnlyFormatter) ?: "Not available"),
                    Pair("All-Time Low Price", atl.toFormattedCurrency()),
                    Pair("All-Time Low Date", atlDate?.toFormattedString(dateOnlyFormatter) ?: "Not available")
                )
            )
        }
    }

    private fun mapCoinWithMarketDataUiItemsList(coinsList: List<CoinWithMarketData>): ImmutableList<BaseCoinWithMarketDataUiItem> {
        return coinsList.map { mapCoinWithMarketDataUiItem(it) }.toImmutableList()
    }

    fun mapMarketChartUiData(marketChartDataPoints: List<MarketChartDataPoint>): MarketChartUiData {
        val startPrice = marketChartDataPoints.first().price
        val lastPrice = marketChartDataPoints.last().price

        val priceChangePercentage: Double = ((lastPrice - startPrice) / startPrice) * 100

        val startPriceDate = marketChartDataPoints.first().date

        val (lowestPrice, lowestPriceDate) = marketChartDataPoints.minBy { it.price }.let {
            Pair(it.price, it.date)
        }

        val (highestPrice, highestPriceDate) = marketChartDataPoints.maxBy { it.price }.let {
            Pair(it.price, it.date)
        }

        return MarketChartUiData(
            chartData = marketChartDataPoints.mapIndexed { _, marketChartDataPoint ->
                DataPoint(
                    y = marketChartDataPoint.price,
                    xLabel = null,
                    yLabel = marketChartDataPoint.price.toFormattedCurrency()
                )
            }.toImmutableList(),
            startPrice = startPrice.toFormattedCurrency(),
            startPriceDate = startPriceDate.toFormattedString(dateOnlyFormatter),
            lowestPrice = lowestPrice.toFormattedCurrency(),
            lowestPriceDate = lowestPriceDate.toFormattedString(dateOnlyFormatter),
            highestPrice = highestPrice.toFormattedCurrency(),
            highestPriceDate = highestPriceDate.toFormattedString(dateOnlyFormatter),
            priceChangePercentage = priceChangePercentage.formatToPercentage(numberFormatter),
            trendColor = priceChangePercentage.correspondingTrendColor()
        )
    }

    fun mapErrorToUiMessage(error: Throwable): String {
        Timber.e("ERROR: $error \n ${error.printStackTrace()}")

        return when(error) {
            is SocketTimeoutException -> "The service is temporarily unavailable. Please try again later."

            is TemporarilyUnavailableNetworkServiceException -> "The ${error.serviceName} service is temporarily unavailable. Please try again later."

            is UnknownHostException,
            is SocketException,
            is IOException -> {
                "You appear to be offline. Please, check your internet connection and retry."
            }

            is retrofit2.HttpException -> {
                when(error.code()) {
                    429, 503 -> "The CoinGecko service is temporarily unavailable [HTTP ${error.code()}]. Please try again later."
                    in 500..599 -> "The CoinGecko server is not responding [HTTP ${error.code()}]. Please try again later."
                    else -> if (BuildConfig.DEBUG) {
                        error.toString()
                    } else {
                        "There has been an error while retrieving data [HTTP ${error.code()}]. Please try again later."
                    }
                }
            }

            else -> if (BuildConfig.DEBUG) {
                error.toString()
            } else {
                "There has been an error while retrieving data. Please try again later."
            }
        }
    }


    private fun LocalDateTime.isToday(): Boolean {
        val now = LocalDateTime.now()

        return this.year == now.year && this.dayOfYear == now.dayOfYear
    }

    private fun Double.correspondingTrendColor(): Color {
        return if (this >= 0) PositiveTrend else NegativeTrend
    }

    private fun Number.toFormattedCurrency(withoutSymbol: Boolean = false): String {
        val number = if (this is Double) {
            this.roundTo2DecimalsIfTooLong()
        } else this

        val symbol = when(settingsConfiguration.getCurrency()) {
            Currency.USD -> "$"
            Currency.EUR -> "€"
            Currency.BTC -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "₿" else "B"
        }

        val formattedCurrency = number.formatToCurrency(
            currencyFormatter,
            customCurrencySymbol = symbol
        )

        return if (!withoutSymbol) {
            formattedCurrency
        } else {
            formattedCurrency.replace(symbol, "").trim()
        }
    }

    private fun Double.roundTo2DecimalsIfTooLong(): Double {
        return if (this >= 1.1) {
            this.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
        } else {
            this
        }
    }


}