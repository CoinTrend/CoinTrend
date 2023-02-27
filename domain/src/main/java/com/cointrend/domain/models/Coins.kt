package com.cointrend.domain.models

import com.cointrend.domain.features.commons.automaticrefresh.models.BaseDataWithLastUpdateDate
import java.time.LocalDateTime

abstract class BaseCoin(
    open val id: String,
    open val name: String,
    open val symbol: String,
    open val image: String
)

abstract class BaseCoinWithMarketData(
    id: String,
    name: String,
    symbol: String,
    image: String,
    open val marketData: CoinMarketData?
) : BaseCoin(
    id = id,
    name = name,
    symbol = symbol,
    image = image
)

data class CoinMarketData(
    val price: Double?,
    val marketCapRank: Int?,
    val marketCap: Double?,
    val marketCapChangePercentage24h: Double?,
    val totalVolume: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val athChangePercentage: Double?,
    val athDate: LocalDateTime?,
    val atl: Double?,
    val atlChangePercentage: Double?,
    val atlDate: LocalDateTime?,
    val priceChangePercentage: Double?,
    val sparklineData: List<Double>?,
    val remoteLastUpdate: LocalDateTime?,
    override val lastUpdate: LocalDateTime
) : BaseDataWithLastUpdateDate(
    lastUpdate = lastUpdate
)

data class Coin(
    override val id: String,
    override val name: String,
    override val symbol: String,
    override val image: String,
    val rank: Int? // Currently corresponding to marketCapRank of CoinMarketData.
) : BaseCoin(
    id = id,
    name = name,
    symbol = symbol,
    image = image
)

data class CoinWithMarketData(
    override val id: String,
    override val name: String,
    override val symbol: String,
    override val image: String,
    override val marketData: CoinMarketData?,
    val rank: Int?, // Currently redundant as it corresponds to marketCapRank of CoinMarketData.
) : BaseCoinWithMarketData(
    id = id,
    name = name,
    symbol = symbol,
    image = image,
    marketData = marketData
)

fun CoinWithMarketData.toCoin(): Coin {
    return with(this) {
        Coin(
            id = id,
            name = name,
            symbol = symbol,
            image = image,
            rank = rank
        )
    }
}

fun CoinWithMarketData.lastUpdateOrNow(): LocalDateTime {
    return this.marketData?.lastUpdate ?: LocalDateTime.now()
}