package com.cointrend.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.commoncomposables.SectionTitle
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.cointrend.presentation.theme.StocksDarkSecondaryText
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate

private val defaultHorizontalPadding = 16.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController<Screen>,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    DisposableEffect(key1 = null) {
        viewModel.init()

        onDispose {
            viewModel.onDispose()
        }
    }

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = BottomNavigationItem.Settings.title)) },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.About)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Go to About Screen",
                            tint = StocksDarkPrimaryText
                        )
                    }
                }
            )
        }
    ) { padding ->


        LazyColumn(
            Modifier
                .fillMaxSize(),
                //.padding(padding),
            contentPadding = padding
        ) {
            item {
                SectionTitle(
                    title = "Price Change Percentage Period",
                    modifier = Modifier.padding(horizontal = defaultHorizontalPadding, vertical = 8.dp)
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .background(
                            color = StocksDarkBackgroundTranslucent,
                            shape = MaterialTheme.shapes.large
                        )
                        .selectableGroup(),
                ) {

                    val priceChangePeriodOptions = viewModel.state.priceChangePeriodOptions

                    priceChangePeriodOptions.forEachIndexed { index, item ->
                        SectionSettingItem(
                            modifier = Modifier.selectable(
                                selected = item == viewModel.state.selectedPriceChangePeriod,
                                onClick = { viewModel.onPriceChangePeriodSelected(priceChangePeriodUi = item) },
                                role = Role.RadioButton
                            ),
                            name = item.uiString,
                            selected = item == viewModel.state.selectedPriceChangePeriod,
                            showDivider = index != priceChangePeriodOptions.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionSettingItem(
    modifier: Modifier = Modifier,
    name: String,
    selected: Boolean,
    showDivider: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            color = StocksDarkPrimaryText,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.size(8.dp))

        RadioButton(
            selected = selected,
            colors = RadioButtonDefaults.colors(
                unselectedColor = StocksDarkSecondaryText,
                selectedColor = StocksDarkPrimaryText
            ),
            onClick = null // null recommended for accessibility with screenreaders
        )

    }

    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .alpha(.2f),
            color = StocksDarkSecondaryText
        )
    }
}

