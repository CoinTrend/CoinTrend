package com.cointrend.presentation.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.*
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.*
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop


private val defaultHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController<Screen>,
) {

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.pop()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                            contentDescription = "Return to previous screen",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
            )
        }
    ) { padding ->


        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                CoinTrend(modifier = Modifier.fillMaxWidth())
            }

            item {
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                SectionTitle(
                    title = "Source Code", modifier = Modifier.padding(
                        defaultHorizontalPadding
                    )
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = StocksDarkSelectedChip,
                            shape = MaterialTheme.shapes.large
                        ),
                ) {
                    SectionInfoItemAbout(
                        name = "GitHub",
                        info = "https://github.com/CoinTrend/CoinTrend",
                        image = R.drawable.ic_github,
                        showDivider = true
                    )
                    SectionInfoItemAbout(
                        name = "Changelog",
                        info = "https://github.com/CoinTrend/CoinTrend/releases",
                        image = R.drawable.ic_github,
                        showDivider = false
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                SectionTitle(
                    title = "Contact", modifier = Modifier.padding(
                        defaultHorizontalPadding
                    )
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = StocksDarkSelectedChip,
                            shape = MaterialTheme.shapes.large
                        ),
                ) {
                    SectionInfoItemAbout(
                        name = "Email",
                        info = "cointrend.info@gmail.com",
                        image = R.drawable.ic_mail,
                        showDivider = true
                    )
                    SectionInfoItemAbout(
                        name = "GitHub Issues",
                        info = "https://github.com/CoinTrend/CoinTrend/issues",
                        image = R.drawable.ic_github,
                        showDivider = false
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                SectionTitle(
                    title = "Support", modifier = Modifier.padding(
                        defaultHorizontalPadding
                    )
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = StocksDarkSelectedChip,
                            shape = MaterialTheme.shapes.large
                        ),
                ) {
                    SectionInfoItemAbout(
                        name = "Donate",
                        info = "https://github.com/CoinTrend#support",
                        image = R.drawable.ic_donate,
                        showDivider = true
                    )
                    SectionInfoItemAbout(
                        name = "Rate on Google Play",
                        info = "",
                        image = R.drawable.ic_google_play,
                        showDivider = false
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(32.dp))
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .requiredHeight(120.dp)
                .padding(top = 2.dp),
            contentDescription = "CoinTrend logo icon",
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.ic_launcher),
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = "CoinTrend",
            fontSize = MaterialTheme.typography.bodyLarge.lineHeight,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun SectionInfoItemAbout(
    name: String,
    info: String,
    image: Int,
    showDivider: Boolean,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size = 30.dp)
                .clip(shape = MaterialTheme.shapes.medium),
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = info,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    if (showDivider) {
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .alpha(.2f),
            color = Color.White
        )
    }
}