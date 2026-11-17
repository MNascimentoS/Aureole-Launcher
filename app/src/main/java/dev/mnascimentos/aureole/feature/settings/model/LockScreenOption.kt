package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource

/** The question asked before the lock screen switch changes. */
enum class LockScreenDialog {
    /** First time it is turned on: the system keeps no copy of the current lock screen (RF-15). */
    FIRST_TIME,

    /** Turned off: keep the lock screen as it is or go back to the system wallpaper (RF-16). */
    TURN_OFF
}

/**
 * The "Also use on lock screen" switch of the background sheet.
 *
 * @property checked Whether saved backgrounds also go to the lock screen.
 * @property isAvailable False on devices that do not let apps change the wallpaper: the switch is off and disabled.
 * @property isNoticeSeen Whether the one-time warning was already shown.
 * @property onTurnOn Called once the user accepted turning it on.
 * @property onTurnOff Called with true to give the lock screen back to the system wallpaper, false to keep it.
 */
data class LockScreenOption(
    val checked: Boolean = false,
    val isAvailable: Boolean = true,
    val isNoticeSeen: Boolean = false,
    val onTurnOn: () -> Unit = {},
    val onTurnOff: (restoreSystem: Boolean) -> Unit = {}
)

/** The question a tap on the switch opens, or null when it can change right away. */
fun lockScreenDialogFor(turnOn: Boolean, isNoticeSeen: Boolean): LockScreenDialog? = when {
    !turnOn -> LockScreenDialog.TURN_OFF
    !isNoticeSeen -> LockScreenDialog.FIRST_TIME
    else -> null
}

/** The message shown when the lock screen could not be changed; null when it worked. */
fun lockScreenErrorOf(applied: Boolean): TextResource? =
    if (applied) null else TextResource.fromResId(R.string.settings_lock_screen_error)
