package dev.mnascimentos.aureole.core.data.model

enum class SidePanelItemType {
    APP,
    FOLDER
}

data class SidePanelItemEntity(
    val id: String,
    val panelId: String,
    val itemType: SidePanelItemType,
    val packageName: String? = null,
    val folderId: String? = null,
    val orderIndex: Int = 0
)
