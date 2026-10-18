package dev.mnascimentos.aureole.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "favorite_containers")
data class FavoriteContainerEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
