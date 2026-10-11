package com.cointrend.presentation.mappers

import com.cointrend.domain.models.LaunchingPage
import com.cointrend.presentation.models.Screen
import javax.inject.Inject

class LaunchingPageMapper @Inject constructor() {

    /** This function map LaunchingPage --> ::: Screen :::
     * com.cointrend.presentation.models.Screen
     *
     * for using in startDestination = (need Screen not LanuchingPage)Screen.CoinsList for example
     * **/
    fun mapScreen(launchingPage: LaunchingPage): Screen {
        return when(launchingPage) {
            LaunchingPage.Market -> Screen.CoinsList
            LaunchingPage.Favorites -> Screen.FavouriteCoinsList
        }
    }

}