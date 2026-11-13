package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

/** The items and, depth first, every child of the scroll groups among them. */
internal fun List<LauncherItemState>.flattenWithChildren(): List<LauncherItemState> =
    flatMap { listOf(it) + it.safeChildren.flattenWithChildren() }

/** The ids of the shortcut containers in the list, at any depth. */
internal fun List<LauncherItemState>.containerIds(): List<String> =
    flattenWithChildren().filter { it.safeType == LauncherItemType.SHORTCUTS_CONTAINER }.map { it.id }

/** The container ids in the list that are not in [existing] yet, each once. */
internal fun List<LauncherItemState>.missingContainerIds(existing: Set<String>): List<String> =
    containerIds().filter { it !in existing }.distinct()
