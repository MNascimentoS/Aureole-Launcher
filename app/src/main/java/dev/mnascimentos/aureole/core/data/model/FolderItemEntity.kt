package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "folder_items",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["folder_id"])]
)
data class FolderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "folder_id")
    val folderId: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "item_order")
    val itemOrder: Int = 0
)
