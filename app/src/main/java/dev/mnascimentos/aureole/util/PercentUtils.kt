package dev.mnascimentos.aureole.util

private const val PERCENT_BASE = 100f

/** Converts a 0-100 percentage into a 0f-1f fraction. */
fun Int.percentToFraction(): Float = this / PERCENT_BASE
