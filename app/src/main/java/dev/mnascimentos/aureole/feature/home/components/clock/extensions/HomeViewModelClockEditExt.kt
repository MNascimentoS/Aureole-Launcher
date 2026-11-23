package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import kotlinx.coroutines.launch

/**
 * Applies [change] to the clock the editor is open for (`editingClockId`) and saves it as that clock's own config;
 * the other clocks and the settings (the look of new clocks) stay as they were. With no clock id (the clock
 * header of the app lists) it changes the clock look of the settings, as before there were many clocks.
 */
internal fun HomeViewModel.editClock(change: (ClockConfig) -> ClockConfig) {
    val state = uiState.value
    val clockId = state.editingClockId
    saveClockConfig(clockId, change(state.clockConfigOf(clockId)))
}

/**
 * Saves [config] as the look of the clock [clockId] (null: the clock look of the settings). Also used by the undo of
 * the editor, which runs after the editor closed (RF-D7).
 */
internal fun HomeViewModel.saveClockConfig(clockId: String?, config: ClockConfig) {
    if (clockId == null) {
        saveDefaultClockConfig(config)
        return
    }
    // The state changes at once; the repository keeps the saves in call order.
    updateUiState { it.copy(clockConfigs = it.clockConfigs + (clockId to config)) }
    viewModelScope.launch { clockConfigRepository.save(clockId, config) }
}

/** Writes [config] to the clock look of the settings: the look of every clock without a config of its own. */
private fun HomeViewModel.saveDefaultClockConfig(config: ClockConfig) {
    with(settingsRepository) {
        clockStyle = config.style
        clockCustomGreeting = config.customGreeting
        clockMessage = config.message
        clockAlignment = config.alignment
        clockFontFamily = config.fontFamily
        clockFontBold = config.isFontBold
        clockTimeFormat = config.timeFormat
        clockDateFormat = config.dateFormat
        clockTextColor = config.textColor
        clockBackgroundColor = config.backgroundColor
        clockTextColorRef = config.textColorRef
        clockBackgroundColorRef = config.backgroundColorRef
    }
    updateUiState { it.withClockFields(config) }
}

/** Keeps the `clockConfigs` of the state in step with the saved clock configs. */
internal fun HomeViewModel.observeClockConfigs() {
    viewModelScope.launch {
        clockConfigRepository.configs.collect { configs -> updateUiState { it.copy(clockConfigs = configs) } }
    }
}
