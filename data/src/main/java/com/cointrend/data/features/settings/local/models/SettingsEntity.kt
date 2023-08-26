package com.cointrend.data.features.settings.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cointrend.domain.models.Currency
import com.cointrend.domain.models.Ordering
import com.cointrend.domain.models.TimeRange

@Entity(
    tableName = "settings"
)
data class SettingsEntity(
    @PrimaryKey val id: Int = SETTINGS_ROW_ID, // Always the same as there is only one row for the settings

    val defaultTimeRange: TimeRange,
    val currency: Currency,
    val defaultOrdering: Ordering
)
