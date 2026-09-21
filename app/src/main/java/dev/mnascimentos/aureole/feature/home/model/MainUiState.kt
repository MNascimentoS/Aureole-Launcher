package dev.mnascimentos.aureole.feature.home.model

import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.home.grid.GridLimits

data class MainUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val alphabet: List<Char> = emptyList(),
    val letterIndexMap: Map<Char, Int> = emptyMap(),
    val isLoading: Boolean = true,

    // Widgets
    val topWidgetIds: List<Int> = emptyList(),
    val widgetRowHeight: Dp = 160.dp,
    val isWidgetRowEnabled: Boolean = true,
    val showWidgetDots: Boolean = true,
    val showWidgetPicker: Boolean = false,
    val pendingWidgetId: Int = -1,

    // Settings
    val isLeftHandedMode: Boolean = false,
    val isSidePanelEnabled: Boolean = true,
    val sidePanelPosition: String = "Center",
    val showFolderLabels: Boolean = false,
    val homeButtonOpensAllApps: Boolean = true,
    val showAllAppsOnHome: Boolean = true,
    val isCustomWallpaperSet: Boolean = false,
    val customWallpaperPath: String? = null,
    val isDynamicWallpaperEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val manualSeedColor: Int = SettingsRepository.DEFAULT_SEED_COLOR,
    val isHazeEnabled: Boolean = true,
    val isHazeSupported: Boolean = true,
    val hazeOpacity: Float = 0.5f,
    val isInAppUpdateEnabled: Boolean = true,
    val isThemedAppIconsEnabled: Boolean = false,
    val isAlphabetScrubberDisabled: Boolean = false,
    val showUpdateAvailableDialog: Boolean = false,
    val showUpdateDownloadedDialog: Boolean = false,

    // Favorites & Folders
    val favoriteAppPackages: List<String> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val favoriteApps: List<AppInfo> = emptyList(),

    // UI State for Dialogs/Drawer & Folders
    val openedFolderId: String? = null,
    val activeFolder: AppFolder? = null,
    val activeFolderTopYPx: Float = 0f,
    val isCreateFolderDialogVisible: Boolean = false,
    val isAddAppToFolderDialogVisible: Boolean = false,
    val isRenameFolderDialogVisible: Boolean = false,
    val searchQuery: String = "",
    val showSettingsDialog: Boolean = false,
    val showFavoritePickerDialog: Boolean = false,
    val isAllAppsDrawerOpen: Boolean = false,
    val activeWidgetId: Int? = null,
    val activeWidgetTopYPx: Float = 0f,
    val showWidgetPopup: Boolean = false,
    val showWidgetResizeDialog: Boolean = false,

    // Dynamic Grid Engine
    val gridItems: List<LauncherItemState> = emptyList(),
    val cachedGridItems: List<LauncherItemState>? = null,
    val gridLimits: GridLimits = GridLimits(),
    val isGridEditMode: Boolean = false,
    val editingGridItem: LauncherItemState? = null,
    val showAddContainerDialog: Boolean = false,
    val gridErrorMessage: String? = null,
    val isAddingSingleWidget: Boolean = false,
)
