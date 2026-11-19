package dev.mnascimentos.aureole.feature.home.grid.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

/**
 * Whether the size of an item inside a scroll group can be adjusted. The apps list cannot: it shows the whole
 * list (RF-C6), so its saved span is ignored, never migrated nor erased (RF-C8). Every other item keeps its span.
 */
internal fun LauncherItemState.isResizable(): Boolean = safeType != LauncherItemType.APPS_LIST

/**
 * The span the size control of this item shows and edits in the group's orientation: the row span when it scrolls
 * vertically (its height), the column span when it scrolls sideways (its width). Null when it is not resizable.
 */
internal fun LauncherItemState.effectiveSpan(isVertical: Boolean): Int? = when {
    !isResizable() -> null
    isVertical -> rowSpan
    else -> colSpan
}
