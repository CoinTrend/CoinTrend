package com.cointrend.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.cointrend.presentation.commoncomposables.*
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.*
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController<Screen>,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = BottomNavigationItem.Settings.title)) },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.About)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Go to About Screen",
                            tint = StocksDarkPrimaryText
                        )
                    }
                }
            )
        }
    ) { padding ->


        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Timber.d("$viewModel")
        }
    }
}
