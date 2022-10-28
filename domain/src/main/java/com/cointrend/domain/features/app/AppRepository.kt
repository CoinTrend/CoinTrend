package com.cointrend.domain.features.app

interface AppRepository {

    suspend fun isPlayStoreReviewAlertAlreadyShown(): Boolean
    suspend fun setPlayStoreReviewAlertShown()

}
