package dev.mnascimentos.aureole.feature.home

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.ext.loadWidgetSettings
import dev.mnascimentos.aureole.feature.home.ext.updateAppsState
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
