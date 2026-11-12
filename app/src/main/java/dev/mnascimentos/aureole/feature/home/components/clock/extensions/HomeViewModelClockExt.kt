package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.updateClockStyle(style: String) = editClock { it.copy(style = style) }

fun HomeViewModel.updateClockCustomGreeting(greeting: String) = editClock { it.copy(customGreeting = greeting) }

fun HomeViewModel.updateClockAlignment(alignment: String) = editClock { it.copy(alignment = alignment) }

fun HomeViewModel.updateClockFontFamily(fontFamily: String) = editClock { it.copy(fontFamily = fontFamily) }

fun HomeViewModel.updateClockTimeFormat(format: String) = editClock { it.copy(timeFormat = format) }

fun HomeViewModel.updateClockDateFormat(format: String) = editClock { it.copy(dateFormat = format) }

fun HomeViewModel.updateClockTextColor(color: Int) = editClock { it.copy(textColor = color) }

fun HomeViewModel.updateClockBackgroundColor(color: Int) = editClock { it.copy(backgroundColor = color) }

fun HomeViewModel.toggleClockBackground() = editClock { it.copy(isBackgroundEnabled = !it.isBackgroundEnabled) }
