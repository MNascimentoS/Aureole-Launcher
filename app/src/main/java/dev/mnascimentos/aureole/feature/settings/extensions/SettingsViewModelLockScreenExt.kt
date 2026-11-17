package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.lockScreenErrorOf
import kotlinx.coroutines.launch

/**
 * Puts the background just saved on the lock screen (RF-14), when the option is on. With [undo] it brings back the
 * image applied before instead (RF-17). Runs in the background; a failure shows "Couldn't change the lock screen"
 * and the home carries on as it is.
 */
internal fun SettingsViewModel.applyLockScreen(undo: Boolean = false) {
    if (!settingsRepository.applyToLockScreen) return
    viewModelScope.launch {
        val lockScreen = settingsRepository.lockScreen
        val applied = if (undo) lockScreen.applyPrevious() else lockScreen.applyCurrent()
        reportLockScreenResult(applied)
    }
}

/** "Also use on lock screen" turned on (RF-13): remembers the warning was seen and applies the background now. */
fun SettingsViewModel.turnLockScreenOn() {
    if (!settingsRepository.lockScreen.canSet()) return
    settingsRepository.applyToLockScreen = true
    settingsRepository.isLockScreenNoticeSeen = true
    updateUiState { it.copy(applyToLockScreen = true, isLockScreenNoticeSeen = true) }
    applyLockScreen()
}

/**
 * Turned off (RF-16): the lock screen stays as it is, or goes back to the system wallpaper when [restoreSystem].
 */
fun SettingsViewModel.turnLockScreenOff(restoreSystem: Boolean) {
    settingsRepository.applyToLockScreen = false
    updateUiState { it.copy(applyToLockScreen = false) }
    if (!restoreSystem) return
    viewModelScope.launch { reportLockScreenResult(settingsRepository.lockScreen.clear()) }
}

private fun SettingsViewModel.reportLockScreenResult(applied: Boolean) {
    val error = lockScreenErrorOf(applied) ?: return
    updateUiState { it.copy(errorMessage = error) }
}
