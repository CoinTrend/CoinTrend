package com.cointrend.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screen : Parcelable {

    @Parcelize
    object CoinsList : Screen()

    @Parcelize
    object FavouriteCoinsList : Screen()

    @Parcelize
    object Search : Screen()

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen()

}
