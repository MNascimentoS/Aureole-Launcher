package dev.mnascimentos.aureole.composable.menu.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/** How long the user has to hold a row that [MenuRowParams.requiresHold] before it fires. */
const val MENU_HOLD_MS = 1000L

/**
 * One row of an [dev.mnascimentos.aureole.composable.menu.AureoleMenuRow] list.
 *
 * @property title Row label.
 * @property subtitle Optional second line under the title ("4 components").
 * @property onClick Called when the row is tapped.
 * @property icon Optional leading icon drawn centered in a fixed 36 dp column; receives the modifier and the tint.
 * @property isDestructive Draws the row with the error color (Remove).
 * @property showChevron Draws a chevron at the far end, pointing away from the thumb side start.
 * @property requiresHold Needs a hold of [MENU_HOLD_MS] instead of a tap, with a fill that grows while holding.
 *   For actions the system does not confirm again.
 */
data class MenuRowParams(
    val title: String,
    val onClick: () -> Unit,
    val icon: (@Composable (Modifier, Color) -> Unit)? = null,
    val isDestructive: Boolean = false,
    val showChevron: Boolean = false,
    val subtitle: String? = null,
    val requiresHold: Boolean = false
)

/** Slots of the menu sheet that depend on the caller. */
data class MenuSheetParams(
    val titleIcon: (@Composable (Modifier) -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
    val bottomBar: (@Composable () -> Unit)? = null
)

/** Fraction (0 to 1) of a hold that has elapsed. */
fun holdProgress(elapsedMs: Long, holdMs: Long = MENU_HOLD_MS): Float =
    if (holdMs <= 0L) 1f else (elapsedMs.toFloat() / holdMs).coerceIn(0f, 1f)

/**
 * One [dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow].
 *
 * @property title Row label.
 * @property checked Current state of the switch.
 * @property onCheckedChange Called with the new state when the row is tapped.
 * @property description Optional second line under the title.
 * @property icon Optional leading icon drawn centered in a fixed column; receives the modifier and the tint.
 */
data class SwitchRowParams(
    val title: String,
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val description: String? = null,
    val icon: (@Composable (Modifier, Color) -> Unit)? = null
)
