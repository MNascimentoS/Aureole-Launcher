package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.selectFont
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.selectPalette

/** Saves a palette and offers an "Undo" that brings back the previous palette and seed color. */
fun HomeViewModel.changePalette(themeName: String) {
    val state = uiState.value
    val previousName = state.selectedThemeName
    val previousSeed = state.manualSeedColor
    selectPalette(themeName)
    showUndo(TextResource.fromResId(R.string.undo_palette_changed)) {
        settingsRepository.selectedThemeName = previousName
        settingsRepository.manualSeedColor = previousSeed
        updateUiState { it.copy(selectedThemeName = previousName, manualSeedColor = previousSeed) }
    }
}

/** Saves a font and offers an "Undo" that brings back the previous one. */
fun HomeViewModel.changeFont(fontName: String) {
    val previous = uiState.value.selectedFontName
    selectFont(fontName)
    showUndo(TextResource.fromResId(R.string.undo_font_changed)) { selectFont(previous) }
}
