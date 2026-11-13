package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

/**
 * Reading a layout saved by 0.9.30 or older, before the apps list had the "show all apps" toggle, keeping the home
 * as it was: the all apps list becomes an apps list with the toggle on, and a favorites list (no toggle saved)
 * becomes an apps list with it off, so it still shows only its favorites. Same id, so the favorites stay. Runs on
 * the children of scroll views too. A layout saved by this version always states the toggle
 * ([withExplicitShowAllApps]), so a missing one can only come from an older version.
 */
fun LauncherItemState.migrateLegacyAppsList(): LauncherItemState {
    val migrated = when {
        type == LauncherItemType.ALL_APPS_LIST -> copy(type = LauncherItemType.APPS_LIST, showAllApps = true)
        safeType == LauncherItemType.APPS_LIST && showAllApps == null -> copy(showAllApps = false)
        else -> this
    }
    val children = migrated.children ?: return migrated
    return migrated.copy(children = children.map { it.migrateLegacyAppsList() })
}

/**
 * The form a layout is saved in: every apps list states its toggle, with the value the home shows now (a list
 * created in this version has it on), and no all apps list is left. Runs on the children of scroll views too.
 */
fun LauncherItemState.withExplicitShowAllApps(): LauncherItemState {
    val explicit = when {
        type == LauncherItemType.ALL_APPS_LIST -> copy(type = LauncherItemType.APPS_LIST, showAllApps = true)
        safeType == LauncherItemType.APPS_LIST && showAllApps == null -> copy(showAllApps = true)
        else -> this
    }
    val children = explicit.children ?: return explicit
    return explicit.copy(children = children.map { it.withExplicitShowAllApps() })
}
