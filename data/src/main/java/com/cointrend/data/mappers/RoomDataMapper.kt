package com.cointrend.data.mappers

import com.cointrend.data.db.room.models.CoinMarketDataEntity
import com.cointrend.data.features.favouritecoins.local.models.FavouriteCoinEntity
import com.cointrend.data.features.favouritecoins.local.models.FavouriteCoinWithMarketDataEntity
import com.cointrend.data.features.topcoins.local.models.TopCoinEntity
import com.cointrend.data.features.topcoins.local.models.TopCoinWithMarketDataEntity
import com.cointrend.data.features.trendingcoins.local.models.TrendingCoinEntity
import com.cointrend.domain.models.Coin
import com.cointrend.domain.models.CoinMarketData
import com.cointrend.domain.models.CoinWithMarketData
import com.github.davidepanidev.kotlinextensions.deserializeAsJson
import com.github.davidepanidev.kotlinextensions.serializeToJsonString
import com.github.davidepanidev.kotlinextensions.utils.serialization.SerializationManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class RoomDataMapper @Inject constructor(
    private val serializationManager: SerializationManager
) {

     private fun mapTopCoinEntity(index: Int, coinWithMarketData: CoinWithMarketData): TopCoinEntity {
        return with(coinWithMarketData) {
            TopCoinEntity(
                sortedPosition = index,
                id = id,
                name = name,
                symbol = symbol,
                image = image,
                rank = rank
            )
        }
    }

    fun mapFavouriteCoinEntity(coin: Coin): FavouriteCoinEntity {
        return with(coin) {
            FavouriteCoinEntity(
                id = id,
                name = name,
                symbol = symbol,
                image = image,
                rank = rank
            )
        }
    }

    private fun mapCoinMarketDataEntity(coinWithMarketData: CoinWithMarketData): CoinMarketDataEntity? {
        return coinWithMarketData.marketData?.let {
            mapCoinMarketDataEntity(
                coinId = coinWithMarketData.id,
                coinMarketData = it
            )
        }
    }

    fun mapTopCoinEntityList(coinsList: List<CoinWithMarketData>): List<TopCoinEntity> {
        return coinsList.mapIndexed { index, coinWithMarketData ->
            mapTopCoinEntity(index, coinWithMarketData)
        }
    }

    fun mapCoinMarketDataEntityList(coinsList: List<CoinWithMarketData>): List<CoinMarketDataEntity> {
        return coinsList.mapNotNull {
            mapCoinMarketDataEntity(it)
        }
    }

    private fun mapTopCoinWithMarketData(coinWithMarketDataEntity: TopCoinWithMarketDataEntity): CoinWithMarketData {
        return with(coinWithMarketDataEntity) {
            CoinWithMarketData(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol,
                image = coin.image,
                marketData = marketData?.let { mapCoinMarketData(coinMarketDataEntity = it) },
                rank = marketData?.marketCapRank
            )
        }
    }

    fun mapCoinMarketData(coinMarketDataEntity: CoinMarketDataEntity): CoinMarketData {
        return with(coinMarketDataEntity) {
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
                priceChangePercentage = priceChangePercentage,
                sparklineData = sparklineData?.deserializeAsJson(serializationManager),
                remoteLastUpdate = remoteLastUpdate?.toLocalDateTime(),
                lastUpdate = lastUpdate.toLocalDateTime(),
            )
        }
    }

    fun mapCoinMarketDataEntity(coinId: String, coinMarketData: CoinMarketData): CoinMarketDataEntity {
        return with(coinMarketData) {
            CoinMarketDataEntity(
                coinId = coinId,
                currentPrice = price,
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
                athDate = athDate?.toEpochSecond(ZoneOffset.UTC),
                atl = atl,
                atlChangePercentage = atlChangePercentage,
                atlDate = atlDate?.toEpochSecond(ZoneOffset.UTC),
                priceChangePercentage = priceChangePercentage,
                sparklineData = sparklineData?.serializeToJsonString(serializationManager),
                remoteLastUpdate = remoteLastUpdate?.toEpochSecond(ZoneOffset.UTC),
                lastUpdate = lastUpdate.toEpochSecond(ZoneOffset.UTC)
            )
        }
    }

    fun mapTopCoinWithMarketDataList(coinsList: List<TopCoinWithMarketDataEntity>): List<CoinWithMarketData> {
        return coinsList.map { mapTopCoinWithMarketData(it) }
    }

    fun mapFavouriteCoinWithMarketDataList(coinsList: List<FavouriteCoinWithMarketDataEntity>): List<CoinWithMarketData> {
        return coinsList.map { mapFavouriteCoinWithMarketData(it) }
    }

    private fun mapFavouriteCoinWithMarketData(coinWithMarketDataEntity: FavouriteCoinWithMarketDataEntity): CoinWithMarketData {
        return with(coinWithMarketDataEntity) {
            CoinWithMarketData(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol,
                image = coin.image,
                marketData = marketData?.let { mapCoinMarketData(coinMarketDataEntity = it) },
                rank = marketData?.marketCapRank
            )
        }
    }

    fun mapCoinList(coinsList: List<TrendingCoinEntity>): List<Coin> {
        return coinsList.map {
            with(it) {
                Coin(
                    id = id,
                    name = name,
                    symbol = symbol,
                    image = image,
                    rank = rank
                )
            }
        }
    }

    fun mapTrendingCoinEntityList(coinsList: List<Coin>): List<TrendingCoinEntity> {
        return coinsList.mapIndexed { index, coinWithMarketData ->
            mapTrendingCoinEntity(index, coinWithMarketData)
        }
    }

    private fun mapTrendingCoinEntity(index: Int, coin: Coin): TrendingCoinEntity {
        return with(coin) {
            TrendingCoinEntity(
                sortedPosition = index,
                id = id,
                name = name,
                symbol = symbol,
                image = image,
                rank = rank,
                insertionDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }
    }

    fun mapLocalDateTime(millis: Long): LocalDateTime {
        return millis.toLocalDateTime()
    }

    private fun Long.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(this, 0, ZoneOffset.UTC)
    }

}