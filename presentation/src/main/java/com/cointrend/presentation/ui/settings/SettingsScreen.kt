package com.cointrend.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.commoncomposables.*
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.theme.*
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
                    title = "Default Price Change Period",
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
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .alpha(.2f),
            color = StocksDarkSecondaryText
        )
    }
}

