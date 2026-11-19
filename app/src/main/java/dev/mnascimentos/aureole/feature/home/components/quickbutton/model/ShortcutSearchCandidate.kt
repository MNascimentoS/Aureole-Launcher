package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction

/** A shortcut that can show up in the search results: a default shortcut ([app] is null) or an app. */
internal data class ShortcutSearchCandidate(
    val action: ShortcutAction,
    val label: String,
    val app: AppInfo? = null,
    /** False for a saved system shortcut that no app on the device answers: shown, marked, never offered. */
    val isAvailable: Boolean = true
)
