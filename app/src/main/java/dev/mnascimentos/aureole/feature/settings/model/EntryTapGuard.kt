package dev.mnascimentos.aureole.feature.settings.model

/** Window after a settings screen opens in which toggles ignore taps (one from the navigation must not reach them). */
const val ENTRY_TAP_GUARD_MS = 400L

/** True when a tap at [nowMs] on a screen opened at [openedAtMs] is a deliberate one. */
fun isDeliberateTap(openedAtMs: Long, nowMs: Long): Boolean = nowMs - openedAtMs >= ENTRY_TAP_GUARD_MS
