package com.cointrend.presentation.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.cointrend.domain.models.TimeRange
import com.cointrend.presentation.R

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

enum class BottomNavigationItem(val route: Screen, val icon: ImageVector, @StringRes val title: Int) {

    Market(
        route = Screen.CoinsList,
        icon = Icons.Default.TrendingUp,
        title = R.string.market
    ),
    Favourites(
        route = Screen.FavouriteCoinsList,
        icon = Icons.Default.Star,
        title = R.string.favorite
    ),
    Search(
        route = Screen.Search,
        icon = Icons.Default.Search,
        title = R.string.search
    )

}