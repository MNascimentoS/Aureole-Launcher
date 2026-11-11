package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun SettingsViewModel.checkDefaultLauncher() {
    val isDefault = settingsRepository.checkDefaultLauncher()
    updateUiState { it.copy(isDefaultLauncher = isDefault) }
}

/** Restores the default background at once and offers an "Undo" for the custom image that was set aside. */
fun SettingsViewModel.restoreDefaultWallpaper() {
    viewModelScope.launch {
        val previousPath = withContext(Dispatchers.IO) {
            settingsRepository.wallpaperStash.restoreDefaultKeepingUndo()
        }
        updateUiState {
            it.copy(isCustomWallpaperSet = false, customWallpaperPath = null)
        }
        if (previousPath != null) {
            showUndo(TextResource.fromResId(R.string.settings_appearance_background_restored)) {
                undoRestoreWallpaper(previousPath)
            }
        }
    }
}

private fun SettingsViewModel.undoRestoreWallpaper(path: String) {
    viewModelScope.launch {
        val restored = withContext(Dispatchers.IO) { settingsRepository.wallpaperStash.undo(path) }
        if (restored) {
            updateUiState { it.copy(isCustomWallpaperSet = true, customWallpaperPath = path) }
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
