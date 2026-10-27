package dev.mnascimentos.aureole.core.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class FolderWithItems(
    @Embedded
    val folder: FolderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "folder_id"
    )
    val items: List<FolderItemEntity> = emptyList()
) {
    fun toAppFolder(): AppFolder {
        return AppFolder(
            id = folder.id,
            panelId = folder.panelId,
            name = folder.name,
            iconPackage = folder.iconFallback,
            icon = folder.icon,
            appPackageNames = items.sortedBy { it.itemOrder }.map { it.packageName },
            displayAsGrid = folder.displayAsGrid
        )
    }
}
