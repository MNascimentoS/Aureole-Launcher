package dev.mnascimentos.aureole.core.data.model

import java.util.UUID

data class SidePanelModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Painel Lateral",
    val position: String = "Space Between",
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val showFolderLabels: Boolean = false,
    val isGridFolderEnabled: Boolean = false,
    val items: List<SidePanelItemEntity> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val appPackageNames: List<String> = emptyList()
)
