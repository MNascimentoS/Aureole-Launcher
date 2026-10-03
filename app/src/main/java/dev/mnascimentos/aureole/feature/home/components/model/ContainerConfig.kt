package dev.mnascimentos.aureole.feature.home.components.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity

data class ContainerConfig(
    val panelId: String = "container_item",
    val title: String = "Painel Lateral",
    val items: List<ContainerItemEntity> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val appPackageNames: List<String> = emptyList(),
    val openedFolderId: String? = null,
    val position: String = "Space Between",
    val showFolderLabels: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val isGridFolderEnabled: Boolean = false,
    val hazeState: HazeState? = null,
)
