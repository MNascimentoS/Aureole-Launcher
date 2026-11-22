package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.feature.home.HomeViewModel

/** Picks the text color; null is "Theme default". The legacy ARGB is cleared so the ref is the only source. */
fun HomeViewModel.updateClockTextColor(color: ThemeColorRef?) =
    editClock { it.copy(textColor = 0, textColorRef = color?.toJson()) }

fun HomeViewModel.updateClockBackgroundColor(color: ThemeColorRef?) =
    editClock { it.copy(backgroundColor = 0, backgroundColorRef = color?.toJson()) }

fun HomeViewModel.toggleClockBackground() = editClock { it.copy(isBackgroundEnabled = !it.isBackgroundEnabled) }
