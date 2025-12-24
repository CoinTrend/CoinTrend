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
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSelectedCard
import com.cointrend.presentation.theme.StocksDarkTopAppBarCollapsed
import com.cointrend.presentation.ui.coindetail.CoinDetailRoute
import com.cointrend.presentation.ui.coinslist.CoinsListRoute
import com.cointrend.presentation.ui.coinslist.CoinsListViewModel
import com.cointrend.presentation.ui.favouritecoins.FavouriteCoinsRoute
import com.cointrend.presentation.ui.search.SearchRoute
import com.cointrend.presentation.ui.settings.SettingsRoute
import com.cointrend.presentation.ui.favouritecoins.FavouriteCoinsViewModel
import com.cointrend.presentation.ui.search.SearchViewModel
import com.cointrend.presentation.ui.settings.SettingsViewModel
import java.net.URLEncoder
import java.net.URLDecoder

@Composable
@Suppress("UNUSED_PARAMETER")
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
            NavHost(
                navController = navController,
                startDestination = "CoinsListRoute"
            ) {
                    composable("CoinsListRoute") {
                        CoinsListRoute(
                            onNavigateToCoinDetail = { coin ->
                                val encodedImageUrl = URLEncoder.encode(coin.imageUrl, "UTF-8")
                                val route = "CoinDetailRoute/${coin.id}/${coin.name}/${coin.symbol}/${encodedImageUrl}/${coin.marketCapRank}"
                                navController.navigate(route)
                            },
                            viewModel = startDestinationViewModel
                        )
                    }

                    composable("FavouriteCoinsListRoute") {
                        FavouriteCoinsRoute(
                            onNavigateToCoinDetail = { coin ->
                                val encodedImageUrl = URLEncoder.encode(coin.imageUrl, "UTF-8")
                                val route = "CoinDetailRoute/${coin.id}/${coin.name}/${coin.symbol}/${encodedImageUrl}/${coin.marketCapRank}"
                                navController.navigate(route)
                            },
                            viewModel = hiltViewModel<FavouriteCoinsViewModel>()
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
                        val encodedImageUrl = backStackEntry.arguments?.getString("coinImageUrl") ?: ""
                        val coinImageUrl = URLDecoder.decode(encodedImageUrl, "UTF-8")
                        val coinMarketCapRank = backStackEntry.arguments?.getString("coinMarketCapRank") ?: ""
                        val coinUiItem = CoinUiItem(
                            id = coinId,
                            name = coinName,
                            symbol = coinSymbol,
                            imageUrl = coinImageUrl,
                            marketCapRank = coinMarketCapRank
                        )
                        CoinDetailRoute(
                            coinDetailMainUiData = coinUiItem,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("SearchRoute") {
                        SearchRoute(
                            onNavigateToCoinDetail = { coin ->
                                val encodedImageUrl = URLEncoder.encode(coin.imageUrl, "UTF-8")
                                val route = "CoinDetailRoute/${coin.id}/${coin.name}/${coin.symbol}/${encodedImageUrl}/${coin.marketCapRank}"
                                navController.navigate(route)
                            },
                            viewModel = hiltViewModel<SearchViewModel>()
                        )
                    }

                    composable("SettingsRoute") {
                        SettingsRoute(
                            onNavigateToAbout = {
                                navController.navigate("AboutRoute")
                            },
                            viewModel = hiltViewModel<SettingsViewModel>()
                        )
                    }

                    composable("AboutRoute") {
                        // AboutScreen needs to be refactored to remove NavController dependency
                        // For now, we'll keep it as is since it's not a critical screen
                    }
                }
        }
    }
}

