package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** Palette and font currently applied, so the appearance pickers can mark the active option. */
data class AppearanceSelection(
    val paletteName: String = "",
    val fontName: String = ""
)
