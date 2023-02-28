package com.cointrend.presentation.commoncomposables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.theme.PositiveTrend
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinWithMarketDataItemCompact(
    item: () -> BaseCoinWithMarketDataUiItem,
    sharedElementScreenKey: () -> String,
    onCoinItemClick: () -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()

    // The setup of the SharedElement Composable is only set
    // when the user taps on the card.
    val setupSharedElement = rememberSaveable {
        mutableStateOf(false)
    }
    
    Card(
        modifier = Modifier
            .width(112.dp)
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
        shape = MaterialTheme.shapes.extraLarge
    ) {

        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (setupSharedElement.value) {
                SharedElement(key = item().imageUrl, screenKey = sharedElementScreenKey()) {
                    CoinIcon(imageUrl = item().imageUrl)
                }
            } else {
                CoinIcon(imageUrl = item().imageUrl)
            }

            // TODO: handle shimmer if market data is missing
            /*
            val shouldShimmerMarketData by remember {
                mutableStateOf(item() is CoinWithShimmeringMarketDataUiItem)
            }

             */

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
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
                    style = MaterialTheme.typography.bodyLarge,
                    //color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = item().name,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = StocksDarkSecondaryText,
                style = MaterialTheme.typography.bodySmall
            )

            LineChart(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                data = item().sparklineData,
                graphColor = item().trendColor,
                showDashedLine = true
            )


            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = item().price, // TODO: handle shimmer if market data is missing
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Card(
                modifier = Modifier.sizeIn(minWidth = 72.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = item().trendColor, // TODO: handle shimmer if market data is missing
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = item().priceChangePercentage, // TODO: handle shimmer if market data is missing
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 1.dp)
                        .align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

        }

    }
}


@Preview(showBackground = true)
@Composable
private fun CoinItemCompactPreview() {
    CoinTrendTheme {
        SharedElementsRoot {
            CoinWithMarketDataItemCompact(
                item = {
                    CoinWithMarketDataUiItem(
                        id = "",
                        name = "Bitcoin",
                        symbol = "BTC",
                        imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579",
                        marketCapRank = "1",
                        price = "19.300,00 $",
                        priceChangePercentage = "+3.45%",
                        trendColor = PositiveTrend,
                        sparklineData = persistentListOf(),
                        lastUpdate = ""
                    )
                },
                sharedElementScreenKey = { "" },
                onCoinItemClick = {}
            )
        }
    }
}