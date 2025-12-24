package com.cointrend.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization
 * These replace the old Screen sealed class for Jetpack Navigation Compose
 */
@Serializable
sealed interface NavigationRoute

@Serializable
object CoinsListRoute : NavigationRoute

@Serializable
object FavouriteCoinsListRoute : NavigationRoute

@Serializable
object SearchRoute : NavigationRoute

@Serializable
object SettingsRoute : NavigationRoute

@Serializable
object AboutRoute : NavigationRoute

@Serializable
@Parcelize
data class CoinDetailRoute(
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinImageUrl: String,
    val coinMarketCapRank: String
) : NavigationRoute, Parcelable

/**
 * Extension function to convert old Screen sealed class to new NavigationRoute
 * This helps with gradual migration if needed
 */
fun com.cointrend.presentation.models.Screen.toNavigationRoute(): NavigationRoute {
    return when (this) {
        is com.cointrend.presentation.models.Screen.CoinsList -> CoinsListRoute
        is com.cointrend.presentation.models.Screen.FavouriteCoinsList -> FavouriteCoinsListRoute
        is com.cointrend.presentation.models.Screen.Search -> SearchRoute
        is com.cointrend.presentation.models.Screen.Settings -> SettingsRoute
        is com.cointrend.presentation.models.Screen.About -> AboutRoute
        is com.cointrend.presentation.models.Screen.CoinDetail -> CoinDetailRoute(
            coinId = this.coinDetailMainData.id,
            coinName = this.coinDetailMainData.name,
            coinSymbol = this.coinDetailMainData.symbol,
            coinImageUrl = this.coinDetailMainData.imageUrl,
            coinMarketCapRank = this.coinDetailMainData.marketCapRank
        )
    }
}