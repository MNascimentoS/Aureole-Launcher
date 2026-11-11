package dev.mnascimentos.aureole.composable.menu.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/** How long the user has to hold a destructive quick action before it fires. */
const val MENU_HOLD_MS = 1000L

/**
 * One row of an [dev.mnascimentos.aureole.composable.menu.AureoleMenuRow] list.
 *
 * @property title Row label.
 * @property subtitle Optional second line under the title ("4 components").
 * @property onClick Called when the row is tapped.
 * @property icon Optional leading icon drawn inside a rounded tile; receives the modifier and the tint.
 * @property isDestructive Draws the row with the error color (Remove).
 * @property showChevron Draws a chevron at the far end, pointing away from the thumb side start.
 */
data class MenuRowParams(
    val title: String,
    val onClick: () -> Unit,
    val icon: (@Composable (Modifier, Color) -> Unit)? = null,
    val isDestructive: Boolean = false,
    val showChevron: Boolean = false,
    val subtitle: String? = null
)

/**
 * One round button of the quick action bar at the bottom of a menu.
 *
 * @property label Text under the button.
 * @property icon Icon inside the button.
 * @property onTrigger Called on tap, or after [MENU_HOLD_MS] of holding when [requiresHold] is set.
 * @property isActive Fills the button (a favorite app, for example).
 * @property isDestructive Draws the button with the error color.
 * @property requiresHold Needs a long press instead of a tap, for actions that cannot be undone.
 */
data class MenuQuickAction(
    val label: String,
    val icon: @Composable (Modifier, Color) -> Unit,
    val onTrigger: () -> Unit,
    val isActive: Boolean = false,
    val isDestructive: Boolean = false,
    val requiresHold: Boolean = false
)

/** Slots of the menu sheet that depend on the caller. */
data class MenuSheetParams(
    val titleIcon: (@Composable (Modifier) -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
    val bottomBar: (@Composable () -> Unit)? = null
)

/**
 * Orders [mostUsedFirst] from left to right so the most used item sits at the thumb side:
 * the right end for right-handed users, the left end for left-handed users.
 */
fun <T> thumbOrder(mostUsedFirst: List<T>, isLeftHanded: Boolean): List<T> =
    if (isLeftHanded) mostUsedFirst else mostUsedFirst.reversed()

/** Fraction (0 to 1) of a hold that has elapsed. */
fun holdProgress(elapsedMs: Long, holdMs: Long = MENU_HOLD_MS): Float =
    if (holdMs <= 0L) 1f else (elapsedMs.toFloat() / holdMs).coerceIn(0f, 1f)
