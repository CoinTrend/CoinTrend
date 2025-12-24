package com.cointrend.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.cointrend.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSelectedCard
import com.cointrend.presentation.theme.StocksDarkTopAppBarCollapsed
import com.cointrend.presentation.ui.about.AboutScreen
import com.cointrend.presentation.ui.coindetail.CoinDetailScreen
import com.cointrend.presentation.ui.coinslist.CoinsListScreen
import com.cointrend.presentation.ui.coinslist.CoinsListViewModel
import com.cointrend.presentation.ui.favouritecoins.FavouriteCoinsScreen
import com.cointrend.presentation.ui.search.SearchScreen
import com.cointrend.presentation.ui.settings.SettingsScreen
import com.cointrend.presentation.ui.favouritecoins.FavouriteCoinsViewModel
import com.cointrend.presentation.ui.search.SearchViewModel
import com.cointrend.presentation.ui.settings.SettingsViewModel

@Composable
fun CoinTrendNavigation(
    navController: NavHostController,
    startDestinationViewModel: CoinsListViewModel,
    packageName: String,
    onLinkClick: (String) -> Unit,
    onEmailClick: (String, String) -> Unit,
    onPlayStoreClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = StocksDarkTopAppBarCollapsed
            ) {
                BottomNavigationItem.entries.forEach { item ->
                    val isSelected = when (item) {
                        BottomNavigationItem.Market -> currentRoute?.contains("CoinsListRoute") == true
                        BottomNavigationItem.Favourites -> currentRoute?.contains("FavouriteCoinsListRoute") == true
                        BottomNavigationItem.Search -> currentRoute?.contains("SearchRoute") == true
                        BottomNavigationItem.Settings -> currentRoute?.contains("SettingsRoute") == true
                    }

                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(id = item.title)) },
                        label = { Text(stringResource(id = item.title)) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StocksDarkPrimaryText,
                            indicatorColor = StocksDarkSelectedCard
                        ),
                        onClick = {
                            if (!isSelected) {
                                when (item) {
                                    BottomNavigationItem.Market -> {
                                        navController.navigate("CoinsListRoute") {
                                            popUpTo("CoinsListRoute") { inclusive = true }
                                        }
                                    }
                                    BottomNavigationItem.Favourites -> {
                                        navController.navigate("FavouriteCoinsListRoute") {
                                            popUpTo("CoinsListRoute")
                                        }
                                    }
                                    BottomNavigationItem.Search -> {
                                        navController.navigate("SearchRoute") {
                                            popUpTo("CoinsListRoute")
                                        }
                                    }
                                    BottomNavigationItem.Settings -> {
                                        navController.navigate("SettingsRoute") {
                                            popUpTo("CoinsListRoute")
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            SharedElementsRoot {
                NavHost(
                    navController = navController,
                    startDestination = "CoinsListRoute"
                ) {
                    composable("CoinsListRoute") {
                        CoinsListScreenWrapper(
                            navController = navController,
                            viewModel = startDestinationViewModel
                        )
                    }

                    composable("FavouriteCoinsListRoute") {
                        FavouriteCoinsScreenWrapper(
                            navController = navController
                        )
                    }

                    composable(
                        route = "CoinDetailRoute/{coinId}/{coinName}/{coinSymbol}/{coinImageUrl}/{coinMarketCapRank}",
                        arguments = listOf(
                            navArgument("coinId") { type = NavType.StringType },
                            navArgument("coinName") { type = NavType.StringType },
                            navArgument("coinSymbol") { type = NavType.StringType },
                            navArgument("coinImageUrl") { type = NavType.StringType },
                            navArgument("coinMarketCapRank") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                        val coinName = backStackEntry.arguments?.getString("coinName") ?: ""
                        val coinSymbol = backStackEntry.arguments?.getString("coinSymbol") ?: ""
                        val coinImageUrl = backStackEntry.arguments?.getString("coinImageUrl") ?: ""
                        val coinMarketCapRank = backStackEntry.arguments?.getString("coinMarketCapRank") ?: ""
                        val coinUiItem = CoinUiItem(
                            id = coinId,
                            name = coinName,
                            symbol = coinSymbol,
                            imageUrl = coinImageUrl,
                            marketCapRank = coinMarketCapRank
                        )
                        val navigationAdapter = rememberNavigationAdapter(navController)
                        CoinDetailScreen(
                            coinDetailMainUiData = coinUiItem,
                            navController = navigationAdapter
                        )
                    }

                    composable("SearchRoute") {
                        SearchScreenWrapper(
                            navController = navController
                        )
                    }

                    composable("SettingsRoute") {
                        SettingsScreenWrapper(
                            navController = navController
                        )
                    }

                    composable("AboutRoute") {
                        AboutScreenWrapper(
                            navController = navController,
                            packageName = packageName,
                            onLinkClick = onLinkClick,
                            onEmailClick = onEmailClick,
                            onPlayStoreClick = onPlayStoreClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper function to extract CoinUiItem from navigation arguments
 * This ensures type safety when navigating to CoinDetailScreen
 */
fun createCoinUiItemFromRoute(route: CoinDetailRoute): CoinUiItem {
    return CoinUiItem(
        id = route.coinId,
        name = route.coinName,
        symbol = route.coinSymbol,
        imageUrl = route.coinImageUrl,
        marketCapRank = route.coinMarketCapRank
    )
}

/**
 * Wrapper composables that handle navigation logic for existing screens
 * These allow gradual migration from old navigation patterns to new ones
 */

@Composable
private fun CoinsListScreenWrapper(
    navController: NavHostController,
    viewModel: CoinsListViewModel
) {
    // Create adapter to bridge old and new navigation
    val navigationAdapter = rememberNavigationAdapter(navController)

    // Use existing CoinsListScreen with adapter
    CoinsListScreen(
        navController = navigationAdapter,
        viewModel = viewModel
    )
}

@Composable
private fun FavouriteCoinsScreenWrapper(
    navController: NavHostController
) {
    val navigationAdapter = rememberNavigationAdapter(navController)

    FavouriteCoinsScreen(
        navController = navigationAdapter,
        viewModel = hiltViewModel<FavouriteCoinsViewModel>()
    )
}

@Composable
private fun SearchScreenWrapper(
    navController: NavHostController
) {
    val navigationAdapter = rememberNavigationAdapter(navController)

    SearchScreen(
        navController = navigationAdapter,
        viewModel = hiltViewModel<SearchViewModel>()
    )
}

@Composable
private fun SettingsScreenWrapper(
    navController: NavHostController
) {
    val navigationAdapter = rememberNavigationAdapter(navController)

    SettingsScreen(
        navController = navigationAdapter,
        viewModel = hiltViewModel<SettingsViewModel>()
    )
}

@Composable
private fun AboutScreenWrapper(
    navController: NavHostController,
    packageName: String,
    onLinkClick: (String) -> Unit,
    onEmailClick: (String, String) -> Unit,
    onPlayStoreClick: () -> Unit
) {
    val navigationAdapter = rememberNavigationAdapter(navController)

    AboutScreen(
        navController = navigationAdapter,
        playStoreCoinTrendPackageName = packageName,
        onLinkClick = onLinkClick,
        onEmailClick = onEmailClick,
        onPlayStoreClick = onPlayStoreClick
    )
}