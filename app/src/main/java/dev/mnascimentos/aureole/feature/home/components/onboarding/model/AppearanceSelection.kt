package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.repository.SettingsRepository

/**
 * Palette and font currently applied, so the Visual sheet can mark the active option, and the clock formats its
 * mini-home writes the real time and date with (RF-C1, D-06).
 */
data class AppearanceSelection(
    val paletteName: String = "",
    val fontName: String = "",
    val clockTimeFormat: String = SettingsRepository.DEFAULT_CLOCK_TIME_FORMAT,
    val clockDateFormat: String = SettingsRepository.DEFAULT_CLOCK_DATE_FORMAT
)
