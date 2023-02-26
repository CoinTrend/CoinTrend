package com.cointrend.presentation.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.cointrend.domain.models.TimeRange

enum class TimeRangeUi(
    val timeRange: TimeRange,
    val uiString: String
) {
    Day(TimeRange.Day, "24H"),
    Week(TimeRange.Week, "1W"),
    Month(TimeRange.Month, "1M"),
    ThreeMonths(TimeRange.ThreeMonths, "3M"),
    Year(TimeRange.Year, "1Y"),
    Max(TimeRange.Max, "MAX")
}

enum class BottomNavigationItem(val route: Screen, val icon: ImageVector, val title: String) {

    Market(
        route = Screen.CoinsList,
        icon = Icons.Default.TrendingUp,
        title = "Market"
    ),
    Favourites(
        route = Screen.FavouriteCoinsList,
        icon = Icons.Default.Star,
        title = "Favourites"
    ),
    Search(
        route = Screen.Search,
        icon = Icons.Default.Search,
        title = "Search"
    )

}