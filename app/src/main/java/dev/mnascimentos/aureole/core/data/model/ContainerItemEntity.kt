package dev.mnascimentos.aureole.core.data.model

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
            childColumns = ["panelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["panelId"])]
)
data class ContainerItemEntity(
    @PrimaryKey
    val id: String,
    val panelId: String,
    val itemType: ContainerItemType,
    val packageName: String? = null,
    val folderId: String? = null,
    val orderIndex: Int = 0
)
