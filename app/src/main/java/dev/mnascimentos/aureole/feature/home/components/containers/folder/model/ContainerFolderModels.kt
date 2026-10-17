@file:Suppress("Filename", "MatchingDeclarationName")

package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderConfig

data class ContainerFolderButtonParams(
    val folder: AppFolder,
    val isGridFolderEnabled: Boolean,
    val containerColor: Color,
    val textColor: Color,
    val isBackgroundEnabled: Boolean = true,
    val onPositionedY: (Float) -> Unit = {},
    val onClick: () -> Unit = {},
    val onLongClick: (() -> Unit)? = null,
)

data class ContainerFolderItemParams(
    val folder: AppFolder,
    val isOpened: Boolean,
    val config: ContainerFolderConfig,
    val onFolderClick: (AppFolder, Float) -> Unit,
    val onLongClick: (() -> Unit)? = null,
)

data class ContainerAppItemParams(
    val app: AppInfo,
    val showLabels: Boolean,
    val onAppClick: (AppInfo) -> Unit,
    val isBackgroundEnabled: Boolean = true,
    val onLongClick: () -> Unit = {}
)

data class ContainerFolderBottomSheetParams(
    val onEditShortcuts: () -> Unit,
    val onRename: () -> Unit,
    val onIcon: () -> Unit,
    val onRemoveFolder: () -> Unit,
    val onOpenContainerSettings: () -> Unit,
)
