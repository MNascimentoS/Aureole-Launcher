package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.search.rankByQuery

/** Default shortcuts (or only apps, while picking an app) followed by every app as an "open app" shortcut. */
internal fun buildShortcutSearchCandidates(
    defaultOptions: List<ShortcutAction>,
    apps: List<AppInfo>,
    onlyApps: Boolean
): List<ShortcutSearchCandidate> {
    val defaults = if (onlyApps) {
        emptyList()
    } else {
        defaultOptions.map { ShortcutSearchCandidate(action = it, label = it.displayName) }
    }
    val appCandidates = apps.map {
        ShortcutSearchCandidate(action = ShortcutAction.openApp(it.packageName), label = it.label, app = it)
    }
    return defaults + appCandidates
}

/** Candidates that match [query], best first. A blank query gives an empty list: there is no search. */
internal fun searchShortcuts(
    candidates: List<ShortcutSearchCandidate>,
    query: String
): List<ShortcutSearchCandidate> {
    if (query.isBlank()) return emptyList()
    return rankByQuery(candidates, query) { it.label }
}

/** The action that Enter picks: the best result, or null when nothing matches. */
internal fun bestShortcutAction(results: List<ShortcutSearchCandidate>): ShortcutAction? =
    results.firstOrNull()?.action
