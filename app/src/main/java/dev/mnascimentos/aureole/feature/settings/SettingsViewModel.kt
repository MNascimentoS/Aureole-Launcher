package dev.mnascimentos.aureole.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsToggleManager
import dev.mnascimentos.aureole.feature.settings.extensions.applySettingValue
import dev.mnascimentos.aureole.feature.settings.extensions.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SettingToggle {
    SHOW_WIDGET_DOTS,
    HAZE,
    LEFT_HANDED_MODE,
    CONTAINER,
    CONTAINER_BACKGROUND,
    CONTAINER_EXPAND_CELL,
    CLOCK_BACKGROUND,
    SHOW_CONTAINER_ADD_FOLDER_BUTTON,
    SHOW_FOLDER_LABELS,
    HOME_OPENS_ALL_APPS,
    SHOW_ALL_APPS_ON_HOME,
    WIDGET_ROW,
    DYNAMIC_WALLPAPER,
    IN_APP_UPDATE,
    THEMED_APP_ICONS,
    DISABLE_ALPHABET_SCRUBBER,
    SHOW_SETTINGS_BUTTON_IN_ALL_APPS,
    SHOW_SEARCH_BAR_IN_ALL_APPS
}

enum class SettingsDialog {
    FAVORITE_PICKER,
    CONTAINER_POSITION,
    HAZE_OPACITY,
    COLOR_PICKER,
    RESTORE_WALLPAPER,
    CREATE_FOLDER,
    RESET_GRID,
    FACTORY_RESET,
    SETTINGS_BUTTON_POSITION,
    SEARCH_ICON_POSITION
}

// SettingsViewModel holds many properties
@Suppress("TooManyFunctions")
class SettingsViewModel(
    internal val settingsRepository: SettingsRepository,
    private val appRepository: AppRepository,
    private val folderRepository: FolderRepository,
    internal val gridRepository: GridRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    internal fun updateUiState(transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update(transform)
    }

    init {
        loadSettings()
        viewModelScope.launch {
            appRepository.appsFlow.collect { apps ->
                _uiState.update { it.copy(allApps = apps) }
            }
        }
        checkDefaultLauncher()
    }

    fun loadSettings() {
        _uiState.update {
            it.copy(
                isLeftHandedMode = settingsRepository.isLeftHandedMode,
                isContainerEnabled = settingsRepository.isContainerEnabled,
                isContainerBackgroundEnabled = settingsRepository.isContainerBackgroundEnabled,
                isContainerExpandCell = settingsRepository.isContainerExpandCell,
                isClockBackgroundEnabled = settingsRepository.isClockBackgroundEnabled,
                showContainerAddFolderButton = settingsRepository.showContainerAddFolderButton,
                containerPosition = settingsRepository.containerPosition,
                showFolderLabels = settingsRepository.showFolderLabels,
                homeButtonOpensAllApps = settingsRepository.homeButtonOpensAllApps,
                showAllAppsOnHome = settingsRepository.showAllAppsOnHome,
                isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled,
                showWidgetDots = settingsRepository.showWidgetDots,
                favoriteAppPackages = settingsRepository.favoriteAppPackages,
                isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet,
                customWallpaperPath = settingsRepository.customWallpaperPath,
                wallpaperScaleType = settingsRepository.wallpaperScaleType,
                isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled,
                manualSeedColor = settingsRepository.manualSeedColor,
                selectedThemeName = settingsRepository.selectedThemeName,
                isHazeEnabled = settingsRepository.isHazeEnabled,
                isHazeSupported = settingsRepository.isHazeSupported,
                hazeOpacity = settingsRepository.hazeOpacity,
                isInAppUpdateEnabled = settingsRepository.isInAppUpdateEnabled,
                isThemedAppIconsEnabled = settingsRepository.isThemedAppIconsEnabled,
                isAlphabetScrubberDisabled = settingsRepository.isAlphabetScrubberDisabled,
                showSettingsButtonInAllApps = settingsRepository.showSettingsButtonInAllApps,
                settingsButtonPosition = settingsRepository.settingsButtonPosition,
                showSearchBarInAllApps = settingsRepository.showSearchBarInAllApps,
                searchIconPosition = settingsRepository.searchIconPosition,
                headerOffsetPercent = settingsRepository.headerOffsetPercent,
                cornerRadiusDp = settingsRepository.cornerRadiusDp,
            )
        }
    }

    fun toggleSetting(toggle: SettingToggle) {
        SettingsToggleManager.toggleSetting(this, toggle)
    }

    fun setDialogVisible(dialog: SettingsDialog, visible: Boolean) {
        _uiState.update {
            when (dialog) {
                SettingsDialog.FAVORITE_PICKER -> it.copy(showFavoritePickerDialog = visible)
                SettingsDialog.CONTAINER_POSITION -> it.copy(showContainerPositionDialog = visible)
                SettingsDialog.HAZE_OPACITY -> it.copy(showHazeOpacityDialog = visible)
                SettingsDialog.COLOR_PICKER -> it.copy(showColorPickerDialog = visible)
                SettingsDialog.RESTORE_WALLPAPER -> it.copy(showRestoreWallpaperDialog = visible)
                SettingsDialog.CREATE_FOLDER -> it.copy(showCreateFolderDialog = visible)
                SettingsDialog.RESET_GRID -> it.copy(showResetGridDialog = visible)
                SettingsDialog.FACTORY_RESET -> it.copy(showFactoryResetDialog = visible)
                SettingsDialog.SETTINGS_BUTTON_POSITION -> it.copy(showSettingsButtonPositionDialog = visible)
                SettingsDialog.SEARCH_ICON_POSITION -> it.copy(showSearchIconPositionDialog = visible)
            }
        }
    }

    fun setSettingValue(value: SettingValue) {
        applySettingValue(value)
    }

    @Suppress("MagicNumber") // Header offset clamping limit of 60 percent
    fun setHeaderOffsetPercent(percent: Int) {
        val clamped = percent.coerceIn(0, 60)
        settingsRepository.headerOffsetPercent = clamped
        _uiState.update { it.copy(headerOffsetPercent = clamped) }
    }

    fun toggleFavorite(packageName: String) {
        val current = _uiState.value.favoriteAppPackages.toMutableList()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        settingsRepository.favoriteAppPackages = current
        _uiState.update { it.copy(favoriteAppPackages = current) }
    }

    fun setCustomWallpaper(uriString: String) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                settingsRepository.saveCustomWallpaper(uriString)
            }

            if (success) {
                _uiState.update {
                    it.copy(
                        isCustomWallpaperSet = true,
                        customWallpaperPath = settingsRepository.customWallpaperPath,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = "Não foi possível carregar a imagem selecionada")
                }
            }
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val newFolder = AppFolder(name = name)
            folderRepository.addFolder(newFolder)
            _uiState.update {
                it.copy(
                    showCreateFolderDialog = false,
                    shouldFinishActivity = true
                )
            }
        }
    }

    fun onActivityFinishedHandled() {
        _uiState.update { it.copy(shouldFinishActivity = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        // ViewModel state helpers
    }
}
