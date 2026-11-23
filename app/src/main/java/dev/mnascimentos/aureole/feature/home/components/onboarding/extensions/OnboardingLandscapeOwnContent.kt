package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.extensions.flattenWithChildren
import java.util.UUID

/** The components whose content lives under their id: the landscape copy gets its own, empty (D-17). */
private val OWN_CONTENT_TYPES = setOf(
    LauncherItemType.SHORTCUTS_CONTAINER,
    LauncherItemType.WIDGET_LIST,
    LauncherItemType.SCROLL_VIEW,
    LauncherItemType.SINGLE_APP_WIDGET
)

internal fun randomItemId(): String = UUID.randomUUID().toString()

/**
 * The landscape copy of the onboarding (RF-L2, RF-L3): the container, widget group, scrolling group and widget get a
 * new id, never one of [portraitItems] or of this list, and come empty (no apps, no widgets, no children, no
 * `appWidgetId`), so editing or removing them in one orientation leaves the other alone. Their storage (the
 * container in Room, `widget_ids_stack_<id>`) starts empty under the new id. The clock, apps list, quick button
 * and the rest keep their id and content, as before. Position and size never change.
 */
internal fun List<LauncherItemState>.withOwnLandscapeContent(
    portraitItems: List<LauncherItemState>,
    newId: () -> String = ::randomItemId
): List<LauncherItemState> {
    val taken = (portraitItems + this).flattenWithChildren().map { it.id }.toMutableSet()
    fun freshId(): String {
        var id = newId()
        while (!taken.add(id)) id = newId()
        return id
    }
    return map { item ->
        if (item.safeType in OWN_CONTENT_TYPES) {
            item.copy(id = freshId(), widgetId = null, children = emptyList())
        } else {
            item
        }
    }
}
