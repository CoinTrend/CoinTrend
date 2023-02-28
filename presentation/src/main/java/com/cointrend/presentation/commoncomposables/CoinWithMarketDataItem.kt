package com.cointrend.presentation.commoncomposables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.customcomposables.LineChart
import com.cointrend.presentation.customcomposables.sharedelements.SharedElement
import com.cointrend.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.cointrend.presentation.models.BaseCoinWithMarketDataUiItem
import com.cointrend.presentation.models.CoinWithMarketDataUiItem
import com.cointrend.presentation.models.CoinWithShimmeringMarketDataUiItem
import com.cointrend.presentation.models.DataPoint
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.theme.PositiveTrend
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinWithMarketDataItem(
    modifier: Modifier = Modifier,
    item: () -> BaseCoinWithMarketDataUiItem,
    sharedElementScreenKey: () -> String,
    onCoinItemClick: () -> Unit,
) {
    Timber.d("CoinItem recomposition")

    val coroutineScope = rememberCoroutineScope()

    // The setup of the SharedElement Composable is only set
    // when the user taps on the card.
    val setupSharedElement = rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
            .wrapContentHeight()
            .semantics(mergeDescendants = true) {},
        onClick = {
            if (setupSharedElement.value) {
                onCoinItemClick()
            } else {
                setupSharedElement.value = true

                coroutineScope.launch {
                    delay(50L)
                    onCoinItemClick()
                }
            }
        },
        colors = CardDefaults.cardColors(
            contentColor = StocksDarkPrimaryText
        ),
        shape = MaterialTheme.shapes.large
    ) {

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Timber.d("CoinItem card recomposition ${item().symbol} $sharedElementScreenKey")

            val shouldShimmerMarketData by remember(key1 = item()) {
                mutableStateOf(item() is CoinWithShimmeringMarketDataUiItem)
            }

            val showChart = remember(key1 = item().id) {
                mutableStateOf(false)
            }

            val animateChart = remember(key1 = item().id) {
                mutableStateOf(false)
            }

            if (setupSharedElement.value) {
                SharedElement(key = item().imageUrl, screenKey = sharedElementScreenKey()) {
                    CoinIcon(imageUrl = item().imageUrl)
                }
            } else {
                CoinIcon(imageUrl = item().imageUrl)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = item().name,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        modifier = Modifier.shimmer(visible = shouldShimmerMarketData),
                        shape = MaterialTheme.shapes.extraSmall,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(
                            text = item().marketCapRank,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 1.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = item().symbol,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.CenterEnd
            ) {

                if (showChart.value) {
                    androidx.compose.animation.AnimatedVisibility(visible = animateChart.value) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LineChart(
                                modifier = Modifier.size(width = 48.dp, height = 29.dp),
                                data = item().sparklineData,
                                graphColor = item().trendColor,
                                showDashedLine = true
                            )

                            // Invisible text with max price size to determine the max possible size of this column
                            Text(
                                text = "$100,000.00",
                                modifier = Modifier.alpha(0f)
                            )
                        }

                    }
                }

                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .shimmer(visible = shouldShimmerMarketData),
                        text = item().price,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.padding(1.dp))

                    Card(
                        modifier = Modifier
                            .requiredWidth(72.dp)
                            .shimmer(visible = shouldShimmerMarketData),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = item().trendColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = item().priceChangePercentage,
                            style = MaterialTheme.typography.bodyMedium,
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


            LaunchedEffect(key1 = item().id, block = {
                if (!showChart.value) {
                    delay(900L)
                    showChart.value = true
                    delay(100L)
                    animateChart.value = true
                }
            })

        }

    }

}


@Preview(showBackground = true)
@Composable
private fun CoinWithMarketDataItemPreview() {

    CoinTrendTheme {
        SharedElementsRoot {
            CoinWithMarketDataItem(
                item = {
                    CoinWithMarketDataUiItem(
                        id = "",
                        name = "Bitcoin BitcoinBitcoinBitcoinBitcoinBitcoin",
                        symbol = "BTC",
                        imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579",
                        marketCapRank = "1",
                        price = "19.300,00 $",
                        priceChangePercentage = "+3.45%",
                        trendColor = PositiveTrend,
                        sparklineData = persistentListOf(
                            DataPoint(y = 1.0, null, null),
                            DataPoint(y = 3.0, null, null),
                            DataPoint(y = 0.0, null, null),
                            DataPoint(y = 4.0, null, null),
                            DataPoint(y = 1.0, null, null),
                            DataPoint(y = 2.0, null, null)
                        ),
                        lastUpdate = ""
                    )
                },
                sharedElementScreenKey = { "" },
                onCoinItemClick = {}
            )
        }
    }
}