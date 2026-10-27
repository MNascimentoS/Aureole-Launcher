package dev.mnascimentos.aureole.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "container_favorite_items",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteContainerEntity::class,
            parentColumns = ["id"],
            childColumns = ["container_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["container_id"])]
)
data class ContainerFavoriteItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "container_id")
    val containerId: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "item_order")
    val itemOrder: Int = 0
)
