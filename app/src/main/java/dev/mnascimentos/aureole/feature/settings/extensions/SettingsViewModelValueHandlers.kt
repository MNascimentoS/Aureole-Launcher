package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SettingValue

internal fun SettingsViewModel.applySettingValue(value: SettingValue) {
    when (value) {
        is SettingValue.SidePanelPosition -> handleSidePanelPosition(value.position)
        is SettingValue.HazeOpacity -> handleHazeOpacity(value.opacity)
        is SettingValue.ManualSeedColor -> handleManualSeedColor(value.color)
        is SettingValue.SelectedTheme -> handleSelectedTheme(value.themeName)
        is SettingValue.SelectedFont -> handleSelectedFont(value.fontName)
        is SettingValue.SettingsButtonPosition -> handleSettingsButtonPosition(value.position)
        is SettingValue.SearchIconPosition -> handleSearchIconPosition(value.position)
    }
}

private fun SettingsViewModel.handleSidePanelPosition(position: String) {
    settingsRepository.sidePanelPosition = position
    updateUiState { it.copy(sidePanelPosition = position, showSidePanelPositionDialog = false) }
}

private fun SettingsViewModel.handleHazeOpacity(opacity: Float) {
    settingsRepository.hazeOpacity = opacity
    updateUiState { it.copy(hazeOpacity = opacity, showHazeOpacityDialog = false) }
}

private fun SettingsViewModel.handleManualSeedColor(color: Int) {
    settingsRepository.manualSeedColor = color
    settingsRepository.clearCustomWallpaper()
    updateUiState {
        it.copy(
            manualSeedColor = color,
            isCustomWallpaperSet = false,
            customWallpaperPath = null,
            showColorPickerDialog = false
        )
    }
}

private fun SettingsViewModel.handleSelectedTheme(themeName: String) {
    val palette = getPaletteByName(themeName)
    val surfaceVariantColorArgb = palette.colors.surfaceVariant.toArgb()
    settingsRepository.selectedThemeName = themeName
    settingsRepository.manualSeedColor = surfaceVariantColorArgb
    updateUiState {
        it.copy(
            selectedThemeName = themeName,
            manualSeedColor = surfaceVariantColorArgb
        )
    }
}

private fun SettingsViewModel.handleSelectedFont(fontName: String) {
    settingsRepository.selectedFontName = fontName
    updateUiState { it.copy(selectedFontName = fontName) }
}

private fun SettingsViewModel.handleSettingsButtonPosition(position: String) {
    settingsRepository.settingsButtonPosition = position
    updateUiState { it.copy(settingsButtonPosition = position, showSettingsButtonPositionDialog = false) }
}

private fun SettingsViewModel.handleSearchIconPosition(position: String) {
    settingsRepository.searchIconPosition = position
    updateUiState { it.copy(searchIconPosition = position, showSearchIconPositionDialog = false) }
}
