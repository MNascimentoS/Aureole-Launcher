package dev.mnascimentos.aureole.feature.home.model

import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
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
    val isContainerEnabled: Boolean = true,
    val isContainerBackgroundEnabled: Boolean = true,
    val isContainerExpandCell: Boolean = false,
    val isClockBackgroundEnabled: Boolean = true,
    val showContainerAddFolderButton: Boolean = true,
    val containerPosition: String = "Center",
    val showFolderLabels: Boolean = false,
    val homeButtonOpensAllApps: Boolean = true,
    val showAllAppsOnHome: Boolean = true,
    val isCustomWallpaperSet: Boolean = false,
    val customWallpaperPath: String? = null,
    val wallpaperScaleType: String = "Crop",
    val isDynamicWallpaperEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val manualSeedColor: Int = SettingsRepository.DEFAULT_SEED_COLOR,
    val selectedThemeName: String = "Frostbite",
    val selectedFontName: String = "Istok Web",
    val isHazeEnabled: Boolean = true,
    val isHazeSupported: Boolean = true,
    val hazeOpacity: Float = 0.5f,
    val isInAppUpdateEnabled: Boolean = true,
    val isThemedAppIconsEnabled: Boolean = false,
    val isAlphabetScrubberDisabled: Boolean = false,
    val showSettingsButtonInAllApps: Boolean = true,
    val settingsButtonPosition: String = "Right",
    val showSearchBarInAllApps: Boolean = true,
    val searchIconPosition: String = "Left",
    val headerOffsetPercent: Int = 10,
    val cornerRadiusDp: Int = SettingsRepository.DEFAULT_CORNER_RADIUS_DP,
    val showUpdateAvailableDialog: Boolean = false,
    val showUpdateDownloadedDialog: Boolean = false,

    // Favorites & Folders
    val favoriteAppPackages: List<String> = emptyList(),
    val containerFavorites: Map<String, List<String>> = emptyMap(),
    val activeFavoriteContainerId: String? = null,
    val folders: List<AppFolder> = emptyList(),
    val favoriteApps: List<AppInfo> = emptyList(),

    // UI State for Dialogs/Drawer & Folders
    val openedFolderId: String? = null,
    val activeFolder: AppFolder? = null,
    val activeFolderTopYPx: Float = 0f,
    val isCreateFolderDialogVisible: Boolean = false,
    val targetPanelIdForFolder: String? = null,
    val isAddAppToFolderDialogVisible: Boolean = false,
    val isRenameFolderDialogVisible: Boolean = false,
    val searchQuery: String = "",
    val showSettingsDialog: Boolean = false,
    val showFavoritePickerDialog: Boolean = false,
    val isAllAppsDrawerOpen: Boolean = false,
    val isAllAppsOpenedFromBottom: Boolean = false,
    val activeWidgetId: Int? = null,
    val activeWidgetTopYPx: Float = 0f,
    val showWidgetPopup: Boolean = false,
    val showWidgetResizeDialog: Boolean = false,

    // Dynamic Grid Engine
    val gridItems: List<LauncherItemState> = emptyList(),
    val portraitGridItems: List<LauncherItemState> = emptyList(),
    val landscapeGridItems: List<LauncherItemState> = emptyList(),
    val cachedPortraitGridItems: List<LauncherItemState>? = null,
    val cachedLandscapeGridItems: List<LauncherItemState>? = null,
    val cachedGridItems: List<LauncherItemState>? = null,
    val gridLimits: GridLimits = GridLimits(),
    val isLandscape: Boolean = false,
    val isGridEditMode: Boolean = false,
    val editingGridItem: LauncherItemState? = null,
    val showAddContainerDialog: Boolean = false,
    val showCustomizeBottomSheet: Boolean = false,
    val targetParentContainerId: String? = null,
    val gridErrorMessage: String? = null,
    val isAddingSingleWidget: Boolean = false,

    // Side Panels
    val containers: Map<String, ContainerModel> = emptyMap(),
    val editingContainerId: String? = null,
    val isEditContainerDialogVisible: Boolean = false,

    // Contextual Bottom Sheets
    val activeContainerAppBottomSheet: ContainerAppBottomSheetState? = null,
    val activeContainerFolderBottomSheet: ContainerFolderBottomSheetState? = null,
    val activeFolderAppBottomSheet: FolderAppBottomSheetState? = null,
    val activeWidgetStackBottomSheet: WidgetStackBottomSheetState? = null,
    val widgetStackDots: Map<String, Boolean> = emptyMap()
)

data class ContainerAppBottomSheetState(
    val app: AppInfo,
    val panelId: String
)

data class ContainerFolderBottomSheetState(
    val folder: AppFolder,
    val panelId: String
)

data class FolderAppBottomSheetState(
    val app: AppInfo,
    val folder: AppFolder
)

data class WidgetStackBottomSheetState(
    val widgetId: Int?,
    val stackId: String
)
