package com.cointrend.presentation.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.R
import com.cointrend.presentation.commoncomposables.LoadingItem
import com.cointrend.presentation.theme.StocksDarkBackground
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun SplashScreen(
    showLoading: Boolean = false
) {
    val isLoadingVisible = remember {
        mutableStateOf(false)
    }

    if (showLoading) {
        LaunchedEffect(key1 = null) {
            delay(1500L)
            isLoadingVisible.value = true
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    StocksDarkBackground,
                    StocksDarkBackgroundTranslucent
                )
            )
        )
        .clickable(
            enabled = false,
            onClickLabel = null,
            role = null
        ) {},
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo"
        )

        AnimatedVisibility(visible = isLoadingVisible.value) {
            LoadingItem(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                text = "Initializing data..."
            )
        }

    }

}