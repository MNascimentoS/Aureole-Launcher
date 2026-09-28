package dev.mnascimentos.aureole.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_info")
data class AppInfoEntity(
    @PrimaryKey
    val packageName: String,
    val label: String,
    val activityName: String,
    val lastUpdated: Long = System.currentTimeMillis(),
)
