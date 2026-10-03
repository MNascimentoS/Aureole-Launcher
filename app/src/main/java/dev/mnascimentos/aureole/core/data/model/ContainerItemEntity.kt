package dev.mnascimentos.aureole.core.data.model

enum class ContainerItemType {
    APP,
    FOLDER
}

data class ContainerItemEntity(
    val id: String,
    val panelId: String,
    val itemType: ContainerItemType,
    val packageName: String? = null,
    val folderId: String? = null,
    val orderIndex: Int = 0
)
