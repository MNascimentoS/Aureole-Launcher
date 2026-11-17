package dev.mnascimentos.aureole.composable.sheets.model

import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig

/**
 * Parameters of `ThemePreviewOverlay`.
 *
 * @property title Title of the sheet.
 * @property base Theme currently applied to the app; the preview theme is resolved from it.
 * @property pending What the user is trying out, laid on top of [base].
 * @property isChanged Whether the pending choice differs from the current theme; read when the sheet closes.
 * @property onCommit Saves the pending choice; runs once on close, only when [isChanged].
 * @property onDismissRequest Called when the overlay closes, whichever way it closed.
 * @property onSettingsClick Opens the Aureole settings from the header logo; null hides the logo (used inside
 * the settings themselves, where it would be circular).
 * @property hasExternalBackdrop True when the screen around draws the backdrop itself.
 */
data class ThemePreviewOverlayParams(
    val title: String,
    val base: AureoleThemeConfig,
    val pending: ThemePreviewState,
    val isChanged: () -> Boolean,
    val onCommit: () -> Unit,
    val onDismissRequest: () -> Unit,
    val onSettingsClick: (() -> Unit)? = null,
    val hasExternalBackdrop: Boolean = false
)
