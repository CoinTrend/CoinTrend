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

    private var numRequests: Int = 0
    private var firstRequestDate: LocalDateTime? = null


    private const val MAX_REQUESTS_NUM = 40
    private const val MINUTES_MAX_REQUESTS = 1

    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @ApplicationContext context: Context
    ): CoinGeckoApiService {
        val cacheSize = 10 * 1024 * 1024L // 10MB
        val cache = Cache(context.cacheDir, cacheSize)
        //val maxAgeSeconds = 60 * 4 // Caches the responses for n seconds

        val okHttpClient = OkHttpClient.Builder()
                /*
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())

                Timber.d("${response.headers()}")

                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAgeSeconds")
                    .removeHeader("Pragma")
                    .build()


            }

                 */
            .addNetworkInterceptor { chain ->
                // This interceptor tracks the number of network requests performed in a given time range
                // to limit them in case they exceed the maximum number set.
                Timber.d("NetworkInterceptor: ${chain.request()}")

                firstRequestDate?.let {
                    numRequests++

                    if (numRequests >= MAX_REQUESTS_NUM) {
                        val minutesFromFirstRequest = it.minutesBetween(LocalDateTime.now())
                        Timber.d("$chain, $numRequests, $minutesFromFirstRequest")

                        if (minutesFromFirstRequest >= MINUTES_MAX_REQUESTS) {
                            initializeRequestsCounter()
                        } else {
                            // In case the maximum number of requests is reached, the request
                            // is blocked by launching the following exception
                            throw TemporarilyUnavailableNetworkServiceException(
                                serviceName = "CoinGecko"
                            )
                        }
                    }

                } ?: kotlin.run {
                    initializeRequestsCounter()
                }

                val response = chain.proceed(chain.request())

                Timber.d("${response.headers()}")

                when(response.code()) {
                    429, 503 -> {
                        // In case of a service unavailable response code from the server,
                        // the requests counter is set to the maximum value so that the next requests
                        // are temporarily blocked by this interceptor before being forwarded to the server
                        // to avoid overcharging it.
                        setRequestsCounterToExceedingValues()
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

    private fun initializeRequestsCounter() {
        numRequests = 1
        firstRequestDate = LocalDateTime.now()
    }

    private fun setRequestsCounterToExceedingValues() {
        numRequests = MAX_REQUESTS_NUM
        firstRequestDate = LocalDateTime.now()
    }

}