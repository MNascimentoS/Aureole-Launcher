package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft

/**
 * Called when the "Cards and glass" sheet closes. The taps already applied, so if the values differ from the ones
 * the sheet opened with, this offers an "Undo" that brings [before] back. No change, no undo.
 */
fun SettingsViewModel.finishCardsGlass(before: CardsGlassDraft) {
    if (uiState.value.toCardsGlassDraft() == before) return
    showUndo(TextResource.fromResId(R.string.settings_cards_glass_changed)) {
        setSettingValue(SettingValue.CardsGlass(before))
    }
}
