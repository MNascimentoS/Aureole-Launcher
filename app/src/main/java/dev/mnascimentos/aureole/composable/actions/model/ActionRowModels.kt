package dev.mnascimentos.aureole.composable.actions.model

/** Weight of the secondary (outlined) button of an action row. */
const val ACTION_SECONDARY_WEIGHT = 1f

/** Weight of the primary (filled) button of an action row. */
const val ACTION_PRIMARY_WEIGHT = 1.4f

/** How long the user has to hold a button that confirms something that cannot be undone (reset launcher). */
const val HOLD_CONFIRM_MS = 1500L

/** The two buttons of an action row. */
enum class ActionSlot { SECONDARY, PRIMARY }

/**
 * Order of the buttons from left to right: the secondary one on the left and the primary on the right,
 * mirrored for the left-handed mode so the primary action sits under the thumb.
 */
fun actionRowOrder(isLeftHanded: Boolean): List<ActionSlot> {
    val order = listOf(ActionSlot.SECONDARY, ActionSlot.PRIMARY)
    return if (isLeftHanded) order.reversed() else order
}

/**
 * Labels and callbacks of an action row.
 *
 * @property primaryLabel Text of the filled button (Save, Apply).
 * @property onPrimary Called when the filled button is tapped.
 * @property secondaryLabel Text of the outlined button (Cancel, Restore).
 * @property onSecondary Called when the outlined button is tapped.
 * @property isSecondaryDestructive Draws the outlined button with the error color.
 * @property primaryHoldMs When set, the primary button is the error colored hold button: it fires only after
 * being held for this long.
 */
data class ActionRowParams(
    val primaryLabel: String,
    val onPrimary: () -> Unit,
    val secondaryLabel: String,
    val onSecondary: () -> Unit,
    val isSecondaryDestructive: Boolean = false,
    val primaryHoldMs: Long? = null
)
