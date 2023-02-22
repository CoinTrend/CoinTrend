package com.cointrend.presentation.ui.coinslist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.*
import com.cointrend.presentation.models.*
import com.cointrend.presentation.ui.search.SearchViewModel
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popAll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController<Screen>,
) {
    val focusManager = LocalFocusManager.current

   // val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier,
     //   snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {IconButton(onClick = {
                    navController.popAll()}) {
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
                item {
                    SectionTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), title = "About",)
                    CoinTrend()
                    SectionTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), title = "Source Code",)
                    CardViewInfo(cardView_title = "GitHub", cardView_info = "https://github.com/CoinTrend/CoinTrend" , cardView_link = "https://github.com/CoinTrend/CoinTrend" , cardView_image = R.drawable.ic_github)
                    SectionTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), title = "Contact",)
                    CardViewInfo(cardView_title = "Mail", cardView_info = "cointrend.info@gmail.com" , cardView_link = " " , cardView_image = R.drawable.ic_gmail)
                    Spacer(modifier = Modifier.size(6.dp))
                    CardViewInfo(cardView_title = "Github Issue", cardView_info = "https://github.com/CoinTrend/CoinTrend/issues", cardView_link = "https://github.com/CoinTrend/CoinTrend/issues", cardView_image = R.drawable.ic_github)
                    SectionTitle(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), title = "Support",)
                    CardViewInfo(cardView_title = "Donate", cardView_info = "bc1qszr4jv77n737569vhsdwgq3zc2x47n39mlq82f", cardView_link = "https://github.com/CoinTrend/CoinTrend#bitcoin", cardView_image = R.drawable.ic_donate)
                    Spacer(modifier = Modifier.size(6.dp))
                    CardViewInfo(cardView_title = "Rate on Google Play", cardView_info = "Rate this application", cardView_link = "https://play.google.com/store/apps/details?id=com.cointrend&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1" , cardView_image = R.drawable.ic_google_play)
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
                .padding(top = 2.dp, start = 150.dp),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 2.dp, start = 150.dp),
            text = "CoinTrend",
            fontSize = 23.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

//Card with icon and title, open url (github,issue,googleplay,bitcoin)
@Composable
fun CardViewInfo(
    cardView_title: String,
    cardView_info: String,
    cardView_link: String,
    cardView_image: Int,
) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(cardView_link)) }
    Card(
        modifier = Modifier
            .width(390.dp)
            .clickable {
                if(cardView_link == " "){
                    println("Do nothing for gmail")
                    /*
                    Maybe open Gmail for
                    send mails
                     */
                }
                else{
                    context.startActivity(intent)
                }
            }
            .padding(start = 8.dp),
        shape = RoundedCornerShape(size = 23.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(size = 25.dp),
                painter = painterResource(id = cardView_image),
                contentDescription = "Image pic",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(width = 18.dp)) // gap between image and text
            Column {
                Text(
                    text = cardView_title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = cardView_info,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}