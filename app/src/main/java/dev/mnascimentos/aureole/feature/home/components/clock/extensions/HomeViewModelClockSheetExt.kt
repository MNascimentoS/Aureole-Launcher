package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import androidx.compose.ui.unit.DpSize
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
            clockMessage = settingsRepository.clockMessage,
            clockAlignment = settingsRepository.clockAlignment,
            clockFontFamily = settingsRepository.clockFontFamily,
            clockFontBold = settingsRepository.clockFontBold,
            clockTimeFormat = settingsRepository.clockTimeFormat,
            clockDateFormat = settingsRepository.clockDateFormat,
            clockTextColor = settingsRepository.clockTextColor,
            clockBackgroundColor = settingsRepository.clockBackgroundColor,
            clockTextColorRef = settingsRepository.clockTextColorRef,
            clockBackgroundColorRef = settingsRepository.clockBackgroundColorRef
        )
    }
}

/** Keeps in memory the size the clock [clockId] has on the home (null: the list header), for the editor preview. */
fun HomeViewModel.recordClockSize(clockId: String?, size: DpSize) {
    if (uiState.value.clockSizeOf(clockId) == size) return
    updateUiState { it.withClockSize(clockId, size) }
}
