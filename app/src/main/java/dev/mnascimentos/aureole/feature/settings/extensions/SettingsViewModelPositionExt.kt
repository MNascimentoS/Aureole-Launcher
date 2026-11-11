package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.PositionKind
import dev.mnascimentos.aureole.feature.settings.model.positionOf
import dev.mnascimentos.aureole.feature.settings.model.toSettingValue

/**
 * Applies the chosen position at once and offers an "Undo" that brings the previous one back.
 * Picking the position that is already set changes nothing and offers no undo.
 */
fun SettingsViewModel.selectPosition(kind: PositionKind, position: String) {
    val previous = uiState.value.positionOf(kind)
    if (previous == position) return
    setSettingValue(kind.toSettingValue(position))
    showUndo(TextResource.fromResId(R.string.settings_position_changed)) {
        setSettingValue(kind.toSettingValue(previous))
    }
}
