package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

/**
 * The clock look saved in the launcher settings: the one clock of 0.9.20 and older, and the look a new clock
 * starts with.
 */
fun SettingsRepository.globalClockConfig(): ClockConfig = ClockConfig(
    style = clockStyle,
    customGreeting = clockCustomGreeting,
    alignment = clockAlignment,
    fontFamily = clockFontFamily,
    timeFormat = clockTimeFormat,
    dateFormat = clockDateFormat,
    textColor = clockTextColor,
    backgroundColor = clockBackgroundColor,
    isBackgroundEnabled = isClockBackgroundEnabled
)

/** The ids of every clock in [this] layout, including the ones inside scroll views. */
internal fun List<LauncherItemState>.clockItemIds(): Set<String> = flatMap { item ->
    val own = if (item.safeType == LauncherItemType.CLOCK && item.id.isNotBlank()) listOf(item.id) else emptyList()
    own + item.safeChildren.clockItemIds()
}.toSet()

/** The ids of the clocks the portrait and the landscape homes show now (the default layout when none was saved). */
internal fun GridRepository.currentClockItemIds(isLeftHanded: Boolean): Set<String> =
    getGridItems(isLandscape = false, isLeftHanded = isLeftHanded).clockItemIds() +
        getGridItems(isLandscape = true, isLeftHanded = isLeftHanded).clockItemIds()
