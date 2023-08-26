package com.cointrend.data.features.settings.local

import com.cointrend.data.features.settings.SettingsLocalDataSource
import com.cointrend.data.mappers.RoomDataMapper
import com.cointrend.domain.features.settings.models.GlobalSettingsConfiguration
import com.cointrend.domain.features.settings.models.SettingsConfiguration
import com.cointrend.domain.features.settings.models.toSettingsConfiguration
import com.cointrend.domain.models.TimeRange
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomSettingsLocalDataSource @Inject constructor(
    private val dao: SettingsDao,
    private val mapper: RoomDataMapper,
    private val dispatchers: DispatcherProvider,
    private val globalSettingsConfiguration: GlobalSettingsConfiguration
) : SettingsLocalDataSource {

    override fun getSettingsFlow(): Flow<SettingsConfiguration?> {
        return dao.getSettingsConfiguration().map {
            it?.let {
                mapper.mapSettingsConfiguration(settingsEntity = it)
            }
        }
    }

    override suspend fun setDefaultTimeRange(timeRange: TimeRange) {
        withContext(dispatchers.io) {
            dao.insertSettingsConfiguration(
                settings = mapper.mapSettingsEntity(
                    settingsConfiguration = globalSettingsConfiguration.toSettingsConfiguration().copy(
                        defaultTimeRange = timeRange
                    )
                )
            )
        }
    }

}