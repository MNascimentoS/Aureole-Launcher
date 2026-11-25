package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.CONTAINER_ITEM_THEME_REF
import dev.mnascimentos.aureole.core.data.model.ContainerItemSpacing
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

data class ContainerFolderConfig(
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean,
    val isBackgroundEnabled: Boolean = false,
    /** Background of the folder button when it has no color of its own; null: none. */
    val itemColorRef: ThemeColorRef? = CONTAINER_ITEM_THEME_REF,
    val itemSpacing: ContainerItemSpacing = ContainerItemSpacing.NORMAL,
)

data class ContainerFolderButtonParams(
    val folder: AppFolder,
    val isGridFolderEnabled: Boolean,
    val containerColor: Color,
    val textColor: Color,
    val isBackgroundEnabled: Boolean = false,
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
    val isBackgroundEnabled: Boolean = false,
    val onLongClick: () -> Unit = {},
    /** Background of the app tile; null: none. */
    val itemColorRef: ThemeColorRef? = CONTAINER_ITEM_THEME_REF,
    val itemSpacing: ContainerItemSpacing = ContainerItemSpacing.NORMAL,
)

data class ContainerFolderBottomSheetParams(
    val onEditShortcuts: () -> Unit,
    val onRename: () -> Unit,
    val onIcon: () -> Unit,
    val onRemoveFolder: () -> Unit,
    val onOpenContainerSettings: () -> Unit,
    val onOpenSettings: (() -> Unit)? = null,
    /** The folder the sheet is about: its name is the title and its icon sits before it (D-01). */
    val folder: AppFolder? = null,
)
