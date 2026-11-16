package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SeedSnapshot
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.toSeedSnapshot
import dev.mnascimentos.aureole.feature.settings.model.withSeedSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Saves a manual color (background or accent) and offers an "Undo" that brings back the palette, the color and the
 * custom image the color replaced; the image is kept aside in the wallpaper stash until the undo window ends.
 */
fun SettingsViewModel.changeManualSeedColor(color: Int, @StringRes message: Int) {
    val before = uiState.value.toSeedSnapshot()
    viewModelScope.launch {
        val stashedPath = before.customWallpaperPath?.let {
            withContext(Dispatchers.IO) { settingsRepository.wallpaperStash.restoreDefaultKeepingUndo() }
        }
        setSettingValue(SettingValue.ManualSeedColor(color))
        showUndo(TextResource.fromResId(message)) { restoreSeed(before, stashedPath) }
    }
}

private fun SettingsViewModel.restoreSeed(before: SeedSnapshot, stashedPath: String?) {
    viewModelScope.launch {
        val restored = stashedPath != null &&
            withContext(Dispatchers.IO) { settingsRepository.wallpaperStash.undo(stashedPath) }
        settingsRepository.selectedThemeName = before.themeName
        settingsRepository.manualSeedColor = before.seedColor
        updateUiState { it.withSeedSnapshot(before, keepWallpaper = restored) }
    }
}

/** Saves a palette and offers an "Undo" that brings back the previous palette and seed color. */
fun SettingsViewModel.changeTheme(themeName: String) {
    val before = uiState.value.toSeedSnapshot()
    setSettingValue(SettingValue.SelectedTheme(themeName))
    showUndo(TextResource.fromResId(R.string.undo_palette_changed)) {
        settingsRepository.selectedThemeName = before.themeName
        settingsRepository.manualSeedColor = before.seedColor
        updateUiState { it.copy(selectedThemeName = before.themeName, manualSeedColor = before.seedColor) }
    }
}

/** Saves a font and offers an "Undo" that brings back the previous one. */
fun SettingsViewModel.changeFont(fontName: String) {
    val before = uiState.value.selectedFontName
    setSettingValue(SettingValue.SelectedFont(fontName))
    showUndo(TextResource.fromResId(R.string.undo_font_changed)) {
        setSettingValue(SettingValue.SelectedFont(before))
    }
}

/**
 * Offers the "Undo" of a custom image just picked. [before] is the path of the image it replaced (kept aside when
 * [kept]) or null when the background was a solid color.
 */
internal fun SettingsViewModel.offerCustomWallpaperUndo(before: String?, kept: Boolean) {
    showUndo(TextResource.fromResId(R.string.undo_background_changed)) { undoCustomWallpaper(before, kept) }
}

private fun SettingsViewModel.undoCustomWallpaper(before: String?, kept: Boolean) {
    viewModelScope.launch {
        val restored = before != null && kept &&
            withContext(Dispatchers.IO) { settingsRepository.wallpaperStash.undo(before) }
        if (!restored) withContext(Dispatchers.IO) { settingsRepository.clearCustomWallpaper() }
        updateUiState {
            it.copy(isCustomWallpaperSet = restored, customWallpaperPath = before.takeIf { restored })
        }
    }
}
