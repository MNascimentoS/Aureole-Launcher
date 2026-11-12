package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The look of a clock with no config of its own: the clock fields of the state, read from the settings. */
fun MainUiState.defaultClockConfig(): ClockConfig = ClockConfig(
    style = clockStyle,
    customGreeting = clockCustomGreeting,
    alignment = clockAlignment,
    fontFamily = clockFontFamily,
    timeFormat = clockTimeFormat,
    dateFormat = clockDateFormat,
    textColor = clockTextColor,
    backgroundColor = clockBackgroundColor,
    isBackgroundEnabled = isClockBackgroundEnabled
)

/** The look of the clock [itemId]: its own config, or the [defaultClockConfig] when it has none (or no id). */
fun MainUiState.clockConfigOf(itemId: String?): ClockConfig =
    itemId?.let { clockConfigs[it] } ?: defaultClockConfig()

/** The look of the clock the clock editor is changing. */
val MainUiState.editingClockConfig: ClockConfig
    get() = clockConfigOf(editingClockId)

/** [this] state with its clock fields set to [config]. */
fun MainUiState.withClockFields(config: ClockConfig): MainUiState = copy(
    clockStyle = config.style,
    clockCustomGreeting = config.customGreeting,
    clockAlignment = config.alignment,
    clockFontFamily = config.fontFamily,
    clockTimeFormat = config.timeFormat,
    clockDateFormat = config.dateFormat,
    clockTextColor = config.textColor,
    clockBackgroundColor = config.backgroundColor,
    isClockBackgroundEnabled = config.isBackgroundEnabled
)

/**
 * The state the clock editor shows: its clock fields hold the look of the clock being edited, so every control
 * of the editor reads that clock without knowing its id.
 */
fun MainUiState.forClockEditor(): MainUiState = withClockFields(editingClockConfig)

/** Every field of [this] config as an override of the clock header. */
fun ClockConfig.toHeaderConfig(): ClockHeaderConfig = ClockHeaderConfig(
    style = style,
    customGreeting = customGreeting,
    alignment = alignment,
    fontFamily = fontFamily,
    timeFormat = timeFormat,
    dateFormat = dateFormat,
    textColor = textColor,
    backgroundColor = backgroundColor,
    isBackgroundEnabled = isBackgroundEnabled
)
