package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel

data class ContainerConfig(
    val panelId: String = "container_item",
    val title: String = "Painel Lateral",
    val items: List<ContainerItemEntity> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val appPackageNames: List<String> = emptyList(),
    val quickButtons: List<QuickButtonModel> = emptyList(),
    val openedFolderId: String? = null,
    val position: String = "Space Between",
    val orientation: String = "Vertical",
    val showFolderLabels: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val isGridFolderEnabled: Boolean = false,
    val hideText: Boolean = false,
    val textColor: Long? = null,
    val backgroundColor: Long? = null,
    val strokeEnabled: Boolean = false,
    val strokeColor: Long? = null,
    val folderColor: Long? = null,
    val folderStrokeEnabled: Boolean = false,
    val scrollUpShortcut: String? = null,
    val scrollDownShortcut: String? = null,
    val resetOnWakeUp: Boolean = false,
    val hazeState: HazeState? = null,
)
