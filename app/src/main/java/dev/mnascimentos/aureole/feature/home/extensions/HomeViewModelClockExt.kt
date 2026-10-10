package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.updateClockStyle(style: String) {
    settingsRepository.clockStyle = style
    updateUiState { it.copy(clockStyle = style) }
}

fun HomeViewModel.updateClockCustomGreeting(greeting: String) {
    settingsRepository.clockCustomGreeting = greeting
    updateUiState { it.copy(clockCustomGreeting = greeting) }
}

fun HomeViewModel.updateClockAlignment(alignment: String) {
    settingsRepository.clockAlignment = alignment
    updateUiState { it.copy(clockAlignment = alignment) }
}

fun HomeViewModel.updateClockFontFamily(fontFamily: String) {
    settingsRepository.clockFontFamily = fontFamily
    updateUiState { it.copy(clockFontFamily = fontFamily) }
}

fun HomeViewModel.updateClockTimeFormat(format: String) {
    settingsRepository.clockTimeFormat = format
    updateUiState { it.copy(clockTimeFormat = format) }
}

fun HomeViewModel.updateClockDateFormat(format: String) {
    settingsRepository.clockDateFormat = format
    updateUiState { it.copy(clockDateFormat = format) }
}

fun HomeViewModel.updateClockTextColor(color: Int) {
    settingsRepository.clockTextColor = color
    updateUiState { it.copy(clockTextColor = color) }
}

fun HomeViewModel.updateClockBackgroundColor(color: Int) {
    settingsRepository.clockBackgroundColor = color
    updateUiState { it.copy(clockBackgroundColor = color) }
}

fun HomeViewModel.toggleClockBackground() {
    val newValue = !uiState.value.isClockBackgroundEnabled
    settingsRepository.isClockBackgroundEnabled = newValue
    updateUiState { it.copy(isClockBackgroundEnabled = newValue) }
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
