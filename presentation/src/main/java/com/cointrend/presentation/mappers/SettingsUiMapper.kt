package com.cointrend.presentation.mappers

import com.cointrend.domain.models.TimeRange
import com.cointrend.presentation.models.TimeRangeUi
import javax.inject.Inject

class SettingsUiMapper @Inject constructor() {

    fun mapTimeRangeUi(timeRange: TimeRange): TimeRangeUi {
        return when(timeRange) {
            TimeRange.Day -> TimeRangeUi.Day
            TimeRange.Week -> TimeRangeUi.Week
            TimeRange.Month -> TimeRangeUi.Month
            TimeRange.ThreeMonths -> TimeRangeUi.ThreeMonths
            TimeRange.Year -> TimeRangeUi.Year
            TimeRange.Max -> TimeRangeUi.Max
        }
    }

}