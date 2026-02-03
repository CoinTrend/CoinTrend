package com.cointrend.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Screen : Parcelable {

    @Parcelize
    data object CoinsList : Screen

    @Parcelize
    data object FavouriteCoinsList : Screen

    @Parcelize
    data object Search : Screen

    @Parcelize
    data object Settings : Screen

    @Parcelize
    data object About : Screen

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen

}
