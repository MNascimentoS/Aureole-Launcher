package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ContainerItemType {
    APP,
    FOLDER
}

@Entity(
    tableName = "container_items",
    foreignKeys = [
        ForeignKey(
            entity = ContainerEntity::class,
            parentColumns = ["id"],
            childColumns = ["panel_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["panel_id"])]
)
data class ContainerItemEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "panel_id")
    val panelId: String,
    @ColumnInfo(name = "item_type")
    val itemType: ContainerItemType,
    @ColumnInfo(name = "package_name")
    val packageName: String? = null,
    @ColumnInfo(name = "folder_id")
    val folderId: String? = null,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0
)
