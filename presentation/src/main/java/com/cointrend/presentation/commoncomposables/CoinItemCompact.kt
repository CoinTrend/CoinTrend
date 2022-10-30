package com.cointrend.presentation.commoncomposables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.customcomposables.sharedelements.SharedElement
import com.cointrend.presentation.customcomposables.sharedelements.SharedElementsRoot
import com.cointrend.presentation.models.CoinUiItem
import com.cointrend.presentation.theme.CoinTrendTheme
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinItemCompact(
    item: () -> CoinUiItem,
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
            .width(120.dp)
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
                .padding(vertical = 8.dp, horizontal = 12.dp)
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

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = item().name,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.size(1.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
            CoinItemCompact(
                item = {
                    CoinUiItem(
                        id = "",
                        name = "Bitcoin",
                        symbol = "BTC",
                        imageUrl = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1547033579",
                        marketCapRank = "1"
                    )
                },
                sharedElementScreenKey = { "" },
                onCoinItemClick = {}
            )
        }
    }
}