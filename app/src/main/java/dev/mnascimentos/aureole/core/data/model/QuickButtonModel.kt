package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import dev.mnascimentos.aureole.R

@Keep
enum class QuickButtonIcon(@DrawableRes val iconRes: Int) {
    ADD(R.drawable.ic_aureole_add),
    ARROW_FORWARD(R.drawable.ic_aureole_right_down),
    BUILD(R.drawable.ic_aureole_advanced),
    CHECK(R.drawable.ic_aureole_check),
    CLEAR(R.drawable.ic_aureole_close),
    CREATE(R.drawable.ic_aureole_edit),
    DATE_RANGE(R.drawable.ic_aureole_calendar),
    DONE(R.drawable.ic_aureole_round_check),
    EDIT(R.drawable.ic_aureole_edit),
    EMAIL(R.drawable.ic_aureole_text),
    FAVORITE(R.drawable.ic_aureole_smile_color),
    HOME(R.drawable.ic_aureole_home),
    INFO(R.drawable.ic_aureole_info),
    LIST(R.drawable.ic_aureole_layout),
    LOCK(R.drawable.ic_aureole_eye_off),
    MENU(R.drawable.ic_aureole_menu_icon),
    NOTIFICATIONS(R.drawable.ic_aureole_dots),
    PERSON(R.drawable.ic_aureole_app_select),
    PHONE(R.drawable.ic_aureole_click),
    PLAY_ARROW(R.drawable.ic_aureole_press),
    REFRESH(R.drawable.ic_aureole_refresh_ccw),
    SEARCH(R.drawable.ic_aureole_search),
    SETTINGS(R.drawable.ic_aureole_settings),
    SHARE(R.drawable.ic_aureole_drag_menu),
    SHOPPING_CART(R.drawable.ic_aureole_folder),
    STAR(R.drawable.ic_aureole_star),
    THUMB_UP(R.drawable.ic_aureole_smile),
    WARNING(R.drawable.ic_aureole_placeholder)
}

@Keep
data class QuickButtonAppearance(
    val backgroundColor: Int = 0xFF455A64.toInt(),
    val strokeEnabled: Boolean = false,
    val strokeColor: Int = 0xFFFFFFFF.toInt(),
    val icon: QuickButtonIcon = QuickButtonIcon.ADD,
    val iconColor: Int = 0xFFFFFFFF.toInt()
)

@Keep
enum class ShortcutType {
    NONE,
    ALL_APPS,
    ASSISTANT,
    SETTINGS,
    NOTIFICATIONS,
    OPEN_APP,
    SYSTEM_INTENT
}

@Keep
data class ShortcutAction(
    val type: ShortcutType = ShortcutType.ALL_APPS,
    val target: String = ""
) {
    val displayName: String
        get() = when (type) {
            ShortcutType.NONE -> "None"
            ShortcutType.ALL_APPS -> "All apps"
            ShortcutType.ASSISTANT -> "Assistant"
            ShortcutType.SETTINGS -> "Aureole Settings"
            ShortcutType.NOTIFICATIONS -> "Notifications"
            ShortcutType.OPEN_APP -> "Open App"
            ShortcutType.SYSTEM_INTENT -> target.ifEmpty { "System Action" }
        }

    companion object {
        val None = ShortcutAction(ShortcutType.NONE)
        val AllApps = ShortcutAction(ShortcutType.ALL_APPS)
        val Assistant = ShortcutAction(ShortcutType.ASSISTANT)
        val Settings = ShortcutAction(ShortcutType.SETTINGS)
        val Notifications = ShortcutAction(ShortcutType.NOTIFICATIONS)
        fun openApp(packageName: String) = ShortcutAction(ShortcutType.OPEN_APP, packageName)
        fun systemIntent(intentType: String) = ShortcutAction(ShortcutType.SYSTEM_INTENT, intentType)
    }
}

@Keep
data class QuickButtonShortcuts(
    val click: ShortcutAction = ShortcutAction.AllApps,
    val press: ShortcutAction = ShortcutAction.None,
    val swipeUp: ShortcutAction = ShortcutAction.systemIntent("Calendar"),
    val swipeLeft: ShortcutAction = ShortcutAction.Notifications,
    val swipeRight: ShortcutAction = ShortcutAction.None
)

@Keep
data class QuickButtonModel(
    val id: String = "",
    val appearance: QuickButtonAppearance = QuickButtonAppearance(),
    val shortcuts: QuickButtonShortcuts = QuickButtonShortcuts()
)
