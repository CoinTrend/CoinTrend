package com.cointrend.data.features.settings.local

import androidx.room.*
import com.cointrend.data.features.settings.local.models.SETTINGS_ROW_ID
import com.cointrend.data.features.settings.local.models.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SettingsDao {

    @Transaction
    @Query("SELECT * FROM settings where id = $SETTINGS_ROW_ID")
    abstract fun getSettingsConfiguration(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSettingsConfiguration(settings: SettingsEntity)

    @Query("DELETE FROM settings")
    abstract suspend fun deleteSettingsConfiguration()

}