package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.model.MainUiState

object FolderPopupMetrics {
    val HazeBlurRadius = 20.dp
    val WidthMin = 180.dp
    val WidthMax = 210.dp
    val HeightMax = 400.dp
}

data class FolderAppRowItemParams(
    val app: AppInfo,
    val index: Int,
    val totalCount: Int,
    val canReorder: Boolean
)

data class FolderSectionParams(
    val filteredSelected: List<AppInfo>,
    val searchQuery: String,
    val totalSelectedCount: Int
)

data class SelectedFolderAppSectionParams(
    val filteredSelected: List<AppInfo>,
    val searchQuery: String,
    val totalSelectedCount: Int,
    val onMoveUp: (Int) -> Unit,
    val onMoveDown: (Int) -> Unit,
    val onToggleSelect: (String) -> Unit,
    /** Off while a selection drag runs: the rows are frozen and their indexes no longer match. */
    val isReorderEnabled: Boolean = true,
    /** Live selection; a row unmarked by a drag stays visible, faded, until the drag ends. */
    val isMarked: (String) -> Boolean = { true }
)

/** Apps of the picker grid that is not searching: selected rows on top, the other apps in the grid. */
data class PickerBrowseApps(
    val selected: List<AppInfo>,
    val remaining: List<AppInfo>
)

data class RemainingFolderAppGridParams(
    val apps: List<AppInfo>,
    val searchQuery: String,
    val isSelecting: Boolean,
    val isMarked: (String) -> Boolean,
    val onToggleSelect: (String) -> Unit
)

data class FolderAppPickerBodyParams(
    val folderName: String,
    val searchQuery: String,
    val filteredSelected: List<AppInfo>,
    val filteredRemaining: List<AppInfo>,
    val selectedPackages: MutableList<String>,
    val isSearching: Boolean = false,
    val searchResults: List<AppInfo> = emptyList(),
    val trayApps: List<AppInfo> = emptyList(),
    val isLeftHandedMode: Boolean = false
)

data class FolderAppPickerFooterParams(
    val trayApps: List<AppInfo>,
    val searchQuery: String,
    val saveCount: Int,
    val isLeftHandedMode: Boolean = false
)

data class OpenedFolderOverlayParams(
    val uiState: MainUiState,
    val folderToDisplay: AppFolder,
    val screenHeightPx: Float,
    val screenDensity: Float,
    val hazeState: HazeState
)

data class OpenedFolderPopupConfig(
    val folder: AppFolder,
    val allApps: List<AppInfo>,
    val actions: FolderPopupActions,
    val isGridFolderEnabled: Boolean = false,
    val hazeState: HazeState? = null
)

data class OpenedFolderPopupContentParams(
    val folderName: String,
    val appsInFolder: List<AppInfo>,
    val isActionsVisible: Boolean,
    val isGridMode: Boolean
)

data class GridFolderPopupParams(
    val folder: AppFolder,
    val appsInFolder: List<AppInfo>,
    val actions: FolderPopupActions,
    val isActionsVisible: Boolean,
    val onToggleActions: () -> Unit,
    val hazeState: HazeState?,
    val isHazeEnabled: Boolean
)

data class FolderAppPickerFooterActions(
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onRemove: (String) -> Unit,
    val onDismiss: () -> Unit,
    val onSave: () -> Unit
)
