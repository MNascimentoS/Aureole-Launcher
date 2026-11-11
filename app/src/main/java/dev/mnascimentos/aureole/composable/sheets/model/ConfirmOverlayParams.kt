package dev.mnascimentos.aureole.composable.sheets.model

import dev.chrisbanes.haze.HazeState

/**
 * Parameters of `AureoleConfirmOverlay`.
 *
 * @property title Title of the sheet.
 * @property primaryLabel Text of the filled button (Apply, Save, Confirm).
 * @property onPrimary Called when the filled button is tapped; the overlay then closes.
 * @property onDismissRequest Called when the overlay has closed, whichever way it closed.
 * @property isLeftHanded Puts the filled button under the left thumb.
 * @property secondaryLabel Text of the outlined button; null uses "Cancel".
 * @property hasSecondary False leaves only the filled button, full width (editors that apply on the spot).
 * @property onSettingsClick Opens the Aureole settings from the header logo; null hides the logo.
 * @property hazeState Blur state of the screen behind the overlay.
 */
data class ConfirmOverlayParams(
    val title: String,
    val primaryLabel: String,
    val onPrimary: () -> Unit,
    val onDismissRequest: () -> Unit,
    val isLeftHanded: Boolean = false,
    val secondaryLabel: String? = null,
    val hasSecondary: Boolean = true,
    val onSettingsClick: (() -> Unit)? = null,
    val hazeState: HazeState? = null
)
