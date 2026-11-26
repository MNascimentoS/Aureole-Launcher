package dev.mnascimentos.aureole.core.data.repository

import androidx.core.content.edit

private const val KEY_CONTAINER_TIPS_SEEN = "container_tips_seen"

/**
 * Whether the three tips of the first app & folder group were seen (grupo-apps-pastas RF-F1, D-07): set by "Skip
 * tips" or by the last tip. Once true the tips never show again, and "Add component" drops its "Start here".
 */
var SettingsRepository.isContainerTipsSeen: Boolean
    get() = prefs.getBoolean(KEY_CONTAINER_TIPS_SEEN, false)
    set(value) = prefs.edit { putBoolean(KEY_CONTAINER_TIPS_SEEN, value) }
