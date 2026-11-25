package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.runtime.Composable

/**
 * Optional extras of the palette and font sheets.
 *
 * @property footer Content below the grid, such as the onboarding hint.
 * @property onSettingsClick Opens the Aureole settings from the header logo; null hides the logo (used inside
 * the settings themselves, where it would be circular).
 * @property hasExternalBackdrop True when the screen around draws the backdrop itself.
 * @property onPreview Called when the user taps an option to preview it (even one equal to the current).
 * @property componentFont Font sheet only: opened by a component with its own font (the clock) instead of by
 * Customize › Font; null keeps the launcher font picker.
 */
data class ThemePickerSheetOptions(
    val footer: (@Composable () -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
    val hasExternalBackdrop: Boolean = false,
    val onPreview: () -> Unit = {},
    val componentFont: ComponentFontOptions? = null
)

/**
 * Layout of `ThemePickerGrid`.
 *
 * @property label Small section label above the grid.
 * @property columns Number of grid columns.
 * @property footer Content below the grid.
 * @property fadesEdges The grid fades where it meets a fixed tab bar instead of being cut (sheets with tabs).
 */
data class ThemePickerGridParams(
    val label: String,
    val columns: Int,
    val footer: (@Composable () -> Unit)? = null,
    val fadesEdges: Boolean = false
)
