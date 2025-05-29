package com.cointrend.presentation

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cointrend.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import com.cointrend.presentation.theme.StocksDarkSelectedCard
import com.cointrend.presentation.theme.StocksDarkTopAppBarCollapsed
import com.cointrend.presentation.ui.about.AboutScreen
import com.cointrend.presentation.ui.coindetail.CoinDetailScreen
import com.cointrend.presentation.ui.coinslist.CoinsListScreen
import com.cointrend.presentation.ui.coinslist.CoinsListViewModel
import com.cointrend.presentation.ui.favouritecoins.FavouriteCoinsScreen
import com.cointrend.presentation.ui.search.SearchScreen
import com.cointrend.presentation.ui.settings.SettingsScreen
import com.github.davidepanidev.androidextensions.views.openAppInPlayStore
import com.github.davidepanidev.androidextensions.views.openEmailInExternalApp
import com.github.davidepanidev.androidextensions.views.openUrlInExternalBrowser
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val startDestinationViewModel: CoinsListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoinTrendTheme {

                val navController = rememberNavController<Screen>(
                    startDestination = Screen.CoinsList
                )

                NavBackHandler(navController)

                val isBackStackEmpty by remember {
                    derivedStateOf {
                        navController.backstack.entries.size == 1
                    }
                }

                var showPlayStoreReviewAlert by remember {
                    mutableStateOf(false)
                }

                BackHandler(enabled = isBackStackEmpty) {
                    if (viewModel.shouldShowPlayStoreReviewAlert) {
                        showPlayStoreReviewAlert = true
                    } else {
                        finish()
                    }
                }


                val currentDestination by remember {
                    derivedStateOf {
                        navController.backstack.entries.first().destination
                    }
                }


                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        NavigationBar(
                            containerColor = StocksDarkTopAppBarCollapsed
                        ) {
                            BottomNavigationItem.entries.forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = stringResource(id = item.title)) },
                                    label = { Text(stringResource(id = item.title)) },
                                    selected = item.route == currentDestination,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = StocksDarkPrimaryText,
                                        indicatorColor = StocksDarkSelectedCard
                                    ),
                                    onClick = {
                                        if (item.route != currentDestination) {
                                            navController.popAll()
                                            navController.navigate(item.route)
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    Surface(modifier = Modifier.padding(it)) {
                        SharedElementsRoot {
                            NavHost(
                                controller = navController,
                                //transitionSpec = mainNavHostTransitionSpec
                            ) { route ->
                                when(route) {
                                    is Screen.CoinsList -> { CoinsListScreen(navController = navController, viewModel = startDestinationViewModel) }
                                    is Screen.FavouriteCoinsList -> { FavouriteCoinsScreen(navController = navController, viewModel = hiltViewModel(viewModelStoreOwner = this@MainActivity)) }
                                    is Screen.CoinDetail -> { CoinDetailScreen(coinDetailMainUiData = route.coinDetailMainData, navController = navController) }
                                    is Screen.Search -> { SearchScreen(navController = navController, viewModel = hiltViewModel(viewModelStoreOwner = this@MainActivity)) }
                                    is Screen.Settings -> { SettingsScreen(navController = navController, viewModel = hiltViewModel(viewModelStoreOwner = this@MainActivity)) }
                                    is Screen.About -> {
                                        AboutScreen(
                                            navController = navController,
                                            playStoreCoinTrendPackageName = packageName,
                                            onLinkClick = { url ->
                                                this@MainActivity.openUrlInExternalBrowser(url = url)
                                            },
                                            onEmailClick = { email, subject ->
                                                this@MainActivity.openEmailInExternalApp(
                                                    toEmailAddresses = setOf(email),
                                                    subject = subject
                                                )
                                            },
                                            onPlayStoreClick = {
                                                openAppInPlayStore(packageName = packageName)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                if (showPlayStoreReviewAlert) {
                    LaunchedEffect(key1 = null) {
                        viewModel.onPlayStoreReviewAlertShown()
                    }

                    PlayStoreReviewAlert(
                        onConfirmClick = {
                            openAppInPlayStore(packageName = packageName)
                            finish()
                        },
                        onDismissClick = {
                            finish()
                        }
                    )
                }

            }
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                startDestinationViewModel.state.topCoinsList.isEmpty() &&
                        startDestinationViewModel.state.state !is CoinsListUiState.Error
            }

            setOnExitAnimationListener { screen ->
                val fade = ObjectAnimator.ofFloat(
                    screen.view,
                    View.ALPHA,
                    1f,
                    0f
                ).apply {
                    interpolator = AccelerateInterpolator()
                    duration = 150L
                    doOnEnd { screen.remove() }
                }

                fade.start()
            }
        }

        viewModel.init()
    }
}


@Composable
private fun PlayStoreReviewAlert(
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Text(
                text = "Ok, I'll leave a review",
                modifier = Modifier
                    .clickable {
                        onConfirmClick()

                    }
                    .padding(end = 8.dp),
                color = StocksDarkSecondaryText
            )
        },
        dismissButton = {
            Text(
                text = "No, thanks",
                modifier = Modifier
                    .clickable {
                        onDismissClick()
                    }
                    .padding(end = 8.dp),
                color = StocksDarkSecondaryText
            )
        },
        title = { Text(text = "What would you like to see in the next update?") },
        text = {
            Text(
                text = "Let us know by leaving a rate and a review on Google Play Store.",
                color = StocksDarkPrimaryText
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.TipsAndUpdates,
                contentDescription = null,
                tint = StocksDarkPrimaryText
            )
        }
    )
}