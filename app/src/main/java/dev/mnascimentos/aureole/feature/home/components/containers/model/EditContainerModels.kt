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

/** Where the back key goes from this screen: the main one from a sub screen, or null (close) from the main one. */
fun EditContainerScreenMode.backTarget(): EditContainerScreenMode? =
    if (this == EditContainerScreenMode.MAIN) null else EditContainerScreenMode.MAIN

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
)

data class ManageItemRowParams(
    val item: ContainerItemEntity,
    val folders: List<AppFolder>,
    val allApps: List<AppInfo>
)
