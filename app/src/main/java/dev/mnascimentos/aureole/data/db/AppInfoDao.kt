package dev.mnascimentos.aureole.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {

    @Query("SELECT * FROM app_info ORDER BY LOWER(label) ASC")
    fun getAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM app_info ORDER BY LOWER(label) ASC")
    suspend fun getAllApps(): List<AppInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppInfoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppInfoEntity)

    @Query("DELETE FROM app_info WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String): Int

    @Query("DELETE FROM app_info WHERE packageName NOT IN (:packageNames)")
    suspend fun deleteAppsNotIn(packageNames: List<String>): Int

    @Query("DELETE FROM app_info")
    suspend fun clearAll(): Int
}
