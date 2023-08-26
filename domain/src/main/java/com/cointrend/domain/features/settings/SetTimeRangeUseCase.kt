package com.cointrend.domain.features.settings

import com.cointrend.domain.models.TimeRange
import javax.inject.Inject

class SetDefaultTimeRangeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(timeRange: TimeRange): Result<Unit> {
        return settingsRepository.setDefaultTimeRange(timeRange = timeRange)
    }

}