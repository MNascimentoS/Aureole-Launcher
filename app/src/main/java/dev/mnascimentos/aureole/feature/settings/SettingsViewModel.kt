package dev.mnascimentos.aureole.feature.settings

import android.app.Application
import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
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
    SHOW_FOLDER_LABELS,
    HOME_OPENS_ALL_APPS,
    SHOW_ALL_APPS_ON_HOME,
    WIDGET_ROW,
    DYNAMIC_WALLPAPER
}

enum class SettingsDialog {
    FAVORITE_PICKER,
    SIDE_PANEL_POSITION,
    HAZE_OPACITY,
    COLOR_PICKER,
    RESTORE_WALLPAPER,
    CREATE_FOLDER
}

sealed interface SettingValue {
    data class SidePanelPosition(val position: String) : SettingValue
    data class HazeOpacity(val opacity: Float) : SettingValue
    data class ManualSeedColor(val color: Int) : SettingValue
}

data class SettingsUiState(
    val isLeftHandedMode: Boolean = false,
    val isSidePanelEnabled: Boolean = true,
    val sidePanelPosition: String = "Center",
    val showFolderLabels: Boolean = false,
    val homeButtonOpensAllApps: Boolean = true,
    val showAllAppsOnHome: Boolean = true,
    val isWidgetRowEnabled: Boolean = true,
    val showWidgetDots: Boolean = true,
    val favoriteAppPackages: List<String> = emptyList(),
    val allApps: List<AppInfo> = emptyList(),
    val isDefaultLauncher: Boolean = false,
    val showFavoritePickerDialog: Boolean = false,
    val showSidePanelPositionDialog: Boolean = false,
    val isCustomWallpaperSet: Boolean = false,
    val customWallpaperPath: String? = null,
    val showRestoreWallpaperDialog: Boolean = false,
    val showCreateFolderDialog: Boolean = false,
    val isDynamicWallpaperEnabled: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val manualSeedColor: Int = SettingsRepository.DEFAULT_SEED_COLOR,
    val isHazeEnabled: Boolean = true,
    val isHazeSupported: Boolean = true,
    val hazeOpacity: Float = 0.5f,
    val showHazeOpacityDialog: Boolean = false,
    val showColorPickerDialog: Boolean = false,
    val shouldFinishActivity: Boolean = false,
    val errorMessage: String? = null,
)

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
            )
        }
    }

    fun toggleSetting(toggle: SettingToggle) {
        when (toggle) {
            SettingToggle.SHOW_WIDGET_DOTS -> {
                val newValue = !_uiState.value.showWidgetDots
                settingsRepository.showWidgetDots = newValue
                _uiState.update { it.copy(showWidgetDots = newValue) }
            }
            SettingToggle.HAZE -> {
                if (!settingsRepository.isHazeSupported) return
                val newValue = !_uiState.value.isHazeEnabled
                settingsRepository.isHazeEnabled = newValue
                _uiState.update { it.copy(isHazeEnabled = newValue) }
            }
            SettingToggle.LEFT_HANDED_MODE -> {
                val newValue = !_uiState.value.isLeftHandedMode
                settingsRepository.isLeftHandedMode = newValue
                _uiState.update { it.copy(isLeftHandedMode = newValue) }
            }
            SettingToggle.SIDE_PANEL -> {
                val newValue = !_uiState.value.isSidePanelEnabled
                settingsRepository.isSidePanelEnabled = newValue
                _uiState.update { it.copy(isSidePanelEnabled = newValue) }
            }
            SettingToggle.SHOW_FOLDER_LABELS -> {
                val newValue = !_uiState.value.showFolderLabels
                settingsRepository.showFolderLabels = newValue
                _uiState.update { it.copy(showFolderLabels = newValue) }
            }
            SettingToggle.HOME_OPENS_ALL_APPS -> {
                val newValue = !_uiState.value.homeButtonOpensAllApps
                settingsRepository.homeButtonOpensAllApps = newValue
                _uiState.update { it.copy(homeButtonOpensAllApps = newValue) }
            }
            SettingToggle.SHOW_ALL_APPS_ON_HOME -> {
                val newValue = !_uiState.value.showAllAppsOnHome
                settingsRepository.showAllAppsOnHome = newValue
                _uiState.update { it.copy(showAllAppsOnHome = newValue) }
            }
            SettingToggle.WIDGET_ROW -> {
                val newValue = !_uiState.value.isWidgetRowEnabled
                settingsRepository.isWidgetRowEnabled = newValue
                _uiState.update { it.copy(isWidgetRowEnabled = newValue) }
            }
            SettingToggle.DYNAMIC_WALLPAPER -> {
                val newValue = !_uiState.value.isDynamicWallpaperEnabled
                settingsRepository.isDynamicWallpaperEnabled = newValue
                _uiState.update { it.copy(isDynamicWallpaperEnabled = newValue) }
            }
        }
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

fun SettingsViewModel.checkDefaultLauncher() {
    val context = getApplication<Application>()
    val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        roleManager?.isRoleHeld(RoleManager.ROLE_HOME) ?: false
    } else {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfo?.activityInfo?.packageName == context.packageName
    }
    updateUiState { it.copy(isDefaultLauncher = isDefault) }
}

fun SettingsViewModel.restoreDefaultWallpaper() {
    viewModelScope.launch {
        val app = getApplication<Application>()
        withContext(Dispatchers.IO) {
            settingsRepository.clearCustomWallpaper()
            try {
                val wallpaperManager = WallpaperManager.getInstance(app)
                wallpaperManager.clear()
            } catch (e: IOException) {
                Log.w("SettingsViewModel", "IOException clearing wallpaper", e)
            } catch (e: SecurityException) {
                Log.w("SettingsViewModel", "SecurityException clearing wallpaper", e)
            }
        }
        updateUiState {
            it.copy(
                isCustomWallpaperSet = false,
                customWallpaperPath = null,
                showRestoreWallpaperDialog = false
            )
        }
    }
}
