package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.feature.home.HomeViewModel

/** Opens the clock editor for the clock [clockId] of the grid; null edits the clock look of the settings. */
fun HomeViewModel.openEditClockBottomSheet(clockId: String? = null) {
    updateUiState { it.copy(showEditClockBottomSheet = true, editingClockId = clockId) }
}

fun HomeViewModel.closeEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = false, editingClockId = null) }
}

/** Puts the factory look back on the clock being edited only; with no clock id, on the settings clock look. */
fun HomeViewModel.resetClockSettings() {
    val clockId = uiState.value.editingClockId
    if (clockId != null) {
        editClock { ClockConfig() }
        return
    }
    settingsRepository.resetClockSettings()
    updateUiState {
        it.copy(
            isClockBackgroundEnabled = settingsRepository.isClockBackgroundEnabled,
            clockStyle = settingsRepository.clockStyle,
            clockCustomGreeting = settingsRepository.clockCustomGreeting,
            clockAlignment = settingsRepository.clockAlignment,
            clockFontFamily = settingsRepository.clockFontFamily,
            clockTimeFormat = settingsRepository.clockTimeFormat,
            clockDateFormat = settingsRepository.clockDateFormat,
            clockTextColor = settingsRepository.clockTextColor,
            clockBackgroundColor = settingsRepository.clockBackgroundColor
        )
    }
}
