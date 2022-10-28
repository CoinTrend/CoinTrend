package com.cointrend.presentation.ui.search

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.cointrend.presentation.commoncomposables.CoinItem
import com.cointrend.presentation.commoncomposables.LoadingItem
import com.cointrend.presentation.models.COINS_LIST_SCREEN_KEY
import com.cointrend.presentation.models.Screen
import com.cointrend.presentation.models.SearchUiState
import com.cointrend.presentation.theme.*
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController<Screen>,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp, top = 24.dp, bottom = 16.dp, start = 8.dp),
                value = viewModel.searchTextFieldState.text,
                onValueChange = {
                    viewModel.onSearchValueChanged(text = it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    autoCorrect = false,
                    capitalization = KeyboardCapitalization.None
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = StocksDarkSecondaryText
                    )

                },
                trailingIcon = {
                    if (viewModel.searchTextFieldState.isTrailingIconVisible) {
                        IconButton(onClick = { viewModel.onSearchValueChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Cancel",
                                tint = StocksDarkSecondaryText
                            )
                        }
                    }
                },
                textStyle = TextStyle(
                    color = StocksDarkPrimaryText
                ),
                placeholder = {
                    Text(
                        color = StocksDarkSecondaryText,
                        text = viewModel.searchTextFieldState.placeholderText
                    )
                },
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = StocksDarkPrimaryText,
                    containerColor = StocksDarkSelectedChip,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            when(val state = viewModel.searchUiState) {
                is SearchUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.size(8.dp))
                        }

                        state.coins.forEach {
                            item {
                                CoinItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    item = { it },
                                    sharedElementScreenKey = { COINS_LIST_SCREEN_KEY },
                                ) {
                                    navController.navigate(
                                        Screen.CoinDetail(
                                            coinDetailMainData = it
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.size(32.dp))
                        }
                    }
                }

                is SearchUiState.Error -> {
                    LaunchedEffect(key1 = snackbarHostState) {

                        val result = snackbarHostState.showSnackbar(
                            message = state.message,
                            actionLabel = "Retry",
                            withDismissAction = true,
                            duration = SnackbarDuration.Indefinite
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onRetryClick()
                        }

                    }
                }
                is SearchUiState.Loading -> {
                    LoadingItem(modifier = Modifier.fillMaxSize())
                }
            }



        }

    }

}