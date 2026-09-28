@file:Suppress("Filename", "MatchingDeclarationName")

package dev.mnascimentos.aureole.feature.settings.model

data class AppearanceDialogFlags(
    val showPalette: Boolean,
    val showFont: Boolean,
    val showSetBg: Boolean,
    val showBlur: Boolean,
)
