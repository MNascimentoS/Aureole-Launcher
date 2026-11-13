package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.openEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = true) }
}

fun HomeViewModel.closeEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = false) }
}

fun HomeViewModel.resetClockSettings() {
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
