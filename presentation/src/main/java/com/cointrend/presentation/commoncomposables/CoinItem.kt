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
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinItem(
    modifier: Modifier = Modifier,
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

            if (setupSharedElement.value) {
                SharedElement(key = item().imageUrl, screenKey = sharedElementScreenKey()) {
                    CoinIcon(imageUrl = item().imageUrl)
                }
            } else {
                CoinIcon(imageUrl = item().imageUrl)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
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

}


@Preview(showBackground = true)
@Composable
private fun CoinItemPreview() {

    CoinTrendTheme {
        SharedElementsRoot {
            CoinItem(
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