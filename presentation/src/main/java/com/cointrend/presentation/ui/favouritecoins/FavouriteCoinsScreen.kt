package com.cointrend.presentation.ui.favouritecoins

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteCoinsScreen(
    navController: NavController<Screen>,
    viewModel: FavouriteCoinsViewModel = hiltViewModel()
) {

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

    val listState = rememberReorderableLazyListState(
        onMove = { from, to ->
            (from.key as? String)?.let { coinId ->
                viewModel.onCoinPositionReordered(
                    coinId = coinId,
                    fromIndex = from.index,
                    toIndex = to.index
                )
            }
        }
    )

    val isDraggingHappening = remember {
        derivedStateOf {
            Timber.d("isDraggingHappening ${listState.draggingItemKey != null}")
            listState.draggingItemKey != null
        }
    }


    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Favourites") },
                actions = {

                    val context = LocalContext.current
                    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://lightningaddress.com")) }
                    IconButton(
                        onClick = {  context.startActivity(intent)
                        }){ Icon(painter = painterResource(id = R.drawable.ic_donate),
                        contentDescription = "Open lightningaddress.com ",
                        modifier = Modifier.padding(start = 12.dp))
                    }

                    LastUpdateDateText(
                        modifier = Modifier.padding(end = 16.dp),
                        lastUpdateDate = viewModel.state.lastUpdateDate,
                        isRefreshing = automaticRefreshState.value
                    )
                }
            )
        }
    ) { innerPadding ->

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = swipeRefreshState.value),
            onRefresh = { viewModel.onSwipeRefresh() },
            modifier = Modifier.fillMaxSize(),
            swipeEnabled = !isDraggingHappening.value,
            indicatorPadding = innerPadding,
        ) {

            LazyColumn(
                modifier = Modifier
                    .reorderable(listState)
                    .detectReorderAfterLongPress(listState)
                    .fillMaxHeight(),
                state = listState.listState,
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(viewModel.state.favouriteCoinsList, key = { it.id }) { item ->

                    ReorderableItem(listState, key = item.id) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 32.dp else 0.dp)
                        val padding = animateDpAsState(if (isDragging) 16.dp else MainHorizontalPadding)

                        CoinWithMarketDataItem(
                            modifier = Modifier
                                .padding(horizontal = padding.value)
                                .shadow(elevation = elevation.value),
                            item = { item },
                            sharedElementScreenKey = { COINS_LIST_SCREEN_KEY },
                            onCoinItemClick = {
                                if (!isDraggingHappening.value) {
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
                            }
                        )
                    }
                }
            }
        }

        when (val state = viewModel.state.state) {
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

