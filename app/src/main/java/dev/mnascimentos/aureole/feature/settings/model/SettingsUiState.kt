package dev.mnascimentos.aureole.feature.settings.model

import android.os.Build
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository

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
