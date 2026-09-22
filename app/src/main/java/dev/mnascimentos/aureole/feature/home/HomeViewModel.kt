package dev.mnascimentos.aureole.feature.home

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.SidePanelRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.extensions.loadGridItems
import dev.mnascimentos.aureole.feature.home.extensions.loadSidePanels
import dev.mnascimentos.aureole.feature.home.extensions.loadWidgetSettings
import dev.mnascimentos.aureole.feature.home.extensions.updateAppsState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    internal val appRepository = AppRepository(application)
    internal val widgetRepository = WidgetRepository(application)
    internal val settingsRepository = SettingsRepository(application)
    internal val folderRepository = FolderRepository(application)
    internal val favoriteContainerRepository = FavoriteContainerRepository(application)
    internal val sidePanelRepository = SidePanelRepository(application)

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
        loadGridItems()
        loadSidePanels()
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
            val containerFavsMap = favoriteContainerRepository.getAllContainerFavorites()
            val savedFolders = folderRepository.getFolders()
            _uiState.update {
                it.copy(
                    isLeftHandedMode = settingsRepository.isLeftHandedMode,
                    isSidePanelEnabled = settingsRepository.isSidePanelEnabled,
                    isSidePanelBackgroundEnabled = settingsRepository.isSidePanelBackgroundEnabled,
                    isSidePanelExpandCell = settingsRepository.isSidePanelExpandCell,
                    isClockBackgroundEnabled = settingsRepository.isClockBackgroundEnabled,
                    showSidePanelAddFolderButton = settingsRepository.showSidePanelAddFolderButton,
                    sidePanelPosition = settingsRepository.sidePanelPosition,
                    showFolderLabels = settingsRepository.showFolderLabels,
                    homeButtonOpensAllApps = settingsRepository.homeButtonOpensAllApps,
                    showAllAppsOnHome = settingsRepository.showAllAppsOnHome,
                    isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled,
                    showWidgetDots = settingsRepository.showWidgetDots,
                    favoriteAppPackages = settingsRepository.favoriteAppPackages,
                    containerFavorites = containerFavsMap,
                    folders = savedFolders,
                    isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet,
                    customWallpaperPath = settingsRepository.customWallpaperPath,
                    isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled,
                    manualSeedColor = settingsRepository.manualSeedColor,
                    isHazeEnabled = settingsRepository.isHazeEnabled,
                    isHazeSupported = settingsRepository.isHazeSupported,
                    hazeOpacity = settingsRepository.hazeOpacity,
                    isInAppUpdateEnabled = settingsRepository.isInAppUpdateEnabled,
                    isThemedAppIconsEnabled = settingsRepository.isThemedAppIconsEnabled,
                    isAlphabetScrubberDisabled = settingsRepository.isAlphabetScrubberDisabled,
                    showSettingsButtonInAllApps = settingsRepository.showSettingsButtonInAllApps,
                    settingsButtonPosition = settingsRepository.settingsButtonPosition,
                    showSearchBarInAllApps = settingsRepository.showSearchBarInAllApps,
                    searchIconPosition = settingsRepository.searchIconPosition
                )
            }
        }
    }

    fun setShowUpdateAvailableDialog(visible: Boolean) {
        _uiState.update { it.copy(showUpdateAvailableDialog = visible) }
    }

    fun setShowUpdateDownloadedDialog(visible: Boolean) {
        _uiState.update { it.copy(showUpdateDownloadedDialog = visible) }
    }
}
