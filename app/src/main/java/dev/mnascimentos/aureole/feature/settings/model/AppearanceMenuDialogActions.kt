package dev.mnascimentos.aureole.feature.settings.model

/**
 * Rows of the Appearance sheet that open another sheet in its place (background, cards and glass) or a picker
 * inside it (palette, font).
 */
data class AppearanceMenuDialogActions(
    val onShowSetBackgroundDialog: () -> Unit,
    val onShowCardsGlassDialog: () -> Unit,
    val onShowPaletteDialog: () -> Unit,
    val onShowFontDialog: () -> Unit
)
