package dev.mnascimentos.aureole.feature.settings.model

/**
 * Rows of the Appearance sheet that open another sheet in its place.
 */
data class AppearanceMenuDialogActions(
    val onShowSetBackgroundDialog: () -> Unit,
    val onShowCardsGlassDialog: () -> Unit,
    val onShowPaletteDialog: () -> Unit,
    val onShowFontDialog: () -> Unit,
    val onShowCustomColorsDialog: () -> Unit = {}
)
