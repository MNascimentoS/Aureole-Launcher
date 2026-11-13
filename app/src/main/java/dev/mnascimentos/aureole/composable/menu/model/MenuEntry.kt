package dev.mnascimentos.aureole.composable.menu.model

import androidx.compose.runtime.Composable

/** One item of a menu list, in the order the caller wants before [withDestructiveDivider] is applied. */
sealed interface MenuEntry {
    /** Destructive entries go to the end of the list, after a divider. */
    val isDestructive: Boolean get() = false

    /** A tappable row. */
    data class Row(val params: MenuRowParams) : MenuEntry {
        override val isDestructive: Boolean get() = params.isDestructive
    }

    /** Small caps label above a group of rows. */
    data class Section(val text: String) : MenuEntry

    /** Read-only centered note. */
    data class Note(val text: String) : MenuEntry

    /** Any other content, such as a switch row. Never destructive. */
    data class Content(val content: @Composable () -> Unit) : MenuEntry

    /** Thin line between two groups of rows. */
    data object Divider : MenuEntry
}

/**
 * Moves destructive entries (Remove, Remove from folder, Uninstall) to the end. Stable: the order inside each
 * group is the order of [items].
 */
fun destructiveLast(items: List<MenuEntry>): List<MenuEntry> {
    val (destructive, regular) = items.partition { it.isDestructive }
    return regular + destructive
}

/**
 * [destructiveLast] plus a [MenuEntry.Divider] right before the destructive group, unless that group is the whole
 * list or a divider already sits there.
 */
fun withDestructiveDivider(items: List<MenuEntry>): List<MenuEntry> {
    val ordered = destructiveLast(items)
    val firstDestructive = ordered.indexOfFirst { it.isDestructive }
    if (firstDestructive <= 0 || ordered[firstDestructive - 1] == MenuEntry.Divider) return ordered
    return ordered.take(firstDestructive) + MenuEntry.Divider + ordered.drop(firstDestructive)
}
