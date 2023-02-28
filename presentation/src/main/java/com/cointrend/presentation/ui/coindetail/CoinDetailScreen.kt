package com.cointrend.presentation.ui.coindetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.CoinIcon
import com.cointrend.presentation.commoncomposables.LoadingItem
import com.cointrend.presentation.commoncomposables.SectionTitle
import com.cointrend.presentation.customcomposables.LineChart
import com.cointrend.presentation.customcomposables.SegmentText
import com.cointrend.presentation.customcomposables.SegmentedControl
import com.cointrend.presentation.customcomposables.sharedelements.SharedElement
import com.cointrend.presentation.models.*
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.pop

private val defaultHorizontalPadding = 16.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinDetailMainUiData: CoinUiItem,
    navController: NavController<Screen>,
    viewModel: CoinDetailViewModel = hiltViewModel(
        defaultArguments = bundleOf(COIN_DETAIL_PARAMETER to coinDetailMainUiData)
    )
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.pop() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                            contentDescription = "Return to previous screen",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                actions = {
                    /*
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Localized description",
                            tint = Favourite
                        )
                    }

                     */

                    IconButton(
                        onClick = {
                            viewModel.onFavouriteButtonClick()
                        }
                    ) {
                        Icon(
                            imageVector = if (viewModel.state.isFavourite) Icons.Filled.Grade else Icons.Outlined.Grade,
                            contentDescription = if (viewModel.state.isFavourite) "Remove this coin from favourites" else "Add this coin to favourites",
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
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {

            item {
                Header(coinDetailMainUiData)
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                Price(state = viewModel.state.coinMarketDataState)
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

                    when (val state = viewModel.state.coinMarketChartState) {
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
                                    modifier = Modifier
                                        .sizeIn(minWidth = 72.dp),
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
                                visible = viewModel.state.isMarketChartVisible,
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
                                viewModel.showMarketChart()
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
                            Text(
                                text = "Error loading chart data.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(16.dp),
                                color = StocksDarkPrimaryText,
                                textAlign = TextAlign.Center
                            )

                            LaunchedEffect(key1 = snackbarHostState) {

                                val result = snackbarHostState.showSnackbar(
                                    message = state.message,
                                    actionLabel = "Retry",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onMarketChartErrorRetry()
                                }

                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                SegmentedControl(
                    viewModel.state.timeRangeOptions,
                    viewModel.state.timeRangeSelected,
                    onSegmentSelected = {
                        viewModel.onTimeRangeSelected(it)
                    },
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    SegmentText(it.uiString)
                }
            }

            item {
                Spacer(modifier = Modifier.size(16.dp))
                SectionTitle(
                    title = "Market Data",
                    modifier = Modifier.padding(defaultHorizontalPadding)
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = StocksDarkBackgroundTranslucent,
                            shape = MaterialTheme.shapes.large
                        ),
                ) {

                    when (val state = viewModel.state.coinMarketDataState) {
                        is CoinMarketDataState.Success -> {
                            val list = state.data.marketDataList

                            list.forEachIndexed { index, pair ->
                                SectionInfoItem(
                                    name = pair.first,
                                    value = pair.second,
                                    showDivider = index != list.lastIndex
                                )
                            }
                        }
                        is CoinMarketDataState.Error -> {
                            Text(
                                text = "Error loading market data.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(16.dp),
                                color = StocksDarkPrimaryText,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            LaunchedEffect(key1 = snackbarHostState) {

                                val result = snackbarHostState.showSnackbar(
                                    message = state.message,
                                    actionLabel = "Retry",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onMarketDataErrorRetry()
                                }

                            }
                        }
                        else -> {
                            LoadingItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(16.dp),
                                text = "Loading market data..."
                            )
                        }
                    }

                }
            }

        }

    }

}

@Composable
fun SectionInfoItem(
    name: String,
    value: String,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = StocksDarkSecondaryText,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = StocksDarkPrimaryText,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    if (showDivider) {
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .alpha(.2f),
            color = StocksDarkSecondaryText
        )
    }
}

@Composable
fun PriceText(
    modifier: Modifier,
    price: String?
) {
    Text(
        modifier = modifier.alpha(
            alpha = if (price == null) 0f else 1f
        ),
        textAlign = TextAlign.End,
        text = price ?: "000000000",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Medium,
        color = StocksDarkPrimaryText,
        maxLines = 1
    )
}

@Composable
fun Header(coinDetailMainUiData: CoinUiItem) {
    Row(
        modifier = Modifier
            .padding(horizontal = defaultHorizontalPadding)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SharedElement(key = coinDetailMainUiData.imageUrl, screenKey = COIN_DETAIL_SCREEN_KEY) {
            CoinIcon(
                imageUrl = coinDetailMainUiData.imageUrl,
                size = 50.dp,
                shape = MaterialTheme.shapes.large
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(
                text = coinDetailMainUiData.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = StocksDarkPrimaryText
            )
            Text(
                text = coinDetailMainUiData.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = StocksDarkSecondaryText
            )
        }

    }
}

@Composable
fun Price(state: CoinMarketDataState) {
    when (state) {
        is CoinMarketDataState.Success -> PriceText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = defaultHorizontalPadding),
            price = state.data.price
        )
        else -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            PriceText(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.End)
                    .padding(horizontal = defaultHorizontalPadding),
                price = null
            )
        }
    }
}