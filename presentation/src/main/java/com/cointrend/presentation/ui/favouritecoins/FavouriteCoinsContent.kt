package com.cointrend.presentation.ui.favouritecoins

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.commoncomposables.CoinWithMarketDataItem
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.models.FavouriteCoinsState
import com.cointrend.presentation.models.CoinsListUiState
import com.cointrend.presentation.theme.MainHorizontalPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteCoinsContent(
    uiState: FavouriteCoinsState,
    onNavigateToCoinDetail: (CoinUiItem) -> Unit,
    onSwipeRefresh: () -> Unit,
    onRetryClick: () -> Unit,
    onCoinPositionReordered: (coinId: String, fromIndex: Int, toIndex: Int) -> Unit
) {
    val swipeRefreshState = remember {
        derivedStateOf {
            uiState.state == CoinsListUiState.Refreshing(isAutomaticRefresh = false)
        }
    }

    val automaticRefreshState = remember {
        derivedStateOf {
            uiState.state == CoinsListUiState.Refreshing(isAutomaticRefresh = true)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val listState = rememberReorderableLazyListState(
        onMove = { from, to ->
            (from.key as? String)?.let { coinId ->
                onCoinPositionReordered(
                    coinId,
                    from.index,
                    to.index
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
                title = { Text(stringResource(id = BottomNavigationItem.Favourites.title)) },
                actions = {
                    LastUpdateDateText(
                        modifier = Modifier.padding(end = 16.dp),
                        lastUpdateDate = uiState.lastUpdateDate,
                        isRefreshing = automaticRefreshState.value
                    )
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = swipeRefreshState.value),
            onRefresh = onSwipeRefresh,
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
                    start = 0.dp,
                    end = 0.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.favouriteCoinsList, key = { it.id }) { item ->
                    ReorderableItem(listState, key = item.id) { isDragging ->
                        val elevation = animateDpAsState(if (isDragging) 32.dp else 0.dp)
                        val padding = animateDpAsState(if (isDragging) 16.dp else MainHorizontalPadding)

                        CoinWithMarketDataItem(
                            modifier = Modifier
                                .padding(horizontal = padding.value)
                                .shadow(elevation = elevation.value),
                            item = { item },
                            onCoinItemClick = {
                                if (!isDraggingHappening.value) {
                                    onNavigateToCoinDetail(
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

                if (uiState.favouriteCoinsList.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp, start = 48.dp, end = 48.dp),
                            text = "Your favourite coins will be shown here",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        when(val state = uiState.state) {
            is CoinsListUiState.Error -> {
                LaunchedEffect(key1 = snackbarHostState) {
                    val result = snackbarHostState.showSnackbar(
                        message = state.message,
                        actionLabel = "Retry",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        onRetryClick()
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