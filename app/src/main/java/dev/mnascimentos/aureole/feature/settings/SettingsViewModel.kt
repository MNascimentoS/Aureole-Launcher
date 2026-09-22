package dev.mnascimentos.aureole.feature.settings

import android.app.Application
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.ext.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

enum class SettingToggle {
    SHOW_WIDGET_DOTS,
    HAZE,
    LEFT_HANDED_MODE,
    SIDE_PANEL,
    SIDE_PANEL_BACKGROUND,
    CLOCK_BACKGROUND,
    SHOW_SIDE_PANEL_ADD_FOLDER_BUTTON,
    SHOW_FOLDER_LABELS,
    HOME_OPENS_ALL_APPS,
    SHOW_ALL_APPS_ON_HOME,
    WIDGET_ROW,
    DYNAMIC_WALLPAPER,
    IN_APP_UPDATE,
    THEMED_APP_ICONS,
    DISABLE_ALPHABET_SCRUBBER
}

enum class SettingsDialog {
    FAVORITE_PICKER,
    SIDE_PANEL_POSITION,
    HAZE_OPACITY,
    COLOR_PICKER,
    RESTORE_WALLPAPER,
    CREATE_FOLDER,
    RESET_GRID,
    FACTORY_RESET
}

@Suppress("TooManyFunctions")
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    internal val settingsRepository = SettingsRepository(application)
    private val appRepository = AppRepository(application)
    private val folderRepository = FolderRepository(application)

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
                isSidePanelEnabled = settingsRepository.isSidePanelEnabled,
                isSidePanelBackgroundEnabled = settingsRepository.isSidePanelBackgroundEnabled,
                isClockBackgroundEnabled = settingsRepository.isClockBackgroundEnabled,
                showSidePanelAddFolderButton = settingsRepository.showSidePanelAddFolderButton,
                sidePanelPosition = settingsRepository.sidePanelPosition,
                showFolderLabels = settingsRepository.showFolderLabels,
                homeButtonOpensAllApps = settingsRepository.homeButtonOpensAllApps,
                showAllAppsOnHome = settingsRepository.showAllAppsOnHome,
                isWidgetRowEnabled = settingsRepository.isWidgetRowEnabled,
                showWidgetDots = settingsRepository.showWidgetDots,
                favoriteAppPackages = settingsRepository.favoriteAppPackages,
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
                SettingsDialog.SIDE_PANEL_POSITION -> it.copy(showSidePanelPositionDialog = visible)
                SettingsDialog.HAZE_OPACITY -> it.copy(showHazeOpacityDialog = visible)
                SettingsDialog.COLOR_PICKER -> it.copy(showColorPickerDialog = visible)
                SettingsDialog.RESTORE_WALLPAPER -> it.copy(showRestoreWallpaperDialog = visible)
                SettingsDialog.CREATE_FOLDER -> it.copy(showCreateFolderDialog = visible)
                SettingsDialog.RESET_GRID -> it.copy(showResetGridDialog = visible)
                SettingsDialog.FACTORY_RESET -> it.copy(showFactoryResetDialog = visible)
            }
        }
    }

    fun setSettingValue(value: SettingValue) {
        when (value) {
            is SettingValue.SidePanelPosition -> {
                settingsRepository.sidePanelPosition = value.position
                _uiState.update { it.copy(sidePanelPosition = value.position, showSidePanelPositionDialog = false) }
            }
            is SettingValue.HazeOpacity -> {
                settingsRepository.hazeOpacity = value.opacity
                _uiState.update { it.copy(hazeOpacity = value.opacity, showHazeOpacityDialog = false) }
            }
            is SettingValue.ManualSeedColor -> {
                settingsRepository.manualSeedColor = value.color
                _uiState.update { it.copy(manualSeedColor = value.color, showColorPickerDialog = false) }
            }
        }
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

    fun setCustomWallpaper(uri: Uri) {
        viewModelScope.launch {
            val app = getApplication<Application>()
            val success = withContext(Dispatchers.IO) {
                try {
                    val inputStream = app.contentResolver.openInputStream(uri) ?: return@withContext false
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                    if (bitmap == null) return@withContext false

                    val file = File(app.filesDir, "custom_wallpaper.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                    }

                    settingsRepository.customWallpaperPath = file.absolutePath
                    settingsRepository.isCustomWallpaperSet = true

                    try {
                        val wallpaperManager = WallpaperManager.getInstance(app)
                        wallpaperManager.setBitmap(bitmap)
                    } catch (e: IOException) {
                        Log.w(TAG, "Failed to set system wallpaper bitmap", e)
                    } catch (e: SecurityException) {
                        Log.w(TAG, "SecurityException setting system wallpaper", e)
                    } catch (e: IllegalArgumentException) {
                        Log.w(TAG, "IllegalArgumentException setting system wallpaper", e)
                    }

                    true
                } catch (e: IOException) {
                    Log.w(TAG, "IOException in setCustomWallpaper", e)
                    false
                } catch (e: SecurityException) {
                    Log.w(TAG, "SecurityException in setCustomWallpaper", e)
                    false
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "IllegalArgumentException in setCustomWallpaper", e)
                    false
                }
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

    fun resetGridLayout() {
        viewModelScope.launch {
            GridRepository(getApplication()).resetToDefault()
            _uiState.update {
                it.copy(
                    showResetGridDialog = false,
                    shouldFinishActivity = true,
                    errorMessage = "Layout da tela inicial resetado para o padrão"
                )
            }
        }
    }

    fun performFactoryReset() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            _uiState.update { it.copy(showFactoryResetDialog = false) }
            withContext(Dispatchers.IO) {
                try {
                    // CA-04: Clear Databases
                    try {
                        AppDatabase.getInstance(app).clearAllTables()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error clearing Room database", e)
                    }
                    app.deleteDatabase("aureole_app_database.db")
                    app.deleteDatabase("aureole_folders.db")

                    // CA-03: Clear SharedPreferences
                    listOf("aureole_grid_prefs", "aureole_settings_prefs", "aureole_widget_prefs", "${app.packageName}_preferences").forEach { prefName ->
                        app.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().commit()
                    }

                    // CA-05: Clear Cache and files
                    app.cacheDir.deleteRecursively()
                    File(app.filesDir, "custom_wallpaper.jpg").delete()
                } catch (e: Exception) {
                    Log.w(TAG, "Error performing factory reset", e)
                }
            }

            // CA-06: Restart Workspace / Activity
            val intent = app.packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            if (intent != null) {
                app.startActivity(intent)
            }
            Runtime.getRuntime().exit(0)
        }
    }

    fun onActivityFinishedHandled() {
        _uiState.update { it.copy(shouldFinishActivity = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
        private const val JPEG_QUALITY = 90
    }
}
