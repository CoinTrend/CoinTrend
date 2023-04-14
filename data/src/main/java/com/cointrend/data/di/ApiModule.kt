package com.cointrend.data.di

import android.content.Context
import com.cointrend.data.api.coingecko.CoinGeckoApiService
import com.cointrend.domain.exceptions.TemporarilyUnavailableNetworkServiceException
import com.github.davidepanidev.kotlinextensions.minutesBetween
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS = 1
    private var timeWhenMaxRequestsLimitIsReached: LocalDateTime? = null

    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @ApplicationContext context: Context
    ): CoinGeckoApiService {
        val cacheSize = 10 * 1024 * 1024L // 10MB
        val cache = Cache(context.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                // This interceptor tracks the number of network requests performed in a given time range
                // to limit them in case they exceed the maximum number set.
                Timber.d("NetworkInterceptor: ${chain.request()}")

                timeWhenMaxRequestsLimitIsReached?.let {
                    val minutesFromLimitReached = it.minutesBetween(LocalDateTime.now())

                    if (minutesFromLimitReached >= MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS) {
                        resetLimitReachedState()
                    } else {
                        // In case the maximum number of requests is reached, the request
                        // is blocked by launching the following exception
                        throw TemporarilyUnavailableNetworkServiceException(
                            serviceName = "CoinGecko"
                        )
                    }
                }

                val response = chain.proceed(chain.request())

                Timber.d("${response.headers()}")

                when(response.code()) {
                    429 -> {
                        // In case of a service unavailable response code from the server,
                        // the requests counter is set to the maximum value so that the next requests
                        // are temporarily blocked by this interceptor before being forwarded to the server
                        // to avoid overcharging it.
                        setMaxRequestsLimitReached()
                        response
                    }
                    else -> response
                }
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CoinGeckoApiService.BASE_URL)
            .client(okHttpClient)
            .build()
            .create(CoinGeckoApiService::class.java)
    }

    private fun resetLimitReachedState() {
        timeWhenMaxRequestsLimitIsReached = null
    }

    private fun setMaxRequestsLimitReached() {
        timeWhenMaxRequestsLimitIsReached = LocalDateTime.now()
    }

}