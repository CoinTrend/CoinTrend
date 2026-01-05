package com.cointrend.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.commoncomposables.SectionTitle
import com.cointrend.presentation.models.BottomNavigationItem
import com.cointrend.presentation.models.SettingsPriceChangePeriodUi
import com.cointrend.presentation.models.SettingsState
import com.cointrend.presentation.theme.StocksDarkBackgroundTranslucent
import com.cointrend.presentation.theme.StocksDarkPrimaryText

private val defaultHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsState,
    onNavigateToAbout: () -> Unit,
    onPriceChangePeriodSelected: (SettingsPriceChangePeriodUi) -> Unit
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = BottomNavigationItem.Settings.title)) },
                actions = {
                    IconButton(onClick = onNavigateToAbout) {
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
            Modifier.fillMaxSize(),
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
                    val priceChangePeriodOptions = uiState.priceChangePeriodOptions

                    priceChangePeriodOptions.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = item == uiState.selectedPriceChangePeriod,
                                    onClick = { onPriceChangePeriodSelected(item) },
                                    role = Role.RadioButton
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                modifier = Modifier.alpha(if (item == uiState.selectedPriceChangePeriod) 1f else 0.6f),
                                selected = item == uiState.selectedPriceChangePeriod,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item.uiString,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

        }
    }
}