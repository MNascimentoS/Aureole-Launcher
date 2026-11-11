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

data class FolderSectionParams(
    val filteredSelected: List<AppInfo>,
    val searchQuery: String,
    val totalSelectedCount: Int
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
    /** Every app, in grid order (alphabetical); the marked ones show a check. */
    val gridApps: List<AppInfo>,
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

data class GridFolderPopupParams(
    val folder: AppFolder,
    val appsInFolder: List<AppInfo>,
    val actions: FolderPopupActions,
    val hazeState: HazeState?,
    val isHazeEnabled: Boolean
)

data class FolderAppPickerFooterActions(
    val onQueryChange: (String) -> Unit,
    val onSearch: () -> Unit,
    val onRemove: (String) -> Unit,
    /** A tray swap: the app at `from` of the tray now sits at `to`. */
    val onMove: (from: Int, to: Int) -> Unit,
    val onDismiss: () -> Unit,
    val onSave: () -> Unit
)
