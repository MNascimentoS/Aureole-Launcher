package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.core.data.model.withMigratedFont
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.updateClockStyle(style: String) = editClock { it.copy(style = style) }

fun HomeViewModel.updateClockMessage(message: String) = editClock { it.copy(message = message) }

fun HomeViewModel.updateClockCustomGreeting(greeting: String) = editClock { it.copy(customGreeting = greeting) }

fun HomeViewModel.updateClockAlignment(alignment: String) = editClock { it.copy(alignment = alignment) }

/** Picks the clock font (a catalog key or `FONT_KEY_LAUNCHER`); an old saved font is read first, so its bold stays. */
fun HomeViewModel.updateClockFontFamily(fontFamily: String) =
    editClock { it.withMigratedFont().copy(fontFamily = fontFamily) }

/** Turns the bold of the time on or off; an old saved font is read first, so it keeps its look. */
fun HomeViewModel.updateClockFontBold(isBold: Boolean) = editClock { it.withMigratedFont().copy(isFontBold = isBold) }

fun HomeViewModel.updateClockTimeFormat(format: String) = editClock { it.copy(timeFormat = format) }

fun HomeViewModel.updateClockDateFormat(format: String) = editClock { it.copy(dateFormat = format) }

/** Picks the text color; null is "Theme default". The legacy ARGB is cleared so the ref is the only source. */
fun HomeViewModel.updateClockTextColor(color: ThemeColorRef?) =
    editClock { it.copy(textColor = 0, textColorRef = color?.toJson()) }

fun HomeViewModel.updateClockBackgroundColor(color: ThemeColorRef?) =
    editClock { it.copy(backgroundColor = 0, backgroundColorRef = color?.toJson()) }

fun HomeViewModel.toggleClockBackground() = editClock { it.copy(isBackgroundEnabled = !it.isBackgroundEnabled) }
