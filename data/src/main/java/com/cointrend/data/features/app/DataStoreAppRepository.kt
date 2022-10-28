package com.cointrend.data.features.app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.cointrend.domain.features.app.AppRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class DataStoreAppRepository @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
) : AppRepository {

    private val isPlayStoreReviewAlertAlreadyShown = booleanPreferencesKey("IS_PLAY_STORE_REVIEW_ALREADY_SHOWN_KEY")


    override suspend fun isPlayStoreReviewAlertAlreadyShown(): Boolean {
        return try {
            preferencesDataStore.data.first()[isPlayStoreReviewAlertAlreadyShown] ?: false
        } catch (e: Exception) {
            Timber.e("isAppFirstLaunch ERROR: $e")
            true
        }
    }

    override suspend fun setPlayStoreReviewAlertShown() {
        preferencesDataStore.edit {
            it[isPlayStoreReviewAlertAlreadyShown] = true
        }
    }


}