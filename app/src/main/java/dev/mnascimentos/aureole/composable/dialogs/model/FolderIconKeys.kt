package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleFolderIcons

/** Key used when the saved key is null or unknown. */
const val FOLDER_ICON_FALLBACK = "folder"

/** Old keys that have no drawable in the catalog; they still draw with the design system icons. */
val LEGACY_FOLDER_ICON_KEYS: Set<String> = setOf(
    "edit",
    "check",
    "add",
    "delete",
    "search",
    "refresh",
    "menu",
    "more_vert"
)

/** Old keys that now point to a catalog icon (several of them used to fall back to a wrong icon). */
private val legacyRemap: Map<String, String> = mapOf(
    "place" to "location_on",
    "date_range" to "calendar_month",
    "play" to "play_circle",
    "cart" to "shopping_cart",
    "create" to "edit"
)

/** Folder names that were accepted as an icon key. */
private val nameAliases: Map<String, String> = mapOf(
    "games" to "sports_esports",
    "jogos" to "sports_esports",
    "media" to "play_circle",
    "fun" to "thumb_up",
    "diversao" to "thumb_up",
    "ai" to "star",
    "ias" to "star",
    "banking" to "account_balance",
    "banco" to "account_balance",
    "shopping" to "shopping_cart",
    "tools" to "build",
    "ferramentas" to "build",
    "security" to "lock",
    "seguranca" to "lock",
    "work" to "work",
    "trabalho" to "work",
    "social" to "group"
)

/**
 * Key that is really drawn for the saved [name]: a catalog key, an old key without drawable, or
 * [FOLDER_ICON_FALLBACK] when [name] is null or unknown. Saved folders keep their old keys.
 */
fun resolveFolderIconKey(name: String?): String {
    val lower = name?.lowercase()?.trim()?.replace(' ', '_').orEmpty()
    // A catalog key always wins: "security" is a catalog icon of its own, not an alias of "lock".
    val key = lower.takeIf(::isKnownFolderIconKey) ?: nameAliases[lower] ?: lower
    val mapped = legacyRemap[key] ?: key
    return mapped.takeIf { AureoleFolderIcons.resFor(it) != null || it in LEGACY_FOLDER_ICON_KEYS }
        ?: FOLDER_ICON_FALLBACK
}

private fun isKnownFolderIconKey(key: String): Boolean =
    AureoleFolderIcons.resFor(key) != null || key in LEGACY_FOLDER_ICON_KEYS
