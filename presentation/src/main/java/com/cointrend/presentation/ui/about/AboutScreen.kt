package com.cointrend.presentation.ui.coinslist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.*
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import com.cointrend.presentation.ui.coindetail.SectionInfoItem


private val defaultHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    // navController: NavController<Screen>,
) {
    val focusManager = LocalFocusManager.current

    // val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier,
        //   snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {IconButton(onClick = {
                    System.out.println("Go Back")
                    //navController.navigate(Screen.CoinsList)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                        contentDescription = "Return to previous screen",
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() },
                        onPress = { focusManager.clearFocus() },
                        onLongPress = { focusManager.clearFocus() },
                        onDoubleTap = { focusManager.clearFocus() }
                    )
                }
        ) {
            LazyColumn() {
                // The Logp
                item {
                    CoinTrend()
                }

                // Setion title 16dp SOURCE CODE
                item {
                    SectionTitle(title = "Source Code", modifier = Modifier.padding(defaultHorizontalPadding))
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
                        SectionInfoItemTest(name = "Github", info = "https://github.com/CoinTrend/CoinTrend", image = R.drawable.ic_github, showDivider = false)
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                // Setion title 16dp CONTACT
                item {
                    SectionTitle(title = "Contact", modifier = Modifier.padding(defaultHorizontalPadding))
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
                        SectionInfoItemTest(name = "Email", info = "cointrend.info@gmail.com", image = R.drawable.ic_gmail, showDivider = true)
                        SectionInfoItemTest(name = "Github Issues", info = "https://github.com/CoinTrend/CoinTrend/issues", image = R.drawable.ic_github, showDivider = false)
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(16.dp))
                }
                // Setion title 16dp SUPPORT
                item {
                    SectionTitle(title = "Support", modifier = Modifier.padding(defaultHorizontalPadding))
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
                        SectionInfoItemTest(name = "Donate Bitcoin", info = "https://github.com/CoinTrend/CoinTrend#bitcoin", image = R.drawable.ic_donate, showDivider = true)
                        SectionInfoItemTest(name = "Rate on Google Play", info = "", image = R.drawable.ic_google_play, showDivider = false)
                    }
                }
                item {
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}


//Logo CoinTrend Like Github Page
@Composable
private fun CoinTrend(
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Image(
            modifier = Modifier
                .requiredHeight(120.dp)
                .padding(top = 2.dp, start = 115.dp),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, start = 115.dp),
            text = "CoinTrend",
            fontSize = 38.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun SectionInfoItemTest(
    name: String,
    info: String,
    image: Int,
    showDivider: Boolean,

    ) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {



        Image(
            modifier = Modifier
                .size(size = 30.dp),
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column() {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = info,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(start = 0.dp, end = 4.dp, top = 1.dp, bottom = 1.dp),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
    if (showDivider) {
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .alpha(.2f),
            color = Color(0xffffffff)
        )
    }
}








// sauvegarde teste
/*




@Composable
fun SectionInfoItemTest(
    name: String,
    value: String,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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




 */
