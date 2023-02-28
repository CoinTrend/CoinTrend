package com.cointrend.presentation.commoncomposables

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.cointrend.presentation.theme.StocksDarkPrimaryText
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.shimmer(
    visible: Boolean
): Modifier = composed {
    if (visible) {
        this.placeholder(
            visible = true,
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = StocksDarkPrimaryText
            )
        )
    } else this
}