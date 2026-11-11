package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.runtime.Composable

/**
 * Optional extras of the palette and font sheets.
 *
 * @property footer Content below the grid, such as the onboarding hint.
 * @property onSettingsClick Opens the Aureole settings from the header logo; null hides the logo (used inside
 * the settings themselves, where it would be circular).
 */
data class ThemePickerSheetOptions(
    val footer: (@Composable () -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null
)

/**
 * Layout of `ThemePickerGrid`.
 *
 * @property label Small section label above the grid.
 * @property columns Number of grid columns.
 * @property footer Content below the grid.
 */
data class ThemePickerGridParams(
    val label: String,
    val columns: Int,
    val footer: (@Composable () -> Unit)? = null
)
