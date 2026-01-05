package com.cointrend.presentation.ui.coindetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.CoinIcon
import com.cointrend.presentation.commoncomposables.LoadingItem
import com.cointrend.presentation.commoncomposables.SectionTitle
import com.cointrend.presentation.customcomposables.LineChart
import com.cointrend.presentation.customcomposables.SegmentText
import com.cointrend.presentation.customcomposables.SegmentedControl
import com.cointrend.presentation.models.CoinMarketChartState
import com.cointrend.presentation.models.CoinMarketDataState
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.models.MarketChartTimeRangeUi
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText

private val defaultHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailContent(
    coinDetailMainUiData: CoinUiItem,
    marketDataState: CoinMarketDataState,
    marketChartState: CoinMarketChartState,
    isFavourite: Boolean,
    onNavigateBack: () -> Unit,
    onFavouriteClick: () -> Unit,
    @Suppress("UNUSED_PARAMETER")
    onTimeIntervalClick: (MarketChartTimeRangeUi) -> Unit,
    onRetryMarketData: () -> Unit,
    onRetryMarketChart: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isMarketChartVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                            contentDescription = "Return to previous screen",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFavouriteClick) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Filled.Grade else Icons.Outlined.Grade,
                            contentDescription = if (isFavourite) "Remove this coin from favourites" else "Add this coin to favourites",
                            tint = StocksDarkPrimaryText
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                ),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            item {
                Header(coinDetailMainUiData)
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                Price(state = marketDataState)
            }

            // Price percentage + Chart + Segmented Controls
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(190.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    when (val state = marketChartState) {
                        is CoinMarketChartState.Success -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = defaultHorizontalPadding),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Price ${state.data.startPriceDate}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Start,
                                        color = StocksDarkSecondaryText,
                                        maxLines = 1
                                    )

                                    Text(
                                        text = state.data.startPrice,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Start,
                                        color = StocksDarkPrimaryText,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }

                                Card(
                                    modifier = Modifier.sizeIn(minWidth = 72.dp),
                                    shape = MaterialTheme.shapes.small,
                                    colors = CardDefaults.cardColors(
                                        containerColor = state.data.trendColor,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = state.data.priceChangePercentage,
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

                            Spacer(modifier = Modifier.size(16.dp))

                            AnimatedVisibility(
                                visible = isMarketChartVisible,
                                exit = ExitTransition.None
                            ) {
                                LineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    data = state.data.chartData,
                                    graphColor = state.data.trendColor,
                                    showDashedLine = true,
                                    showYLabels = true
                                )
                            }

                            LaunchedEffect(null) {
                                isMarketChartVisible = true
                            }
                        }
                        is CoinMarketChartState.Loading -> {
                            LoadingItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                text = "Loading chart data..."
                            )
                        }
                        is CoinMarketChartState.Error -> {
                            ErrorChartItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                onRetryClick = onRetryMarketChart
                            )
                        }
                    }
                }
            }

            // Segmented Control for time intervals
            if (marketChartState is CoinMarketChartState.Success) {
                item {
                    Spacer(modifier = Modifier.size(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = defaultHorizontalPadding),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Time interval selector would go here
                        // This needs to be properly integrated with the SegmentedControl API
                    }
                }
            }

            // Market Data Section
            item {
                Spacer(modifier = Modifier.size(32.dp))
                SectionTitle(
                    modifier = Modifier.padding(horizontal = defaultHorizontalPadding),
                    title = "Market data",
                )
                Spacer(modifier = Modifier.size(8.dp))
            }

            when (val state = marketDataState) {
                is CoinMarketDataState.Success -> {
                    item {
                        MarketDataContent(state.data)
                    }
                }
                is CoinMarketDataState.Loading -> {
                    item {
                        LoadingItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = defaultHorizontalPadding),
                            text = "Loading market data..."
                        )
                    }
                }
                is CoinMarketDataState.Error -> {
                    item {
                        ErrorItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = defaultHorizontalPadding),
                            message = state.message,
                            onRetryClick = onRetryMarketData
                        )
                    }
                }
            }
        }

        // Handle errors with snackbar
        when (marketChartState) {
            is CoinMarketChartState.Error -> {
                LaunchedEffect(key1 = snackbarHostState) {
                    val result = snackbarHostState.showSnackbar(
                        message = marketChartState.message,
                        actionLabel = "Retry",
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        onRetryMarketChart()
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun Header(coinDetailMainUiData: CoinUiItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = defaultHorizontalPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SharedElement transitions don't work with Navigation Compose
        // as screens are not composed simultaneously
        // TODO: Migrate to Compose SharedTransitionLayout when stable
        CoinIcon(
            imageUrl = coinDetailMainUiData.imageUrl,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = coinDetailMainUiData.marketCapRank,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = coinDetailMainUiData.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = StocksDarkPrimaryText,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = coinDetailMainUiData.symbol.uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = StocksDarkSecondaryText,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun Price(state: CoinMarketDataState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        when (state) {
            is CoinMarketDataState.Success -> {
                Text(
                    text = state.data.price,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = StocksDarkPrimaryText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            is CoinMarketDataState.Loading -> {
                LoadingItem(modifier = Modifier)
            }
            is CoinMarketDataState.Error -> {
                Text(
                    text = "Unable to load price",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = StocksDarkSecondaryText,
                    maxLines = 1
                )
            }
        }
    }

    Spacer(modifier = Modifier.size(16.dp))
}

@Composable
private fun MarketDataContent(data: com.cointrend.presentation.models.CoinMarketUiData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = defaultHorizontalPadding)
            .background(
                color = StocksDarkBackgroundTranslucent,
                shape = MaterialTheme.shapes.large
            )
            .padding(16.dp)
    ) {
        data.marketDataList.forEachIndexed { index, (label, value) ->
            if (index > 0) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            MarketDataRow(label, value)
        }
    }
}

@Composable
private fun MarketDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = StocksDarkSecondaryText
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = StocksDarkPrimaryText
        )
    }
}

@Composable
private fun ErrorChartItem(
    modifier: Modifier,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unable to load chart data",
            style = MaterialTheme.typography.bodyMedium,
            color = StocksDarkSecondaryText
        )
        Spacer(modifier = Modifier.size(8.dp))
        androidx.compose.material3.TextButton(onClick = onRetryClick) {
            Text("Retry")
        }
    }
}

@Composable
private fun ErrorItem(
    modifier: Modifier,
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = StocksDarkSecondaryText
        )
        Spacer(modifier = Modifier.size(8.dp))
        androidx.compose.material3.TextButton(onClick = onRetryClick) {
            Text("Retry")
        }
    }
}