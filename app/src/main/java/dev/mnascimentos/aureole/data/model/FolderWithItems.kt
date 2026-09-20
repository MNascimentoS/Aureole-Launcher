package dev.mnascimentos.aureole.data.model

data class FolderWithItems(
    val folder: FolderEntity,
    val items: List<FolderItemEntity> = emptyList()
) {
    fun toAppFolder(): AppFolder {
        return AppFolder(
            id = folder.id,
            name = folder.name,
            iconPackage = folder.iconFallback,
            appPackageNames = items.sortedBy { it.itemOrder }.map { it.packageName },
            displayAsGrid = folder.displayAsGrid
        )
    }
}
