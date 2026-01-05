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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.navigation.CoinTrendNavigation
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import com.cointrend.presentation.ui.coinslist.CoinsListViewModel
import com.github.davidepanidev.androidextensions.views.openAppInPlayStore
import com.github.davidepanidev.androidextensions.views.openEmailInExternalApp
import com.github.davidepanidev.androidextensions.views.openUrlInExternalBrowser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val startDestinationViewModel: CoinsListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoinTrendTheme {

                val navController = rememberNavController()

                var showPlayStoreReviewAlert by remember {
                    mutableStateOf(false)
                }

                BackHandler(enabled = navController.previousBackStackEntry == null) {
                    if (viewModel.shouldShowPlayStoreReviewAlert) {
                        showPlayStoreReviewAlert = true
                    } else {
                        finish()
                    }
                }

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CoinTrendNavigation(
                        navController = navController,
                        startDestinationViewModel = startDestinationViewModel,
                        packageName = packageName,
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