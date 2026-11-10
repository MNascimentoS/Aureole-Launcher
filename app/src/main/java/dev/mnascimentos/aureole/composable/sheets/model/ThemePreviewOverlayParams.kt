package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig

/**
 * Parameters of `ThemePreviewOverlay`.
 *
 * @property title Title of the sheet.
 * @property base Theme currently applied to the app; the preview theme is resolved from it.
 * @property pending What the user is trying out, laid on top of [base].
 * @property onApply Called when the user taps Apply.
 * @property onDismissRequest Called when the overlay closes, whichever way it closed.
 */
data class ThemePreviewOverlayParams(
    val title: String,
    val base: AureoleThemeConfig,
    val pending: ThemePreviewState,
    val onApply: () -> Unit,
    val onDismissRequest: () -> Unit
)
