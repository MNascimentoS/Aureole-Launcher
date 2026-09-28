package dev.mnascimentos.aureole.feature.home.folder.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.model.MainUiState

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

data class FolderAppPickerBodyParams(
    val folderName: String,
    val searchQuery: String,
    val filteredSelected: List<AppInfo>,
    val filteredRemaining: List<AppInfo>,
    val selectedPackages: MutableList<String>
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
