package com.cointrend.presentation.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.cointrend.domain.models.TimeRange
import com.cointrend.presentation.R

enum class MarketChartTimeRangeUi(
    val timeRange: TimeRange,
    val uiString: String
) {
    Day(TimeRange.Day, "24H"),
    Week(TimeRange.Week, "1W"),
    Month(TimeRange.Month, "1M"),
    SixMonths(TimeRange.SixMonths, "6M"),
    Year(TimeRange.Year, "1Y"),
    //Max(TimeRange.Max, "MAX") // Not supported by free CoinGecko API anymore
}

// Enum used in SettingsScreen to allow the user to choose the default price change period
// to be displayed in the coins lists.
// It contains less options than TimeRange because the CoinGecko API coins/markets doesn't support
// the other values when retrieving the coins lists. The other values are supported by the market_chart API instead.
enum class SettingsPriceChangePeriodUi(
    val timeRange: TimeRange,
    val uiString: String
) {
    Day(TimeRange.Day, "24 Hours"),
    Week(TimeRange.Week, "1 Week"),
    Month(TimeRange.Month, "1 Month"),
    SixMonths(TimeRange.SixMonths, "6 Months"),
    Year(TimeRange.Year, "1 Year")
}

enum class BottomNavigationItem(val route: Screen, val icon: ImageVector, @StringRes val title: Int) {

    Market(
        route = Screen.CoinsList,
        icon = Icons.AutoMirrored.Filled.TrendingUp,
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
    ),
    Settings(
        route = Screen.Settings,
        icon = Icons.Default.Settings,
        title = R.string.settings
    )

}