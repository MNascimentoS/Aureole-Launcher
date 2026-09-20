package dev.mnascimentos.aureole.ui.settings

import android.app.Application
import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.data.model.AppFolder
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.data.repository.AppRepository
import dev.mnascimentos.aureole.data.repository.FolderRepository
import dev.mnascimentos.aureole.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class SettingsUiState(
    val isLeftHandedMode: Boolean = false,
    val isSidePanelEnabled: Boolean = true,
    val sidePanelPosition: String = "Center",
    val showFolderLabels: Boolean = false,
    val homeButtonOpensAllApps: Boolean = true,
    val showAllAppsOnHome: Boolean = true,
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
    val showColorPickerDialog: Boolean = false,
    val shouldFinishActivity: Boolean = false,
    val errorMessage: String? = null,
)

@Suppress("TooManyFunctions")
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val appRepository = AppRepository(application)
    private val folderRepository = FolderRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadApps()
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
                favoriteAppPackages = settingsRepository.favoriteAppPackages,
                isCustomWallpaperSet = settingsRepository.isCustomWallpaperSet,
                customWallpaperPath = settingsRepository.customWallpaperPath,
                isDynamicWallpaperEnabled = settingsRepository.isDynamicWallpaperEnabled,
                manualSeedColor = settingsRepository.manualSeedColor,
            )
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            val apps = appRepository.getInstalledApps()
            _uiState.update { it.copy(allApps = apps) }
        }
    }

    fun checkDefaultLauncher() {
        val context = getApplication<Application>()
        val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_HOME) ?: false
        } else {
            val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
            val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo?.activityInfo?.packageName == context.packageName
        }
        _uiState.update { it.copy(isDefaultLauncher = isDefault) }
    }

    fun toggleLeftHandedMode() {
        val newValue = !_uiState.value.isLeftHandedMode
        settingsRepository.isLeftHandedMode = newValue
        _uiState.update { it.copy(isLeftHandedMode = newValue) }
    }

    fun toggleSidePanel() {
        val newValue = !_uiState.value.isSidePanelEnabled
        settingsRepository.isSidePanelEnabled = newValue
        _uiState.update { it.copy(isSidePanelEnabled = newValue) }
    }

    fun toggleShowFolderLabels() {
        val newValue = !_uiState.value.showFolderLabels
        settingsRepository.showFolderLabels = newValue
        _uiState.update { it.copy(showFolderLabels = newValue) }
    }

    fun setSidePanelPosition(position: String) {
        settingsRepository.sidePanelPosition = position
        _uiState.update { it.copy(sidePanelPosition = position, showSidePanelPositionDialog = false) }
    }

    fun toggleHomeOpensAllApps() {
        val newValue = !_uiState.value.homeButtonOpensAllApps
        settingsRepository.homeButtonOpensAllApps = newValue
        _uiState.update { it.copy(homeButtonOpensAllApps = newValue) }
    }

    fun toggleShowAllAppsOnHome() {
        val newValue = !_uiState.value.showAllAppsOnHome
        settingsRepository.showAllAppsOnHome = newValue
        _uiState.update { it.copy(showAllAppsOnHome = newValue) }
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

    fun setShowFavoritePicker(show: Boolean) {
        _uiState.update { it.copy(showFavoritePickerDialog = show) }
    }

    fun setShowSidePanelPositionDialog(show: Boolean) {
        _uiState.update { it.copy(showSidePanelPositionDialog = show) }
    }

    fun toggleDynamicWallpaper() {
        val newValue = !_uiState.value.isDynamicWallpaperEnabled
        settingsRepository.isDynamicWallpaperEnabled = newValue
        _uiState.update { it.copy(isDynamicWallpaperEnabled = newValue) }
    }

    fun setManualSeedColor(color: Int) {
        settingsRepository.manualSeedColor = color
        _uiState.update { it.copy(manualSeedColor = color, showColorPickerDialog = false) }
    }

    fun setShowColorPickerDialog(show: Boolean) {
        _uiState.update { it.copy(showColorPickerDialog = show) }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
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
                    } catch (_: Exception) {
                        // System wallpaper manager might fail or require permissions
                    }

                    true
                } catch (_: Exception) {
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

    fun setShowRestoreWallpaperDialog(show: Boolean) {
        _uiState.update { it.copy(showRestoreWallpaperDialog = show) }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun restoreDefaultWallpaper() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            withContext(Dispatchers.IO) {
                settingsRepository.clearCustomWallpaper()
                try {
                    val wallpaperManager = WallpaperManager.getInstance(app)
                    wallpaperManager.clear()
                } catch (_: Exception) {}
            }
            _uiState.update {
                it.copy(
                    isCustomWallpaperSet = false,
                    customWallpaperPath = null,
                    showRestoreWallpaperDialog = false
                )
            }
        }
    }

    fun setShowCreateFolderDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateFolderDialog = show) }
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
        private const val JPEG_QUALITY = 90
    }
}
