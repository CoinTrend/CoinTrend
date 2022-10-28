package com.cointrend.presentation.ui.coinslist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.cointrend.presentation.commoncomposables.CoinItemCompact
import com.cointrend.presentation.commoncomposables.CoinWithMarketDataItem
import com.cointrend.presentation.commoncomposables.SectionTitle
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsListScreen(
    navController: NavController<Screen>,
    viewModel: CoinsListViewModel = hiltViewModel()
) {

    //val context = LocalContext.current
    //val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val coinsListState = rememberLazyListState()

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
                title = {
                    PoweredByCoinGeckoText()
                },
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

                item {
                    SectionTitle(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        title = "Trending Coins"
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(viewModel.state.trendingCoinsList) { item ->
                            CoinItemCompact(
                                item = { item },
                                sharedElementScreenKey = { COINS_LIST_SCREEN_KEY }
                            ) {
                                goToCoinDetail(item)
                            }
                        }

                    }
                }

                item {
                    SectionTitle(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        title = "Top Coins"
                    )
                }

                viewModel.state.topCoinsList.forEach { item ->

                    item {
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

                }

                item { 
                    Spacer(modifier = Modifier.size(32.dp))
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

@Composable
private fun PoweredByCoinGeckoText(
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Powered by ",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall
        )
        Image(
            modifier = Modifier
                .requiredHeight(20.dp)
                .padding(top = 2.dp),
            painter = painterResource(id = R.drawable.ic_coingecko),
            contentDescription = null,
        )
    }
}

@Composable
private fun ErrorItem(
    modifier: Modifier,
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        OutlinedButton(
            onClick = { onRetryClick.invoke() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text(text = "Retry")
        }
    }
}