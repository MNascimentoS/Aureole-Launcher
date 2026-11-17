package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import com.google.gson.annotations.JsonAdapter
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole

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

/** Catalog key of the icon a new quick button starts with. */
internal val DEFAULT_QUICK_BUTTON_ICON_KEY: String = AureoleIconCatalog.actionKey(QuickButtonIcon.ADD)

/**
 * Look of a quick button. [icon] is a key of [AureoleIconCatalog]; the colors follow the theme or are fixed
 * ([ThemeColorRef]). [label] is the name shown on the home when [showLabel] is on, and always the TalkBack
 * description; null means the name of the click action. Old saves (enum icon name, ARGB numbers) are read by
 * the adapters in `QuickButtonJsonAdapters.kt` without changing the look.
 */
@Keep
data class QuickButtonAppearance(
    @field:JsonAdapter(BackgroundColorRefAdapter::class, nullSafe = false)
    val backgroundColor: ThemeColorRef = ThemeColorRef.Role(ThemeColorRole.VARIANT),
    val strokeEnabled: Boolean = false,
    @field:JsonAdapter(WhiteColorRefAdapter::class, nullSafe = false)
    val strokeColor: ThemeColorRef = ThemeColorRef.Fixed(0xFFFFFFFF.toInt()),
    @field:JsonAdapter(QuickButtonIconKeyAdapter::class, nullSafe = false)
    val icon: String = DEFAULT_QUICK_BUTTON_ICON_KEY,
    @field:JsonAdapter(WhiteColorRefAdapter::class, nullSafe = false)
    val iconColor: ThemeColorRef = ThemeColorRef.Fixed(0xFFFFFFFF.toInt()),
    val label: String? = null,
    val showLabel: Boolean = false
) {
    /** Drawable of [icon]; the default icon when the key is unknown. */
    val iconRes: Int
        get() = AureoleIconCatalog.resFor(icon) ?: QuickButtonIcon.ADD.iconRes
}

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
