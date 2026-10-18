package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "folders",
    indices = [Index(value = ["panel_id"])]
)
data class FolderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "panel_id")
    val panelId: String? = null,
    val name: String,
    val color: String? = null,
    @ColumnInfo(name = "icon_fallback")
    val iconFallback: String? = null,
    val icon: String? = null,
    @ColumnInfo(name = "display_as_grid")
    val displayAsGrid: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
