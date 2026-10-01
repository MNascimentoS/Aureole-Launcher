@file:Suppress("Filename", "MatchingDeclarationName")

package dev.mnascimentos.aureole.feature.settings.model

data class AppearanceDialogFlags(
    val showPalette: Boolean = false,
    val showFont: Boolean = false,
    val showSetBg: Boolean = false,
    val showBlur: Boolean = false,
    val showBorderRadius: Boolean = false,
)
