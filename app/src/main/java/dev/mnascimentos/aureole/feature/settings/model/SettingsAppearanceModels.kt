package dev.mnascimentos.aureole.feature.settings.model

data class AppearanceDialogFlags(
    val showPalette: Boolean = false,
    val showFont: Boolean = false,
    val showSetBg: Boolean = false,
    val showCardsGlass: Boolean = false,
)

/**
 * Dialog actions for appearance settings menu items.
 */
data class AppearanceMenuDialogActions(
    val onShowSetBackgroundDialog: () -> Unit,
    val onShowCardsGlassDialog: () -> Unit,
    val onShowPaletteDialog: () -> Unit,
    val onShowFontDialog: () -> Unit
)
