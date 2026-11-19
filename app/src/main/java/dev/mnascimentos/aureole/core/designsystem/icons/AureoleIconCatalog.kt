package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon

/** Categories of the [AureoleIconCatalog], in display order: the actions first, then the folder ones. */
enum class AureoleIconCategory(@StringRes val label: Int) {
    ACTIONS(R.string.folder_icon_cat_actions),
    WORK(R.string.folder_icon_cat_work),
    SOCIAL(R.string.folder_icon_cat_social),
    MEDIA(R.string.folder_icon_cat_media),
    GAMES(R.string.folder_icon_cat_games),
    MONEY(R.string.folder_icon_cat_money),
    HEALTH(R.string.folder_icon_cat_health),
    TRAVEL(R.string.folder_icon_cat_travel),
    TOOLS(R.string.folder_icon_cat_tools),
}

/** One icon of the catalog: a stable [key] (the one that gets saved), its [category] and its drawable. */
data class AureoleIconEntry(
    val key: String,
    val category: AureoleIconCategory,
    @DrawableRes val res: Int
)

/**
 * The single icon catalog shared by the folder form and the quick button appearance (pasta-form D-04): the 80
 * folder icons plus the quick button ones in the [AureoleIconCategory.ACTIONS] category.
 *
 * Keys are stable: folder icons keep their old key (`work`, `lock`...), quick button icons are `action_<name>`
 * (`action_add`). [resolveKey] maps every key ever saved (folder keys, [QuickButtonIcon] enum names) to the
 * current one. Each drawable appears once, so [QuickButtonIcon.CREATE] and [QuickButtonIcon.EDIT] share
 * `action_edit`.
 */
object AureoleIconCatalog {
    private const val ACTION_PREFIX = "action_"

    /** Key of the icon used when a saved key resolves to nothing. */
    const val FALLBACK_KEY = "folder"

    /** Every icon, grouped by category in [AureoleIconCategory] order. */
    val entries: List<AureoleIconEntry> = buildEntries()

    val categories: List<AureoleIconCategory> = AureoleIconCategory.entries

    private val byKey: Map<String, AureoleIconEntry> = entries.associateBy { it.key }

    /** Old folder keys that had no drawable of their own and now point to an action icon. */
    private val legacyFolderKeys: Map<String, String> = mapOf(
        "edit" to actionKey(QuickButtonIcon.EDIT),
        "create" to actionKey(QuickButtonIcon.EDIT),
        "check" to actionKey(QuickButtonIcon.CHECK),
        "add" to actionKey(QuickButtonIcon.ADD),
        "search" to actionKey(QuickButtonIcon.SEARCH),
        "refresh" to actionKey(QuickButtonIcon.REFRESH),
        "menu" to actionKey(QuickButtonIcon.MENU),
        "more_vert" to actionKey(QuickButtonIcon.NOTIFICATIONS),
        "place" to "location_on",
        "date_range" to "calendar_month",
        "play" to "play_circle",
        "cart" to "shopping_cart",
    )

    /** Stable key of a quick button icon. */
    fun actionKey(icon: QuickButtonIcon): String =
        ACTION_PREFIX + (if (icon == QuickButtonIcon.CREATE) QuickButtonIcon.EDIT else icon).name.lowercase()

    /**
     * Current key for a saved one, or null when it is unknown. Accepts a catalog key, an old folder key, a
     * [QuickButtonIcon] enum name (`DATE_RANGE`, upper case as it was persisted) and the old keys that were
     * remapped. A lower case key is looked up as a folder key first, so `date_range` stays the calendar icon.
     */
    fun resolveKey(raw: String?): String? {
        val trimmed = raw?.trim().orEmpty()
        // An exact upper case enum name is a persisted quick button icon: "FAVORITE" is not the folder key "favorite".
        val action = QuickButtonIcon.entries.firstOrNull { it.name == trimmed }
        val normalized = trimmed.lowercase().replace(' ', '_')
        return when {
            trimmed.isEmpty() -> null
            action != null -> actionKey(action)
            normalized in byKey -> normalized
            else -> legacyFolderKeys[normalized]
        }
    }

    fun entryFor(raw: String?): AureoleIconEntry? = resolveKey(raw)?.let(byKey::get)

    fun resFor(raw: String?): Int? = entryFor(raw)?.res

    fun categoryOf(raw: String?): AureoleIconCategory? = entryFor(raw)?.category

    fun byCategory(category: AureoleIconCategory): List<AureoleIconEntry> = entries.filter { it.category == category }

    private fun buildEntries(): List<AureoleIconEntry> {
        val actions = QuickButtonIcon.entries
            .filter { it != QuickButtonIcon.CREATE }
            .map { AureoleIconEntry(actionKey(it), AureoleIconCategory.ACTIONS, it.iconRes) }
        val folders = AureoleFolderIcons.all.map {
            AureoleIconEntry(it.key, AureoleIconCategory.valueOf(it.category.name), it.res)
        }
        return actions + folders
    }
}
