package dev.mnascimentos.aureole.core.data.model

data class FolderWithItems(
    val folder: FolderEntity,
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
