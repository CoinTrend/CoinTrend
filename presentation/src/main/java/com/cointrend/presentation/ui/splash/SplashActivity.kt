package com.cointrend.presentation.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.cointrend.presentation.MainActivity
import com.cointrend.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.ui.coinslist.CoinsListScreen
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController<Screen>(
                startDestination = Screen.CoinsList
            )
            NavBackHandler(navController)

            CoinTrendTheme {
                SharedElementsRoot {
                    CoinsListScreen(navController = navController, viewModel = hiltViewModel())
                }

                SplashScreen()
            }
        }

        lifecycleScope.launch {
            delay(4000L)

            startActivity(
                Intent(this@SplashActivity, MainActivity::class.java)
            )

            finish()
        }
    }

}