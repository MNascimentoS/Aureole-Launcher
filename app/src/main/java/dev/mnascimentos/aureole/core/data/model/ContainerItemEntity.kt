package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ContainerItemType {
    APP,
    FOLDER,
    QUICK_BUTTON
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
    @ColumnInfo(name = "quick_button_id")
    val quickButtonId: String? = null,
    @ColumnInfo(name = "quick_button_json")
    val quickButtonJson: String? = null,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0
) {
    @Ignore
    var quickButtonModel: QuickButtonModel? = null

    @Ignore
    constructor(
        id: String,
        panelId: String,
        itemType: ContainerItemType,
        packageName: String?,
        folderId: String?,
        quickButtonId: String?,
        orderIndex: Int
    ) : this(id, panelId, itemType, packageName, folderId, quickButtonId, null, orderIndex)

    @Ignore
    constructor(
        id: String,
        panelId: String,
        itemType: ContainerItemType,
        packageName: String?,
        folderId: String?,
        quickButtonId: String?,
        orderIndex: Int,
        quickButtonModel: QuickButtonModel?
    ) : this(id, panelId, itemType, packageName, folderId, quickButtonId, null, orderIndex) {
        this.quickButtonModel = quickButtonModel
    }
}
