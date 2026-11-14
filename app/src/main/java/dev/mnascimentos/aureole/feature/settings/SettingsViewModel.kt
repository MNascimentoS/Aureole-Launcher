package dev.mnascimentos.aureole.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.model.glassLevelOf
import dev.mnascimentos.aureole.core.undo.UndoController
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsToggleManager
import dev.mnascimentos.aureole.feature.settings.extensions.applySettingValue
import dev.mnascimentos.aureole.feature.settings.extensions.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.model.SettingToggle
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    internal val settingsRepository: SettingsRepository,
    private val appRepository: AppRepository,
    internal val folderRepository: FolderRepository,
    internal val gridRepository: GridRepository,
    internal val undoController: UndoController = UndoController(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    internal fun updateUiState(transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update(transform)
    }

    /** Publishes the controller's current offer (or none) to [uiState]. */
    internal fun syncUndoState() {
        _uiState.update { it.copy(undoSnackbar = undoController.state.value) }
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
                hazeBlurLevel = glassLevelOf(settingsRepository.hazeBlurLevel),
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
                SettingsDialog.COLOR_PICKER -> it.copy(showColorPickerDialog = visible)
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

    fun setHeaderOffsetPercent(percent: Int) {
        val clamped = percent.coerceIn(0, MAX_HEADER_OFFSET_PERCENT)
        settingsRepository.headerOffsetPercent = clamped
        _uiState.update { it.copy(headerOffsetPercent = clamped) }
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
                    it.copy(errorMessage = TextResource.fromResId(R.string.settings_wallpaper_load_error))
                }
            }
        }
    }

    fun onActivityFinishedHandled() {
        _uiState.update { it.copy(shouldFinishActivity = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private companion object {
        const val MAX_HEADER_OFFSET_PERCENT = 60
    }
}
