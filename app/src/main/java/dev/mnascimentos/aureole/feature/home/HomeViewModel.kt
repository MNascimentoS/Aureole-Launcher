package dev.mnascimentos.aureole.feature.home

import android.app.Application
import android.content.ComponentName
import android.os.Build
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface FolderViewIntent {
    object OpenCreateFolderDialog : FolderViewIntent
    data class SubmitFolderName(val name: String) : FolderViewIntent
    data class OpenFolder(val folderId: String, val topYPx: Float = 0f) : FolderViewIntent
    object CloseFolder : FolderViewIntent
    data class LaunchApp(val packageName: String) : FolderViewIntent
    data class AddAppToFolder(val folderId: String) : FolderViewIntent
    data class SaveFolderApps(val folderId: String, val selectedPackageNames: List<String>) : FolderViewIntent
    data class RenameFolder(val folderId: String, val newName: String, val icon: String? = null) : FolderViewIntent
    data class DeleteFolder(val folderId: String) : FolderViewIntent
}

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
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    internal val appRepository = AppRepository(application)
    internal val widgetRepository = WidgetRepository(application)
    internal val settingsRepository = SettingsRepository(application)
    internal val folderRepository = FolderRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    internal fun updateUiState(transform: (MainUiState) -> MainUiState) {
        _uiState.update(transform)
    }

    val pendingWidgetId: Int
        get() = _uiState.value.pendingWidgetId

    init {
        loadSettings()
        observeAppsFlow()
        loadWidgetSettings()
        syncApps()
    }

    private fun observeAppsFlow() {
        viewModelScope.launch {
            appRepository.appsFlow.collect { appsList ->
                updateAppsState(appsList)
            }
        }
    }

    fun loadApps() {
        syncApps()
    }

    fun syncApps() {
        viewModelScope.launch {
            appRepository.syncApps()
        }
    }

    fun launchApp(componentName: ComponentName) {
        appRepository.launchApp(componentName)
    }

    fun loadSettings() {
        viewModelScope.launch {
            val isLeftHanded = settingsRepository.isLeftHandedMode
            val isSidePanelEnabled = settingsRepository.isSidePanelEnabled
            val sidePanelPosition = settingsRepository.sidePanelPosition
            val showFolderLabels = settingsRepository.showFolderLabels
            val homeOpensAllApps = settingsRepository.homeButtonOpensAllApps
            val showAllAppsHome = settingsRepository.showAllAppsOnHome
            val isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled
            val showWidgetDots = settingsRepository.showWidgetDots
            val favoritePackages = settingsRepository.favoriteAppPackages
            val savedFolders = folderRepository.getFolders()
            val isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet
            val customWallpaperPath = settingsRepository.customWallpaperPath
            val isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled
            val manualSeedColor = settingsRepository.manualSeedColor
            val isHazeEnabled = settingsRepository.isHazeEnabled
            val isHazeSupported = settingsRepository.isHazeSupported
            val hazeOpacity = settingsRepository.hazeOpacity

            _uiState.update {
                it.copy(
                    isLeftHandedMode = isLeftHanded,
                    isSidePanelEnabled = isSidePanelEnabled,
                    sidePanelPosition = sidePanelPosition,
                    showFolderLabels = showFolderLabels,
                    homeButtonOpensAllApps = homeOpensAllApps,
                    showAllAppsOnHome = showAllAppsHome,
                    isWidgetRowEnabled = isWidgetRowEnabled,
                    showWidgetDots = showWidgetDots,
                    favoriteAppPackages = favoritePackages,
                    folders = savedFolders,
                    isCustomWallpaperSet = isCustomWallpaperSet,
                    customWallpaperPath = customWallpaperPath,
                    isDynamicWallpaperEnabled = isDynamicWallpaperEnabled,
                    manualSeedColor = manualSeedColor,
                    isHazeEnabled = isHazeEnabled,
                    isHazeSupported = isHazeSupported,
                    hazeOpacity = hazeOpacity
                )
            }
        }
    }
}
