package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerModel

/**
 * Parameters for [EditContainerScreen].
 */
data class EditContainerScreenParams(
    val panel: ContainerModel,
    val allApps: List<AppInfo>,
    val onDismiss: () -> Unit,
    val onSave: (ContainerModel) -> Unit,
    val hazeState: HazeState? = null
)

enum class EditContainerScreenMode {
    MAIN, APPEARANCE, BEHAVIOR, MANAGE_ITEMS
}

data class ContainerOptionsState(
    val position: String,
    val orientation: String,
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean,
    val hideText: Boolean,
    val textColor: Long?,
    val backgroundColor: Long?,
    val strokeEnabled: Boolean,
    val strokeColor: Long?,
    val folderColor: Long?,
    val folderStrokeEnabled: Boolean,
    val scrollUpShortcut: String?,
    val scrollDownShortcut: String?
)

data class EditContainerFormState(
    val position: String,
    val orientation: String = "Vertical",
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean
)

data class EditContainerFormCallbacks(
    val onPositionChange: (String) -> Unit,
    val onOrientationChange: (String) -> Unit = {},
    val onBgChange: (Boolean) -> Unit,
    val onExpandChange: (Boolean) -> Unit,
    val onAddFolderBtnChange: (Boolean) -> Unit,
    val onFolderLabelsChange: (Boolean) -> Unit,
    val onGridFolderChange: (Boolean) -> Unit
)

data class EditContainerItemRowParams(
    val title: String,
    val iconName: String?,
    val isFolder: Boolean,
    val canMoveUp: Boolean,
    val canMoveDown: Boolean,
)

data class EditContainerItemRowActions(
    val onMoveUp: () -> Unit,
    val onMoveDown: () -> Unit,
    val onRemove: () -> Unit,
)

data class ManageItemRowParams(
    val index: Int,
    val item: ContainerItemEntity,
    val totalItems: Int,
    val folders: List<AppFolder>,
    val allApps: List<AppInfo>
)

data class ManageItemRowActions(
    val onMoveUp: (Int) -> Unit,
    val onMoveDown: (Int) -> Unit,
    val onRemove: (Int) -> Unit
)
