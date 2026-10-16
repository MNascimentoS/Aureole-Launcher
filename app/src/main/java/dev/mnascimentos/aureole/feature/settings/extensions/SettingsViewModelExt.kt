package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun SettingsViewModel.checkDefaultLauncher() {
    val isDefault = settingsRepository.checkDefaultLauncher()
    updateUiState { it.copy(isDefaultLauncher = isDefault) }
}

fun SettingsViewModel.restoreDefaultWallpaper() {
    viewModelScope.launch {
        withContext(Dispatchers.IO) {
            settingsRepository.restoreDefaultWallpaper()
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

fun SettingsViewModel.resetGridLayout() {
    viewModelScope.launch {
        gridRepository.resetToDefault()
        updateUiState {
            it.copy(
                showResetGridDialog = false,
                shouldFinishActivity = true,
                errorMessage = "Layout da tela inicial resetado para o padrão"
            )
        }
    }
}

fun SettingsViewModel.performFactoryReset() {
    viewModelScope.launch {
        updateUiState { it.copy(showFactoryResetDialog = false) }
        withContext(Dispatchers.IO) {
            settingsRepository.performFactoryReset()
        }
    }
}
