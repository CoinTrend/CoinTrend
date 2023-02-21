package com.cointrend.presentation.ui.about

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.cointrend.presentation.ui.coinslist.CoinsListViewModel




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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
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



    val snackbarHostState = remember { SnackbarHostState() }



    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.pop()}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                            contentDescription = "Return to previous screen",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                actions = {
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
                        title = "About"
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {



                    }
                }

                item {
                    CoinTrend()
                }

                item {
                    SectionTitle(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        title = "Source Code"
                    )

                }
                item {
                    Spacer(modifier = Modifier.size(32.dp))
                    Card(
                        modifier = Modifier
                            .sizeIn(minWidth = 72.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "test",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                                .align(Alignment.End),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
                }


                item {
                    SectionTitle(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        title = "Contact"
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(32.dp))
                    Card(
                        modifier = Modifier
                            .sizeIn(minWidth = 72.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "test",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                                .align(Alignment.End),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
                }

                item {
                    SectionTitle(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                        title = "Support"
                    )
                }
                item {
                    Spacer(modifier = Modifier.size(32.dp))
                    Card(
                        modifier = Modifier
                            .sizeIn(minWidth = 72.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "test",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                                .align(Alignment.End),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(32.dp))
                    Card(
                        modifier = Modifier
                            .sizeIn(minWidth = 72.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "test",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 1.dp)
                                .align(Alignment.End),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
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
private fun CoinTrend(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .requiredHeight(120.dp)
                .padding(top = 2.dp),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "CoinTrend",
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}









