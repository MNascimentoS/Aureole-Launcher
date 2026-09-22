package dev.mnascimentos.aureole.feature.home.components.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder

data class SidePanelConfig(
    val folders: List<AppFolder>,
    val openedFolderId: String?,
    val position: String = "Space Between",
    val showFolderLabels: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val hazeState: HazeState? = null,
)
