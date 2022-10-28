package com.cointrend.presentation.ui.favouritecoins

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.CoinWithMarketDataItem
import com.cointrend.presentation.models.COINS_LIST_SCREEN_KEY
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.MainHorizontalPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteCoinsScreen(
    navController: NavController<Screen>,
    viewModel: FavouriteCoinsViewModel = hiltViewModel()
) {

    val coinsListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isButtonVisible = remember {
        derivedStateOf {
            coinsListState.firstVisibleItemIndex >= 7
        }
    }

    val swipeRefreshState = remember {
        derivedStateOf {
            viewModel.state.state == CoinsListUiState.Refreshing(isAutomaticRefresh = false)
        }
    }

    val automaticRefreshState = remember {
        derivedStateOf {
            viewModel.state.state == CoinsListUiState.Refreshing(isAutomaticRefresh = true)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val goToCoinDetail: (CoinUiItem) -> Unit = {
        navController.navigate(
            Screen.CoinDetail(
                coinDetailMainData = it
            )
        )
    }


    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Favourites") },
                actions = {
                    LastUpdateDateText(
                        modifier = Modifier.padding(end = 16.dp),
                        lastUpdateDate = viewModel.state.lastUpdateDate,
                        isRefreshing = automaticRefreshState.value
                    )
                },
                //scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = swipeRefreshState.value),
            onRefresh = { viewModel.onSwipeRefresh() },
            modifier = Modifier.fillMaxSize(),
            indicatorPadding = innerPadding,
        ) {

            LazyColumn(
                state = coinsListState,
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(viewModel.state.favouriteCoinsList, key = { it.id }) { item ->
                    CoinWithMarketDataItem(
                        modifier = Modifier.padding(horizontal = MainHorizontalPadding),
                        item = { item },
                        sharedElementScreenKey = { COINS_LIST_SCREEN_KEY },
                        onCoinItemClick = {
                            goToCoinDetail(
                                with(item) {
                                    CoinUiItem(
                                        id = id,
                                        name = name,
                                        symbol = symbol,
                                        imageUrl = imageUrl,
                                        marketCapRank = marketCapRank,
                                    )
                                }
                            )
                        }
                    )
                }

                item { 
                    Spacer(modifier = Modifier.size(32.dp))
                }
                
            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            AnimatedVisibility(
                visible = isButtonVisible.value,
                //enter = fadeIn(),
                //exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            coinsListState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_double_up_arrow), contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    //Text(text = "Back to top", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

        }

        when(val state = viewModel.state.state) {
            is CoinsListUiState.Error -> {
                LaunchedEffect(key1 = snackbarHostState) {

                    val result = snackbarHostState.showSnackbar(
                        message = state.message,
                        actionLabel = "Retry",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onRetryClick()
                    }

                }

            }
            else -> {
                // If Idle do nothing whereas Refreshing is handled by SwipeRefresh
            }
        }

    }

}

@Composable
private fun LastUpdateDateText(
    modifier: Modifier = Modifier,
    lastUpdateDate: String,
    isRefreshing: Boolean
) {
    val showLastUpdate = remember(lastUpdateDate) {
        lastUpdateDate.isNotEmpty()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = "Last update",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Medium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRefreshing) "Updating... " else if (showLastUpdate) lastUpdateDate else "",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium
            )

            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

