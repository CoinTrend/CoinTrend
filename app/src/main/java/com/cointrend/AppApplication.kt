package com.cointrend

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Timber.treeCount == 0 && BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}