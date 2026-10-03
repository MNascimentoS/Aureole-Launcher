package dev.mnascimentos.aureole.core.data.model

import java.util.UUID

data class ContainerModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Container",
    val position: String = "Space Between",
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val showFolderLabels: Boolean = false,
    val isGridFolderEnabled: Boolean = true,
    val items: List<ContainerItemEntity> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val appPackageNames: List<String> = emptyList()
)
