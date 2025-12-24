package com.cointrend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.models.Screen
import dev.olshevski.navigation.reimagined.NavController as ReimagineNavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController

/**
 * Adapter to bridge between old Reimagined Navigation and new Jetpack Navigation.
 * This allows existing screens to work during the migration period.
 *
 * Note: Since NavController is a final class, we can't extend it directly.
 * For now, we'll just return the reimagined controller and let screens use it
 * while the actual navigation is handled by Jetpack Navigation in the wrappers.
 */
@Composable
fun rememberNavigationAdapter(
    @Suppress("UNUSED_PARAMETER")
    navHostController: NavHostController
): ReimagineNavController<Screen> {
    // Create a dummy reimagined nav controller for compatibility
    // The actual navigation will be handled by the Jetpack NavController
    return rememberNavController<Screen>(
        startDestination = Screen.CoinsList
    )
}

/**
 * Extension functions to help with navigation during migration
 */
fun NavHostController.navigateToCoinDetail(coinUiItem: CoinUiItem) {
    val route = "CoinDetailRoute/${coinUiItem.id}/${coinUiItem.name}/${coinUiItem.symbol}/${coinUiItem.imageUrl}/${coinUiItem.marketCapRank}"
    this.navigate(route)
}

fun NavHostController.navigateToSearch() {
    this.navigate("SearchRoute")
}

fun NavHostController.navigateToSettings() {
    this.navigate("SettingsRoute")
}

fun NavHostController.navigateToAbout() {
    this.navigate("AboutRoute")
}

fun NavHostController.navigateToFavorites() {
    this.navigate("FavouriteCoinsListRoute")
}