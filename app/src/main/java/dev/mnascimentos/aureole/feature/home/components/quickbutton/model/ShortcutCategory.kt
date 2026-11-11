package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType

/** Group of shortcuts the sheet lists at a time; the chips next to the search field switch between them. */
internal enum class ShortcutCategory(@StringRes val labelRes: Int) {
    AUREOLE(R.string.home_quickbutton_category_aureole),
    SYSTEM(R.string.home_quickbutton_category_system),
    APPS(R.string.home_quickbutton_category_apps)
}

/** Category a shortcut belongs to: an app, a system intent, or one of the launcher's own actions. */
internal fun shortcutCategoryOf(action: ShortcutAction): ShortcutCategory = when (action.type) {
    ShortcutType.OPEN_APP -> ShortcutCategory.APPS
    ShortcutType.SYSTEM_INTENT -> ShortcutCategory.SYSTEM
    else -> ShortcutCategory.AUREOLE
}

/** The [options] of [category], in their original order. [ShortcutCategory.APPS] has none: apps are listed apart. */
internal fun shortcutsInCategory(options: List<ShortcutAction>, category: ShortcutCategory): List<ShortcutAction> =
    options.filter { shortcutCategoryOf(it) == category }

/** Name shown for [action]: the label of the app for "open app", the display name of the shortcut otherwise. */
internal fun shortcutLabel(action: ShortcutAction, apps: List<AppInfo>): String =
    if (action.type == ShortcutType.OPEN_APP) {
        apps.find { it.packageName == action.target }?.label ?: action.displayName
    } else {
        action.displayName
    }

/** Text of the preview pill ("On tap: %1$s") for the gesture being edited; unknown gestures get a neutral text. */
@StringRes
internal fun shortcutGestureLabelRes(editingType: String?): Int = when (editingType) {
    "click" -> R.string.home_quickbutton_gesture_click
    "press" -> R.string.home_quickbutton_gesture_press
    "swipeUp" -> R.string.home_quickbutton_gesture_swipe_up
    "swipeLeft" -> R.string.home_quickbutton_gesture_swipe_left
    "swipeRight" -> R.string.home_quickbutton_gesture_swipe_right
    else -> R.string.home_quickbutton_gesture_generic
}
