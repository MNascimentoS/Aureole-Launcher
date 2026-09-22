package dev.mnascimentos.aureole.feature.home.components.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder

data class SidePanelConfig(
    val panelId: String = "side_panel_item",
    val title: String = "Painel Lateral",
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
