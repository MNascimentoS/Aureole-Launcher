package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The state with the global clock settings read from [settings]. */
fun MainUiState.withClockSettings(settings: SettingsRepository): MainUiState = copy(
    clockStyle = settings.clockStyle,
    clockCustomGreeting = settings.clockCustomGreeting,
    clockMessage = settings.clockMessage,
    clockAlignment = settings.clockAlignment,
    clockFontFamily = settings.clockFontFamily,
    clockFontBold = settings.clockFontBold,
    clockTimeFormat = settings.clockTimeFormat,
    clockDateFormat = settings.clockDateFormat,
    clockTextColor = settings.clockTextColor,
    clockBackgroundColor = settings.clockBackgroundColor,
    clockTextColorRef = settings.clockTextColorRef,
    clockBackgroundColorRef = settings.clockBackgroundColorRef
)
