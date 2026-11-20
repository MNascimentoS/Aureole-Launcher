package dev.mnascimentos.aureole.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.migrateVividBackground
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.model.glassLevelOf
import dev.mnascimentos.aureole.core.undo.UndoController
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsToggleManager
import dev.mnascimentos.aureole.feature.settings.extensions.applySettingValue
import dev.mnascimentos.aureole.feature.settings.extensions.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.model.PendingPhotoSwap
import dev.mnascimentos.aureole.feature.settings.model.SettingToggle
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.pendingPhotoSwapOf
import dev.mnascimentos.aureole.feature.settings.model.toSeedSnapshot
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

    /** The photo just picked, whose "Undo" is offered when the editor that opened for it closes. */
    internal var pendingPhotoSwap: PendingPhotoSwap? = null

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
        settingsRepository.migrateVividBackground(RecommendedColors)
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
                favoriteAppPackages = settingsRepository.favoriteAppPackages,
                isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet,
                customWallpaperPath = settingsRepository.customWallpaperPath,
                wallpaperScaleType = settingsRepository.wallpaperScaleType,
                wallpaperAdjustments = settingsRepository.wallpaperAdjustments,
                applyToLockScreen = settingsRepository.applyToLockScreen,
                canSetWallpaper = settingsRepository.lockScreen.canSet(),
                isLockScreenNoticeSeen = settingsRepository.isLockScreenNoticeSeen,
                isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled,
                manualSeedColor = settingsRepository.manualSeedColor,
                selectedThemeName = settingsRepository.selectedThemeName,
                selectedFontName = settingsRepository.selectedFontName,
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
                secretBestScore = settingsRepository.secretBestScore,
                secretGamePlayed = settingsRepository.secretGamePlayed,
                secretSoundOn = settingsRepository.secretSoundOn,
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
                SettingsDialog.CREATE_FOLDER -> it.copy(showCreateFolderDialog = visible)
                SettingsDialog.RESET_GRID -> it.copy(showResetGridDialog = visible)
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
            val before = _uiState.value.toSeedSnapshot().customWallpaperPath
            val (success, kept) = withContext(Dispatchers.IO) {
                val kept = before != null && settingsRepository.wallpaperStash.keepCopy(before)
                settingsRepository.saveCustomWallpaper(uriString) to kept
            }

            if (success) {
                // The editor opens on the new photo (RF-01); its close offers the undo of the swap.
                pendingPhotoSwap = pendingPhotoSwapOf(before, kept)
                _uiState.update {
                    it.copy(
                        isCustomWallpaperSet = true,
                        customWallpaperPath = settingsRepository.customWallpaperPath,
                        wallpaperAdjustments = settingsRepository.wallpaperAdjustments,
                        isWallpaperEditorRequested = true,
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
